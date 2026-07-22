plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.register<Exec>("verifyUiInventory") {
    group = "verification"
    description =
        "Сверка Routes ↔ NavHost, манифеста, strings RU/EN и PNG из screens_catalog.csv."
    workingDir(rootDir)
    commandLine("python3", file("scripts/verify_ui_inventory.py").absolutePath)
}

tasks.register<Exec>("verifyTestInventoryLoveTest") {
    group = "verification"
    description = "Gate: минимум unit / Compose UI tests (без эмулятора)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/verify_test_inventory.sh").absolutePath)
}

tasks.register<Exec>("countTestsLoveTest") {
    group = "verification"
    description = "Подсчёт unit / instrumented tests (Compose UI + route smoke) в :app."
    workingDir(rootDir)
    commandLine("bash", file("scripts/count_tests.sh").absolutePath)
}

tasks.register<Exec>("projectHealthLoveTest") {
    group = "verification"
    description = "Быстрая сводка: tests + Play checklist + next step (без Gradle build)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/project_health.sh").absolutePath)
}

tasks.register<Exec>("onboardReleaseLoveTest") {
    group = "verification"
    description = "Первичная настройка release: store config + git hints + project health."
    workingDir(rootDir)
    val user = (findProperty("githubUser") as String?)?.trim().orEmpty()
    val repo = (findProperty("githubRepo") as String?)?.trim().orEmpty()
    if (user.isNotEmpty() && repo.isNotEmpty()) {
        commandLine(
            "bash",
            file("scripts/onboard_release.sh").absolutePath,
            user,
            repo,
        )
    } else {
        commandLine("bash", file("scripts/onboard_release.sh").absolutePath)
    }
}

tasks.register<Exec>("suggestFirstCommitLoveTest") {
    group = "verification"
    description = "Dry-run первого commit: validate staging + preview paths."
    workingDir(rootDir)
    commandLine("bash", file("scripts/suggest_first_commit.sh").absolutePath)
}

tasks.register<Exec>("postPushLoveTest") {
    group = "verification"
    description = "После git push: Pages, privacy, keystore, upload hints."
    workingDir(rootDir)
    val url = (findProperty("privacyUrl") as String?)?.trim().orEmpty()
    if (url.isNotEmpty()) {
        commandLine(
            "bash",
            file("scripts/post_push.sh").absolutePath,
            url,
        )
    } else {
        commandLine("bash", file("scripts/post_push.sh").absolutePath)
    }
}

tasks.register<Exec>("firstPushLoveTest") {
    group = "verification"
    description = "Pipeline до первого push: remote + suggest + prepare (без commit)."
    workingDir(rootDir)
    val user = (findProperty("githubUser") as String?)?.trim().orEmpty()
    val repo = (findProperty("githubRepo") as String?)?.trim().orEmpty()
    if (user.isEmpty() || repo.isEmpty()) {
        throw GradleException("Укажите -PgithubUser=USER -PgithubRepo=REPO")
    }
    commandLine(
        "bash",
        file("scripts/first_push.sh").absolutePath,
        user,
        repo,
    )
}

tasks.register<Exec>("checkPrLoveTest") {
    group = "verification"
    description = "Локальная проверка как CI: audit + staging + verifyLoveTest."
    workingDir(rootDir)
    commandLine("bash", file("scripts/check_pr.sh").absolutePath)
}

tasks.register<Exec>("validateGitStagingLoveTest") {
    group = "verification"
    description = "Проверка git add --dry-run: нет build/, secrets, decompile."
    workingDir(rootDir)
    commandLine("bash", file("scripts/validate_git_staging_cached.sh").absolutePath)
}

tasks.register("verifyLoveTestJvm") {
    group = "verification"
    description = "Unit-тесты :core:domain (расчёт совместимости)."
    dependsOn(":core:domain:test")
}

tasks.register("verifyLoveTestAndroid") {
    group = "verification"
    description = "Unit-тесты app, compileDebug и lintDebug."
    dependsOn(
        ":app:testDebugUnitTest",
        ":app:compileDebugKotlin",
        ":app:lintDebug",
    )
}

tasks.register("verifyLoveTest") {
    group = "verification"
    description = "Полная проверка: JVM + Android + UI inventory + test inventory."
    dependsOn("verifyLoveTestJvm", "verifyLoveTestAndroid", "verifyUiInventory", "verifyTestInventoryLoveTest")
}

tasks.register("verifyLoveTestRelease") {
    group = "verification"
    description = "verifyLoveTest + assembleRelease + bundleRelease."
    dependsOn("verifyLoveTest", ":app:assembleRelease", ":app:bundleRelease")
}

tasks.register<Exec>("materializeScreenshotPlaceholders") {
    group = "screenshots"
    description = "Создать placeholder PNG 1080×1920 из screens_catalog.csv."
    workingDir(rootDir)
    commandLine("python3", file("scripts/write_screenshot_placeholders.py").absolutePath)
}

tasks.register<Exec>("captureScreenshotCatalogRu") {
    group = "screenshots"
    description = "Съёмка docs/screenshots/ru/*.png (adb, debug APK)."
    workingDir(rootDir)
    dependsOn(":app:installDebug")
    commandLine("bash", file("scripts/capture_screenshot_catalog.sh").absolutePath, "ru")
}

tasks.register<Exec>("captureScreenshotCatalogEn") {
    group = "screenshots"
    description = "Съёмка docs/screenshots/en/*.png (adb, locale en-US)."
    workingDir(rootDir)
    dependsOn(":app:installDebug")
    commandLine("bash", file("scripts/capture_screenshot_catalog.sh").absolutePath, "en")
}

tasks.register<Exec>("auditLoveTestScreens") {
    group = "verification"
    description = "F6: матрица CSV ↔ SVG ↔ Kotlin ↔ PNG, отчёт AUDIT_REPORT.md."
    workingDir(rootDir)
    commandLine(
        "python3",
        file("scripts/audit_screens_matrix.py").absolutePath,
        "--write",
        "docs/product/AUDIT_REPORT.md",
    )
}

tasks.register<Exec>("storeReadyLoveTest") {
    group = "verification"
    description = "preflight + verifyLoveTest (без gate placeholder PNG)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/store_ready.sh").absolutePath)
}

tasks.register<Exec>("preflightLoveTestStore") {
    group = "verification"
    description = "Чеклист перед Play (privacy, placeholders, adb) без полной сборки."
    workingDir(rootDir)
    commandLine("bash", file("scripts/preflight_store.sh").absolutePath)
}

tasks.register<Exec>("captureReadinessLoveTest") {
    group = "screenshots"
    description = "DEBUG_UI_PREVIEW + PNG stats + adb (без съёмки)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/capture_readiness.sh").absolutePath)
}

tasks.register<Exec>("capturePriorityScreensLoveTest") {
    group = "screenshots"
    description = "Съёмка 7 приоритетных экранов для листинга Play (adb)."
    workingDir(rootDir)
    dependsOn(":app:installDebug")
    val locale = (findProperty("locale") as String?)?.trim()?.ifEmpty { null } ?: "ru"
    commandLine("bash", file("scripts/capture_priority_screens.sh").absolutePath, locale)
}

tasks.register<Exec>("printStoreChecklistLoveTest") {
    group = "verification"
    description = "Сводный чеклист Play Console с текущим статусом."
    workingDir(rootDir)
    commandLine("bash", file("scripts/print_store_checklist.sh").absolutePath)
}

tasks.register<Exec>("packStoreUploadLoveTest") {
    group = "verification"
    description = "Собрать AAB, mapping, feature graphic в build/store-upload/."
    workingDir(rootDir)
    commandLine("bash", file("scripts/pack_store_upload.sh").absolutePath)
}

tasks.register<Exec>("validateStoreUploadLoveTest") {
    group = "verification"
    description = "Проверить build/store-upload/ перед Play."
    workingDir(rootDir)
    commandLine("bash", file("scripts/validate_store_upload.sh").absolutePath)
}

tasks.register<Exec>("finalizeStoreReleaseLoveTest") {
    group = "verification"
    description = "verify + PNG gate + audit + pack + validate (финальный gate Play)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/finalize_store_release.sh").absolutePath)
}

tasks.register<Exec>("zipStoreUploadLoveTest") {
    group = "verification"
    description = "ZIP build/store-upload/ → build/love-tester-store-upload.zip"
    workingDir(rootDir)
    dependsOn("packStoreUploadLoveTest")
    commandLine("bash", file("scripts/zip_store_upload.sh").absolutePath)
}

tasks.register<Exec>("postPrivacySetupLoveTest") {
    group = "verification"
    description = "После деплоя privacy: set URL + check + bundle + pack."
    workingDir(rootDir)
    val url = (findProperty("privacyUrl") as String?)?.trim().orEmpty()
    if (url.isEmpty()) {
        throw GradleException("Укажите -PprivacyUrl=https://your-domain/")
    }
    commandLine(
        "bash",
        file("scripts/post_privacy_setup.sh").absolutePath,
        url,
    )
}

tasks.register<Exec>("initGitForGithubLoveTest") {
    group = "verification"
    description = "git init + подсказки GitHub Pages (без commit)."
    workingDir(rootDir)
    val user = (findProperty("githubUser") as String?)?.trim().orEmpty()
    val repo = (findProperty("githubRepo") as String?)?.trim().orEmpty()
    if (user.isNotEmpty() && repo.isNotEmpty()) {
        commandLine(
            "bash",
            file("scripts/init_git_for_github.sh").absolutePath,
            user,
            repo,
        )
    } else {
        commandLine("bash", file("scripts/init_git_for_github.sh").absolutePath)
    }
}

tasks.register<Exec>("playConsoleNextLoveTest") {
    group = "verification"
    description = "Один следующий шаг до Play Console по текущему состоянию."
    workingDir(rootDir)
    commandLine("bash", file("scripts/play_console_next.sh").absolutePath)
}

tasks.register<Exec>("verifyAdsBuildLoveTest") {
    group = "verification"
    description = "Smoke-compile debug with lovetest.ads.enabled=true (AdMob + AD_ID manifest)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/verify_ads_build.sh").absolutePath)
}

tasks.register<Exec>("servePrivacyPreviewLoveTest") {
    group = "verification"
    description = "Локальный HTTP превью build/legal-host/ (не для Play)."
    workingDir(rootDir)
    val port = (findProperty("port") as String?)?.trim()?.ifEmpty { null } ?: "8765"
    commandLine("bash", file("scripts/serve_privacy_preview.sh").absolutePath, port)
}

tasks.register<Exec>("checkPrivacyUrlLoveTest") {
    group = "verification"
    description = "Проверить доступность lovetest.privacy.policy.url (curl)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/check_privacy_url.sh").absolutePath)
}

tasks.register<Exec>("generateUploadKeystoreLoveTest") {
    group = "verification"
    description = "Production upload keystore (LOVETEST_KEYSTORE_PASS required)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/generate_upload_keystore.sh").absolutePath)
}

tasks.register<Exec>("captureStoreLocalLoveTest") {
    group = "screenshots"
    description = "AVD + эмулятор + съёмка (priority по умолчанию). -Plocale=both -Pscope=priority|full"
    workingDir(rootDir)
    val locale = (findProperty("locale") as String?)?.trim()?.ifEmpty { null } ?: "both"
    val scope = (findProperty("scope") as String?)?.trim()?.ifEmpty { null } ?: "priority"
    commandLine(
        "bash",
        file("scripts/capture_store_local.sh").absolutePath,
        locale,
        scope,
    )
}

tasks.register<Exec>("exportPrivacyForHosting") {
    group = "verification"
    description = "Экспорт privacy HTML в build/legal-host/ для GitHub Pages."
    workingDir(rootDir)
    commandLine("bash", file("scripts/export_privacy_for_hosting.sh").absolutePath)
}

tasks.register<Exec>("exportFeatureGraphic") {
    group = "verification"
    description = "Экспорт feature_graphic.png 1024×500 для Play Console."
    workingDir(rootDir)
    commandLine("bash", file("scripts/export_feature_graphic.sh").absolutePath)
}

tasks.register<Exec>("initStoreConfigLoveTest") {
    group = "verification"
    description = "Создать gradle.properties и keystore.properties из example (если нет)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/init_store_config.sh").absolutePath)
}

tasks.register<Exec>("generateDebugUploadKeystoreLoveTest") {
    group = "verification"
    description = "Локальный upload keystore для smoke-test bundleRelease (не для Play)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/generate_debug_upload_keystore.sh").absolutePath)
}

tasks.register<Exec>("bundleReleaseLoveTest") {
    group = "verification"
    description = "verifyLoveTest + bundleRelease + путь к AAB."
    workingDir(rootDir)
    commandLine("bash", file("scripts/bundle_release.sh").absolutePath)
}

tasks.register<Exec>("releaseGateLoveTest") {
    group = "verification"
    description = "verifyLoveTest + audit P0 + store preflight (release gate)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/release_gate.sh").absolutePath)
}

tasks.register<Exec>("openLoveTestDebugScreen") {
    group = "screenshots"
    description = "Открыть экран на устройстве: -PscreenId=hub_main (нужен adb)."
    workingDir(rootDir)
    val screenId = (findProperty("screenId") as String?)?.trim().orEmpty()
    if (screenId.isEmpty()) {
        throw GradleException("Укажите -PscreenId=<screen_id>, например -PscreenId=hub_main")
    }
    commandLine(
        "bash",
        file("scripts/open_debug_screen.sh").absolutePath,
        screenId,
    )
}

tasks.register<Exec>("runRouteSmokeTestsLoveTest") {
    group = "verification"
    description = "Instrumented smoke: DebugStartRoute (adb, быстрее Compose UI)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/run_route_smoke_tests.sh").absolutePath)
}

tasks.register<Exec>("runComposeUiTestsLoveTest") {
    group = "verification"
    description = "Compose UI tests локально (adb + эмулятор)."
    workingDir(rootDir)
    commandLine("bash", file("scripts/run_compose_ui_tests.sh").absolutePath)
}

tasks.register<Exec>("setupGithubRemoteLoveTest") {
    group = "verification"
    description = "git remote add origin + privacy URL hint (-PgithubUser= -PgithubRepo=)."
    workingDir(rootDir)
    val user = (findProperty("githubUser") as String?)?.trim().orEmpty()
    val repo = (findProperty("githubRepo") as String?)?.trim().orEmpty()
    if (user.isEmpty() || repo.isEmpty()) {
        throw GradleException("Укажите -PgithubUser=USER -PgithubRepo=REPO")
    }
    commandLine(
        "bash",
        file("scripts/setup_github_remote.sh").absolutePath,
        user,
        repo,
    )
}

tasks.register<Exec>("connectedComposeUiTestLoveTest") {
    group = "verification"
    description = "Compose UI tests на подключённом устройстве/эмуляторе."
    workingDir(rootDir)
    commandLine("./gradlew", ":app:connectedDebugAndroidTest")
}

tasks.register<Exec>("prepareGitPushLoveTest") {
    group = "verification"
    description = "verifyLoveTestBeforeStore + подсказки перед первым git push."
    workingDir(rootDir)
    commandLine("bash", file("scripts/prepare_git_push.sh").absolutePath)
}

tasks.register<Exec>("verifyLoveTestBeforeStore") {
    group = "verification"
    description = "verifyLoveTest + PNG каталога без placeholder-шаблонов."
    dependsOn("verifyLoveTest")
    workingDir(rootDir)
    commandLine(
        "python3",
        file("scripts/verify_ui_inventory.py").absolutePath,
        "--require-screenshots",
        "--fail-on-placeholders",
    )
}

gradle.projectsEvaluated {
    val gate = rootProject.tasks.named("verifyLoveTest")
    project(":app").tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" }
        .configureEach { mustRunAfter(gate) }
}
