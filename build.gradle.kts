// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    dependencies {

    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Serialization
    alias(libs.plugins.kotlinPluginSerialization) apply false
    kotlin("jvm") version "2.0.20"
    alias(libs.plugins.android.test) apply false
//    kotlin("plugin.serialization") version "2.0.20"
}