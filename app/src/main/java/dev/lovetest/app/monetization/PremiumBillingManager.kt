package dev.lovetest.app.monetization

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult as PlayBillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dev.lovetest.app.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface PremiumPurchaseOutcome {
    data object Success : PremiumPurchaseOutcome
    data object Cancelled : PremiumPurchaseOutcome
    data object NotConfigured : PremiumPurchaseOutcome
    data class Error(val message: String) : PremiumPurchaseOutcome
}

class PremiumBillingManager(
    context: Context,
) {
    private val appContext = context.applicationContext

    private val productIds: List<String> = parseBillingProductIds(BuildConfig.BILLING_PRODUCT_IDS)

    fun isConfigured(): Boolean = productIds.isNotEmpty()

    private var purchaseContinuation: ((PremiumPurchaseOutcome) -> Unit)? = null
    @Volatile
    private var destroyed = false

    private val purchasesListener = PurchasesUpdatedListener { result, purchases ->
        val cont = purchaseContinuation ?: return@PurchasesUpdatedListener
        purchaseContinuation = null
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val owned = ownedPurchases(purchases)
                if (owned.isEmpty()) {
                    cont(PremiumPurchaseOutcome.Error("purchase_not_granted"))
                } else {
                    acknowledgeAll(owned) { ok ->
                        cont(
                            if (ok) PremiumPurchaseOutcome.Success
                            else PremiumPurchaseOutcome.Error("acknowledge_failed"),
                        )
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> cont(PremiumPurchaseOutcome.Cancelled)
            else -> cont(PremiumPurchaseOutcome.Error(result.debugMessage.ifBlank { "billing_error" }))
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(purchasesListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
        )
        .build()

    suspend fun connect(): Boolean {
        if (destroyed || !isConfigured()) return false
        return suspendCancellableCoroutine { cont ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: PlayBillingResult) {
                    cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
                }

                override fun onBillingServiceDisconnected() {
                    if (cont.isActive) cont.resume(false)
                }
            })
        }
    }

    suspend fun purchase(activity: Activity): PremiumPurchaseOutcome {
        if (destroyed) return PremiumPurchaseOutcome.Error("billing_destroyed")
        if (!isConfigured()) return PremiumPurchaseOutcome.NotConfigured
        if (!connect()) return PremiumPurchaseOutcome.Error("billing_unavailable")

        val productId = productIds.first()
        val details = queryProductDetails(productId)
            ?: return PremiumPurchaseOutcome.Error("product_not_found")

        return suspendCancellableCoroutine { cont ->
            purchaseContinuation = { outcome -> cont.resume(outcome) }
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()
            val launchResult = billingClient.launchBillingFlow(activity, flowParams)
            if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                purchaseContinuation = null
                cont.resume(PremiumPurchaseOutcome.Error(launchResult.debugMessage.ifBlank { "launch_failed" }))
            }
        }
    }

    suspend fun restorePurchases(): Boolean {
        when (val ownership = queryPremiumOwnership()) {
            true -> return true
            false -> return false
            null -> return false
        }
    }

    /**
     * @return true if premium is owned, false if not, null if billing could not be queried.
     */
    suspend fun queryPremiumOwnership(): Boolean? {
        if (destroyed || !isConfigured()) return null
        if (!connect()) return null
        return suspendCancellableCoroutine { cont ->
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            ) { result, purchases ->
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    cont.resume(null)
                    return@queryPurchasesAsync
                }
                val owned = ownedPurchases(purchases)
                if (owned.isEmpty()) {
                    cont.resume(false)
                    return@queryPurchasesAsync
                }
                acknowledgeAll(owned) { ok ->
                    cont.resume(if (ok) true else null)
                }
            }
        }
    }

    fun destroy() {
        if (destroyed) return
        destroyed = true
        purchaseContinuation?.invoke(PremiumPurchaseOutcome.Cancelled)
        purchaseContinuation = null
        billingClient.endConnection()
    }

    private fun ownedPurchases(purchases: List<Purchase>?): List<Purchase> =
        purchases.orEmpty().filter { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any { it in productIds }
        }

    private fun acknowledgeAll(purchases: List<Purchase>, onDone: (Boolean) -> Unit) {
        acknowledgeAt(purchases, 0, onDone)
    }

    private fun acknowledgeAt(purchases: List<Purchase>, index: Int, onDone: (Boolean) -> Unit) {
        if (index >= purchases.size) {
            onDone(true)
            return
        }
        val purchase = purchases[index]
        if (purchase.isAcknowledged) {
            acknowledgeAt(purchases, index + 1, onDone)
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                acknowledgeAt(purchases, index + 1, onDone)
            } else {
                onDone(false)
            }
        }
    }

    private suspend fun queryProductDetails(productId: String): ProductDetails? =
        suspendCancellableCoroutine { cont ->
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    ),
                )
                .build()
            billingClient.queryProductDetailsAsync(params) { result, response ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    cont.resume(response.productDetailsList.firstOrNull())
                } else {
                    cont.resume(null)
                }
            }
        }
}
