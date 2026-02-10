plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
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
            val storeFileVar = System.getenv("RELEASE_STORE_FILE")
                ?: project.properties["RELEASE_STORE_FILE"]?.toString()
            storeFile = storeFileVar?.let { file(it) }

            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                ?: project.properties["RELEASE_STORE_PASSWORD"]?.toString()
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: project.properties["RELEASE_KEY_ALIAS"]?.toString()
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: project.properties["RELEASE_KEY_PASSWORD"]?.toString()
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
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
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

    // Mockito для unit тестов
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    // для Kotlin

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //Google
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    implementation(libs.gson)

    // Ktor клиент с OkHttp движком (не путать с OkHttp библиотекой напрямую)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)

    // Для работы с Gson в Ktor
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.gson)

    //viewmodel

    // ViewModel for Activities/Fragments
    implementation(libs.lifecycle.viewmodel.ktx)

    // For Jetpack Compose projects
    implementation(libs.lifecycle.viewmodel.compose)

    // Optional: LiveData if you use it
    implementation(libs.lifecycle.livedata.ktx)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // ViewModel integration with Navigation3
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}