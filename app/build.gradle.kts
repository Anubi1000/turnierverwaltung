import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.spotless)
}

group = "de.anubi1000"
version = "0.1.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation(libs.koin.annotations)
    ksp(libs.koin.annotations.ksp)
    implementation(libs.koin.core)
    implementation(libs.koin.core.viewmodel)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)

    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.kotlinx.immutableCollections)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.hosts)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.json)

    implementation(libs.realm)

    implementation(libs.log4j.core)
    implementation(libs.log4j.kotlin)
    implementation(libs.log4j.slf4j)

    implementation(libs.poi.ooxml)

    implementation(libs.lyricist)
    ksp(libs.lyricist.processor)

    @OptIn(ExperimentalComposeLibrary::class)
    testImplementation(compose.uiTest)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.extensions.koin)
}

sourceSets {
    main {
        resources {
            this.srcDirs(projectDir.resolve("../scoreboard/dist"))
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

composeCompiler {
    stabilityConfigurationFile = projectDir.resolve("compose_stability.conf")
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL","true")
}

compose.desktop {
    application {
        mainClass = "de.anubi1000.turnierverwaltung.MainKt"
        jvmArgs("-Dlog4j.skipJansi=false", "-DlogLevel=info")

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "turnierverwaltung"
            packageVersion = "0.1.0"

            modules("jdk.unsupported", "java.management", "java.naming")

            windows {
                perUserInstall = true
                shortcut = true
                console = true
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(projectDir.resolve("proguard-rules.pro"))
            version = "7.5.0"
        }
    }
}

spotless {
    kotlin {
        targetExclude("build/**")

        ktlint()
            .editorConfigOverride(mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
            ))
    }
}
