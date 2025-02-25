plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.mibocadilloapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mibocadilloapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    viewBinding {enable = true}

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // nav_graph.xml dependencies
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    // Room
    runtimeOnly("androidx.room:room-runtime:2.6.1")
    runtimeOnly("androidx.room:room-ktx:2.6.1")

    // Navigation Component
    runtimeOnly("androidx.navigation:navigation-fragment-ktx:2.8.7")
    runtimeOnly("androidx.navigation:navigation-ui-ktx:2.8.7")

    // ViewModel y LiveData
    runtimeOnly("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    runtimeOnly("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Kotlinx Coroutines
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // RecyclerView
    runtimeOnly("androidx.recyclerview:recyclerview:1.4.0")

    // Inicio Sesion Google
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // FireBase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-firestore:25.1.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}