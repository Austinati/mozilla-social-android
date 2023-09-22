plugins {
    id("org.mozilla.social.android.library")
    id("org.mozilla.social.android.library.secrets")
    alias(libs.plugins.jetbrains.python)
    alias(libs.plugins.glean.gradle.plugin)
}

android {
    namespace = "org.mozilla.social.core.analytics"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.glean)

    implementation(libs.koin)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)

    testImplementation(libs.glean.forUnitTests)
}