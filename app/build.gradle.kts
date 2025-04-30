import com.android.build.api.variant.BuildConfigField
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "rs.kitten.buggy"
    compileSdk = 35

    defaultConfig {
        applicationId = "rs.kitten.buggy"
        minSdk = 35
        //noinspection EditedTargetSdkVersion
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

androidComponents {
    onVariants {

        val buildTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")

        it.buildConfigFields.put(
            "BUILD_TIME", BuildConfigField(
                "String", "\"${buildTime.format(formatter).toString()}\"", "build Date"
            )
        )
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.material)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //noinspection UseTomlInstead
    implementation("androidx.compose.material3:material3:1.3.2")
    //noinspection UseTomlInstead
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")
    //noinspection UseTomlInstead
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.4.0-alpha13")
    //noinspection UseTomlInstead
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    //noinspection UseTomlInstead
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
}