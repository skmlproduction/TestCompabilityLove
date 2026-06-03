import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

private fun Project.lovetestPrivacyPolicyUrl(): String {
    val fromProp = findProperty("lovetest.privacy.policy.url") as String?
    return fromProp?.trim().orEmpty()
}

private fun Project.lovetestBillingProductIds(): String {
    val fromProp = findProperty("lovetest.billing.product.ids") as String?
    return fromProp?.trim().orEmpty()
}

private fun Project.lovetestAdsEnabled(): Boolean {
    val fromProp = findProperty("lovetest.ads.enabled") as String?
    return fromProp?.trim()?.equals("true", ignoreCase = true) == true
}

private fun Project.lovetestAdMobAppId(): String {
    val fromProp = findProperty("lovetest.admob.app.id") as String?
    return fromProp?.trim()?.takeIf { it.isNotEmpty() }
        ?: "ca-app-pub-3940256099942544~3347511713"
}

private fun Project.lovetestAdMobInterstitialUnitId(): String {
    val fromProp = findProperty("lovetest.admob.interstitial.unit.id") as String?
    return fromProp?.trim()?.takeIf { it.isNotEmpty() }
        ?: "ca-app-pub-3940256099942544/1033173712"
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.isFile) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val releaseStoreFilePath = keystoreProperties.getProperty("storeFile")?.trim().orEmpty()
val hasReleaseKeystore = keystorePropertiesFile.isFile &&
    releaseStoreFilePath.isNotBlank() &&
    rootProject.file(releaseStoreFilePath).isFile

android {
    namespace = "dev.lovetest.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.lovetest.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "PRIVACY_POLICY_URL",
            "\"${project.lovetestPrivacyPolicyUrl().replace("\\", "\\\\").replace("\"", "\\\"")}\"",
        )
        buildConfigField(
            "String",
            "BILLING_PRODUCT_IDS",
            "\"${project.lovetestBillingProductIds().replace("\\", "\\\\").replace("\"", "\\\"")}\"",
        )
        buildConfigField("boolean", "ADS_ENABLED", "${project.lovetestAdsEnabled()}")
        buildConfigField(
            "String",
            "ADMOB_INTERSTITIAL_UNIT_ID",
            "\"${project.lovetestAdMobInterstitialUnitId().replace("\\", "\\\\").replace("\"", "\\\"")}\"",
        )
        manifestPlaceholders["admobAppId"] = project.lovetestAdMobAppId()
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets {
        getByName("main") {
            if (project.lovetestAdsEnabled()) {
                manifest.srcFile("src/ads/AndroidManifest.xml")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.android.billing.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
