import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // KSP
    alias(libs.plugins.ksp)

    // Serialization
    id ("kotlinx-serialization")
    id("kotlin-parcelize")

}

android {
    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\redij\\AndroidStudioProjects\\Waltz\\keystore\\waltz.jks")
            storePassword = "waltz@123"
            keyAlias = "key0"
            keyPassword = "waltz@123"
        }
    }
    namespace = "com.chirag_redij.waltz"
    compileSdk = 35

    fun loadLocalProperties(rootDir: File): Properties {
        val propertiesFile = File(rootDir, "local.properties")
        val properties = Properties()
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { inputStream ->
                properties.load(inputStream)
            }
        }
        return properties
    }

    val localProperties = loadLocalProperties(rootDir)
    val authorizationKey: String = localProperties.getProperty("authorization")

    defaultConfig {
        applicationId = "com.chirag_redij.waltz"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String","authorizationKey","\"$authorizationKey\"")

    }



    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(
            File(buildDir, "generated/ksp/$name/kotlin")
        )
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.adaptive.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Splash Screen -------------------------------------------------------------------------------
    implementation(libs.androidx.core.splashscreen)

    // Compose Destinations ------------------------------------------------------------------------
    implementation(libs.raamcosta.compose.destinations.core)
    ksp(libs.raamcosta.compose.destinations.ksp)

    // COIL & Lottie -------------------------------------------------------------------------------
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.material.icons)
    // KTOR ----------------------------------------------------------------------------------------
    implementation(libs.bundles.ktor)
    // Koin
    implementation(libs.bundles.koin)

    // Utilities -----------------------------------------------------------------------------------
    implementation(libs.timber)
    implementation(libs.compose.shimmer)
    implementation(libs.messageBar)
    implementation(libs.compose.material3.pullrefresh)

    // Serialization -------------------------------------------------------------------------------
    implementation(libs.kotlinx.serialization.json)

    // Profile Installer ---------------------------------------------------------------------------
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")

    // Google InApp Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Google InApp Review
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

}