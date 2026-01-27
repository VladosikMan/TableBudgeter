plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.vladgad.tablebudgeter"


    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.vladgad.tablebudgeter"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            // Сначала пытаемся взять из переменных окружения (для CI),
            // если их нет, берём из gradle.properties (для локальной сборки)
            val storeFileVar = System.getenv("RELEASE_STORE_FILE") ?: project.properties["RELEASE_STORE_FILE"]?.toString()
            storeFile = storeFileVar?.let { file(it) }

            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: project.properties["RELEASE_STORE_PASSWORD"]?.toString()
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: project.properties["RELEASE_KEY_ALIAS"]?.toString()
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: project.properties["RELEASE_KEY_PASSWORD"]?.toString()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // Используем этот ключ для debug-сборки (Run app)
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.credentials:credentials:1.6.0-rc01")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-rc01")
    implementation("com.google.android.libraries.identity.googleid:googleid:<latest version>")
    implementation("com.google.android.gms:play-services-auth:21.5.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Ktor клиент с OkHttp движком (не путать с OkHttp библиотекой напрямую)
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-okhttp:2.3.7")

    // Для работы с Gson в Ktor
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")
}