import java.text.SimpleDateFormat
import java.util.Date
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

fun formatDateToYYYYDDMM(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.wjdaudtn.mission"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wjdaudtn.mission"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        setProperty(
//            "archivesBaseName",
//            "Mission-v$versionCode($versionName)-${formatDateToYYYYDDMM(Date())}"
//        )
    }

    archivesName = "Mission-v${defaultConfig.versionCode}(${defaultConfig.versionName})-${formatDateToYYYYDDMM(Date())}"

    signingConfigs {
        create("release") {
            keyAlias = findProperty("SIGNING_KEY_ALIAS") as String
            keyPassword = findProperty("SIGNING_KEY_PASSWORD") as String
            storeFile = file(findProperty("SIGNING_STORE_FILE") as String)
            storePassword = findProperty("SIGNING_STORE_PASSWORD") as String
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    viewBinding {
        enable = true
    }

    flavorDimensions += listOf("service")
    productFlavors {
        create("demo") {
            dimension = "service"
            applicationIdSuffix = ".demo"
        }
        create("production") {
            dimension = "service"
            applicationIdSuffix = ".production"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.camera.core)
//    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.kotlinx.coroutines.core)
//    implementation(kotlin("script-runtime"))

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.4.0-beta02"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    //barcode mlkit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")



}


// Git 태그 추가를 위한 Exec 태스크
tasks.register<Exec>("tagGit") {
    commandLine("git", "tag", "v${android.defaultConfig.versionName}_${formatDateToYYYYDDMM(Date())}")
}

// 빌드 후 태그 추가
tasks.named("build").configure {
    dependsOn("tagGit")
}