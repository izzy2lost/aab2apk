plugins {
    id("com.android.application")
    kotlin("android")
}

repositories {
    google()
    mavenCentral()
}

android {
    namespace = "com.aab2apk"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aab2apk"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        ndkVersion = "28.2.13676358"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3:1.1.1")
}
