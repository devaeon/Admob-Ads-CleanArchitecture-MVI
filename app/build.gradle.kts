plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.googleDaggerHiltAndroid)
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidxNavigationSafeArgs)

    // PlayStore version only
    alias(libs.plugins.googleCrashlytics)
    alias(libs.plugins.googleGms)
}

android {
    namespace = "com.devaeon.adsTemplate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devaeon.adsTemplate"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField(
                "String",
                "CONSENT_TEST_DEVICES_IDS",
                "\"${project.findProperty("CONSENT_TEST_DEVICE_IDS") ?: ""}\""
            )
            buildConfigField(
                "String",
                "CONSENT_TEST_GEOGRAPHY",
                "\"${project.findProperty("CONSENT_TEST_GEOGRAPHY_DEBUG") ?: "0"}\""
            )
            buildConfigField(
                "String",
                "ADS_TEST_DEVICES_IDS",
                "\"${project.findProperty("ADS_TEST_DEVICES_IDS") ?: ""}\""
            )

            resValue("string", "admob_app_id", "ca-app-pub-3940256099942544~3347511713")
            resValue("string", "admob_interstitial_ad_unit", "ca-app-pub-3940256099942544/1033173712")


            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            buildConfigField("String", "CONSENT_TEST_DEVICES_IDS", "\"\"")
            buildConfigField("String", "CONSENT_TEST_GEOGRAPHY", "\"0\"")
            buildConfigField("String", "ADS_TEST_DEVICES_IDS", "\"\"")

            resValue("string", "admob_app_id", "ca-app-pub-3940256099942544~3347511713")
            resValue("string", "admob_interstitial_ad_unit", "ca-app-pub-3940256099942544/1033173712")



            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerView)

    implementation(libs.google.material)


    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)

    implementation(libs.google.dagger.hilt)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintLayout)
    ksp(libs.google.dagger.hilt.compiler)

    implementation(libs.google.gms.ads)

    implementation(libs.androidx.core.splashscreen)

}