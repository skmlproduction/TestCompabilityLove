package dev.lovetest.app.monetization

/** Parses `lovetest.billing.product.ids` / BuildConfig comma list. */
internal fun parseBillingProductIds(raw: String): List<String> =
    raw.split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
