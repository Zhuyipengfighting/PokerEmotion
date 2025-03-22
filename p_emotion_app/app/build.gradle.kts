plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.chaquo.python)
}


android {
    namespace = "com.example.pokeremotionapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pokeremotionapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        chaquopy {
            defaultConfig {
                version = "3.8"
                buildPython("C:/Python3.8/python.exe")

                pip {
                    install("contourpy-1.0.5-0-cp38-cp38-android_21_arm64_v8a.whl")
                    install("contourpy-1.0.5-0-cp38-cp38-android_21_x86.whl")
                    install("numpy-1.19.5-0-cp38-cp38-android_21_x86_64.whl")
                    install("numpy-1.19.5-0-cp38-cp38-android_21_arm64_v8a.whl")
                    install("-r" , "C:/Project/PokerEmotion/p_emotion_app/app/src/main/python/requirements.txt")
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            isMinifyEnabled = false
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.glide)
    implementation(files("src\\state\\MPAndroidChart-v3.0.1.jar"))
    implementation(libs.gson)
    implementation(libs.opencsv)
    implementation(libs.jackson.databind)
    implementation(libs.cardview)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}