plugins {
    alias(libs.plugins.android.application)
}

// This sets a default JDK for all Java-based tools on your system, including Gradle and Maven.
// JAVA_HOME=/path/to/java17


// 命令行运行，直接指定运行gradle时的jdk版本
// & "C:\Program Files\Java\jdk-21\bin\java.exe" -jar gradle\wrapper\gradle-wrapper.jar :kdsmartposlib:build
// 指定编译当前模块所使用的jdk版本
// https://docs.gradle.org/current/userguide/toolchains.html#sec:using-java-toolchains
// java {
//     toolchain {
//         languageVersion = JavaLanguageVersion.of(17)
//     }
// }

android {
    namespace = "com.example.demosetlanguage"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.demosetlanguage"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // buildFeatures {
    //     viewBinding = true
    // }
    buildFeatures(action = fun com.android.build.api.dsl.ApplicationBuildFeatures.() {

        // 这里的 this 是 ApplicationBuildFeatures 实例ApplicationBuildFeatures@
        this.viewBinding = true
    })
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.eventbus)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}