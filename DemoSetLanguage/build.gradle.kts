// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

// https://docs.gradle.org/9.4.1/userguide/gradle_daemon.html#sec:daemon_jvm_criteria
// $ ./gradlew updateDaemonJvm --jvm-version=17
tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = JavaLanguageVersion.of(17)
}