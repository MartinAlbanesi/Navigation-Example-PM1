buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.10.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}
