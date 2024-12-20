import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqlDelight)
    kotlin("plugin.serialization") version "1.8.0"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sqldelight {
        databases {
            create("LifeOsDatabase") {
                packageName = "com.lifeos"
            }
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(compose.uiTooling)
            implementation(compose.material3)

            implementation(libs.kizitonwose.calendar)

            //Calendar view
            implementation (libs.glide.compose)

            //SQLDelight Android
            implementation(libs.sqldelight.android)

            //auth android
            implementation (libs.androidx.credentials)
            implementation (libs.googleid)

            //splash screen implementation
            implementation (libs.androidx.core.splashscreen)

            //ktor for android support
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            //compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(compose.components.resources)

            //voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottom.sheet.navigator)
            implementation(libs.voyager.tab.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.screenmodel)

            //kermit
            implementation(libs.kermit)

            //SQLDelight Common
            implementation(libs.sqldelight.coroutines)

            implementation(libs.kotlinx.serialization.json)

            // Ktor client core
            implementation(libs.ktor.client.core.v234)

            // Ktor for content negotiation
            implementation(libs.ktor.client.content.negotiation)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            // Ktor serialization with JSON support
            implementation(libs.ktor.serialization.kotlinx.json)

            // Extended icons pack
            implementation(compose.materialIconsExtended)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            //SQLDelight Desktop
            implementation(libs.sqldelight.jvm)

            //Auth Desktop
            implementation(libs.google.auth.desktop)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)

            //ktor for jvm support
            implementation(libs.ktor.client.java)
        }
    }
}

android {
    namespace = "com.project.lifeos"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.project.lifeos"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.project.lifeos"
            packageVersion = "1.0.0"
        }
    }
}
