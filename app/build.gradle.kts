import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.gms.oss.license.plugin)
}

android {
    namespace = "com.ogata_k.mobile.winp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ogata_k.mobile.winp"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        baseline = file("lint-baseline.xml")
    }

    signingConfigs {
        create("release") {
            if (System.getenv("CI") == "true") { // CI=true is exported by Codemagic
                val storeFilePath = System.getenv("CM_KEYSTORE_PATH")
                if (storeFilePath != null && storeFilePath != "") {
                    storeFile = file(storeFilePath)
                    storePassword = System.getenv("CM_KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("CM_KEY_ALIAS")
                    keyPassword = System.getenv("CM_KEY_PASSWORD")
                }
            } else {
                val keystoreProperties = Properties()
                val keystorePropertiesFile = rootProject.file("keystore.properties")

                if (keystorePropertiesFile.exists()) {
                    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

                    storeFile = file(keystoreProperties["storeFile"] as String)
                    storePassword = keystoreProperties["storePassword"] as String
                    keyAlias = keystoreProperties["keyAlias"] as String
                    keyPassword = keystoreProperties["keyPassword"] as String
                }
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }

        release {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    flavorDimensions += listOf("default")
    productFlavors {
        create("develop") {
            dimension = "default"
            applicationIdSuffix = ".dev"
        }

        create("staging") {
            dimension = "default"
            applicationIdSuffix = ".stg"
        }

        create("product") {
            dimension = "default"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // For my modules
    implementation(project(":app:common"))
    implementation(project(":app:domain"))
    implementation(project(":app:infra"))

    // For libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icon.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    // 実行時に自動生成されたRoomのDatabaseを使うためroomのruntimeとktxだけ入れておく
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    // for google play service
    implementation(libs.gms.play.services.oss.licenses)

    // For Local tests
    testImplementation(libs.junit)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // For instrumentation tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // For Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
