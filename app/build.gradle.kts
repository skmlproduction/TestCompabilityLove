import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.cleveradssolutions.gradle-plugin") version "4.8.0"
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// CAS.AI mediation (курс владельца 2026-09-22): Optimal Ads Solutions + Advertising ID.
// CAS ID = applicationId (dev.lovetest.app), регистрация приложения — в дашборде cas.ai.
// ProGuard-правила встроены в AAR адаптеров; consent manager встроен в SDK.
cas {
    includeOptimalAds = true
    useAdvertisingId = true
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
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.lovetest.app"
        minSdk = 26
        targetSdk = 36
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
            // Решение владельца 2026-09-22: без R8/обфускации — быстрые и простые сборки.
            isMinifyEnabled = false
            isShrinkResources = false
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
    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
    lint {
        // Меньше параллелизма анализа — снижает риск JBR C1 crash на Apple Silicon (16 GB).
        checkReleaseBuilds = false
        abortOnError = true
        warningsAsErrors = false
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.android.billing.ktx)
    // Google UMP: форма «Privacy options» в настройках. Consent manager CAS построен на UMP.
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
