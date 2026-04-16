plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.apilocalaar"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlin {
        compilerOptions {
            // languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
            // Optional: Set jvmTarget
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }
}

dependencies {
    // implementation(fileTree("libs") { include("*.jar") })
    fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // method 1
    // depend on local aar
    // implementation(project(":localrepo:helloworld1"))
    // implementation(project(":localrepo:helloworld2"))
    // implementation(project(":localrepo:helloworld3"))

    // method 2 depend on modules directly
    implementation(project(":aarhelloworld1"))
    implementation(project(":aarhelloworld2"))
    implementation(project(":aarhelloworld3"))
}