plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.chaquo.python)
}


android {
    namespace = "com.example.pokeremotionapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pokeremotionapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("x86_64")
        }

        chaquopy {
            defaultConfig {
                version = "3.8"
                buildPython("C:/Python3.8/python.exe")

                pip {
                    install("contourpy-1.0.7-0-cp313-cp313-android_24_x86_64.whl")
                    install("numpy-1.19.5-0-cp38-cp38-android_21_x86_64.whl")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}