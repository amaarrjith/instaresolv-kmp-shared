import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream
val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.layout)
    debugImplementation(libs.compose.uiTooling)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    // Image Cropping
    implementation(libs.androidx.appcompat)
    implementation("com.github.yalantis:ucrop:2.2.11")
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.instaresolve.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 13
        versionName = "2.0"
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: if (keystoreProperties.isNotEmpty()) keystoreProperties["storeFile"] as? String else null
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                    ?: keystoreProperties["storePassword"] as? String
                keyAlias = System.getenv("KEY_ALIAS")
                    ?: keystoreProperties["keyAlias"] as? String
                keyPassword = System.getenv("KEY_PASSWORD")
                    ?: keystoreProperties["keyPassword"] as? String
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += listOf("brand")
    productFlavors {
        create("instaresolv") {
            dimension = "brand"
            applicationId = "com.instaresolve.app"
            buildConfigField("String", "BRAND_NAME", "\"instaresolv\"")
        }
        create("qualityexpertise") {
            dimension = "brand"
            applicationId = "com.quality_expertise.app"
            buildConfigField("String", "BRAND_NAME", "\"qualityexpertise\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

androidComponents {
    onVariants { variant ->
        if (variant.buildType == "debug") {
            if (variant.flavorName == "instaresolv") {
                variant.applicationId.set("com.instaresolve.app.development")
            } else if (variant.flavorName == "qualityexpertise") {
                variant.applicationId.set("com.quality_expertise.app.dev")
            }
        }
    }
}

configurations.configureEach {
    resolutionStrategy {
        force("androidx.camera:camera-core:1.4.0")
        force("androidx.camera:camera-camera2:1.4.0")
        force("androidx.camera:camera-lifecycle:1.4.0")
        force("androidx.camera:camera-view:1.4.0")
    }
}
