import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqlDelight)
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

            implementation("com.kizitonwose.calendar:compose:2.5.1")

            //SQLDelight Android
            implementation(libs.sqldelight.android)

//            Maybe I'll use it for creating calendar view for android, but basically, we have to find smth multiplatform
//            implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.3.0")
//            implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:1.3.0")
        }
        commonMain.dependencies {
            //compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            @OptIn(ExperimentalComposeLibrary::class)
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
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.materialIconsExtended)
            //SQLDelight Desktop
            implementation(libs.sqldelight.jvm)
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
