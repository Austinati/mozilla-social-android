plugins {
    id("org.mozilla.social.android.library")
    id("org.mozilla.social.android.library.compose")
}

android {
    namespace = "org.mozilla.social.feature.settings"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:analytics"))

    implementation(libs.koin)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)
}