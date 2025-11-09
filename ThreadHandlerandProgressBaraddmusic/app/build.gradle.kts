plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.threadhandlerandprogressbaraddmusic"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.threadhandlerandprogressbaraddmusic"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 檔案路徑: app/build.gradle.kts

    dependencies {
        implementation("androidx.core:core-ktx:1.17.0")
        implementation("androidx.appcompat:appcompat:1.7.1")
        implementation("com.google.android.material:material:1.13.0")
        implementation("androidx.constraintlayout:constraintlayout:2.2.1")
        implementation("androidx.activity:activity:1.11.0")

        // ▼▼▼▼▼ 在這裡加入這一行 ▼▼▼▼▼
        implementation("androidx.fragment:fragment-ktx:1.8.0")
        // ▲▲▲▲▲ ▲▲▲▲▲ ▲▲▲▲▲ ▲▲▲▲▲▲ ▲▲▲▲▲

        // 其他測試相關的依賴...
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    }

// 請使用最新的穩定版本，你可以在 Android 開發者官網上查到
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}