import com.android.build.api.variant.BuildConfigField
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.text.SimpleDateFormat
import java.util.Date


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

//        buildConfigField("String", "BUILD_TYPE", "\"${buildType}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
////            resValue("string","BUILD_TYPE","release")
//        }

        create("staging") {

            buildConfigField("String", "BUILD_TYPE", "\"debug\"")
            initWith(getByName("debug"))
            manifestPlaceholders["hostName"] = "com.wjdaudtn.mission"
            applicationIdSuffix = ".debugStaging"

        }
        create("demo") {

            buildConfigField("String", "BUILD_TYPE", "\"release\"")
            initWith(getByName("release"))
            manifestPlaceholders["hostName"] = "com.wjdaudtn.mission"
            applicationIdSuffix = ".releaseDemo"

        }
        create("production") {

            buildConfigField("String", "BUILD_TYPE", "\"release\"")
            initWith(getByName("release"))

            manifestPlaceholders["hostName"] = "com.wjdaudtn.mission"
            applicationIdSuffix = ".releaseProduction"

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

    fun formatDateToYYYYDDMM(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(date)
    }

//    afterEvaluate {
//        val androidExtension = project.extensions.getByType(AppExtension::class.java)
//        val variants = androidExtension.applicationVariants
//        for (variant in variants) {
//            val buildTypeName = variant.buildType.name
//            for (output in variant.outputs) {
//                output as BaseVariantOutputImpl
//                val apkName = "Mission-$buildTypeName-v${defaultConfig.versionName}-${
//                    formatDateToYYYYDDMM(Date())
//                }.apk"
//                val outputFile = output.outputFile
//
//                val renameTask =
//                    tasks.register("rename${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}Apk") {
//                        doLast {
//                            File(outputFile.parent, apkName).apply {
//                                if (exists()) {
//                                    delete()
//                                }
//                            }
//                            outputFile.renameTo(File(outputFile.parent, apkName))
//                        }
//                    }
//
//                variant.assembleProvider.configure {
//                    finalizedBy(renameTask)
//                }
//            }
//        }
//    }//chatgpt

//    applicationVariants.all { variant ->
//        variant.outputs.all { output ->
//            output as BaseVariantOutputImpl
//            val buildTypeName = variant.buildType.name
//            val apkName = "Mission-$buildTypeName-v${defaultConfig.versionName}-${formatDateToYYYYDDMM(Date())}.apk"
//            output.outputFileName.(apkName)
//        }
//    }

//    applicationVariants.all { variant ->
//        variant.outputs.forEach { output ->
//            if (output is BaseVariantOutputImpl) {
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//                val date = dateFormat.format(Date())
//                val appName = "AndroidStudy" // 앱 이름을 적절하게 변경하세요.
//                val versionName = variant.versionName ?: "1.0"
//                val buildType = variant.buildType.name
//                val flavorName = variant.flavorName ?: "default"
//
//                val fileName = "${appName}-${versionName}-${buildType}-${flavorName}-${date}.apk"
//                output.outputFileName = fileName
//            }
//        }
//        return@android
//    }

//    applicationVariants.all { variant ->
//        variant.outputs.all { output ->
//            if (output is BaseVariantOutputImpl) {
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//                val date = dateFormat.format(Date())
//                val appName = "AndroidStudy" // 앱 이름을 적절하게 변경하세요.
//                val versionName = variant.versionName ?: "1.0"
//                val buildType = variant.buildType.name
//                val flavorName = variant.flavorName ?: "default"
//
//                val fileName = "${appName}-${versionName}-${buildType}-${flavorName}-${date}.apk"
//                output.outputFileName = fileName
//            }
//        }
//        return@android
//    }

//    applicationVariants.all { variant ->
//
//        variant.outputs.all { baseVariantOutput: BaseVariantOutput? ->
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//            val date = dateFormat.format(Date())
//            val appName = "AndroidStudy" // 앱 이름을 적절하게 변경하세요.
//            val versionName = variant.versionName ?: "1.0"
//            val buildType = variant.buildType.name
//            val flavorName = variant.flavorName ?: "default"
//
//            val fileName = "${appName}-${versionName}-${buildType}-${flavorName}-${date}.apk"
//            if (baseVariantOutput != null) {
//                baseVariantOutput.outputFile.name.format(fileName)
//            }
//            return@android
//        }
//    }

    archivesName = "Mission-v${defaultConfig.versionName}-${formatDateToYYYYDDMM(Date())}"
//    val archivesName: String by lazy {
//        val versionName = "1.0" // 이 값을 BuildConfig.VERSION_NAME로 설정해도 됩니다.
//        val buildType = if (BuildConfig.DEBUG) "debug" else "release"
//        "Mission-v$versionName-${formatDateToYYYYDDMM(Date())}-$buildType"
//    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
//    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.kotlinx.coroutines.core)
//    implementation(kotlin("script-runtime"))
}

// apk 생성할때 나는 규칙이있다.
// apk-{release}-{YYYY-MM-DD}.apk
// 생성할때 git에 TAG 자동으로