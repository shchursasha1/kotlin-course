plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.homework2_1"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.homework2_1"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Debug app is installed as com.example.homework2_1.debug
            applicationIdSuffix = ".debug"
        }
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        // Generates binding classes for layouts, e.g. ActivityFullCalculatorBinding
        viewBinding = true
        // Generates BuildConfig class with BuildConfig.DEBUG
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.coil)
    implementation(libs.timber)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

tasks.register("printProjectInfo") {
    // Read values here, because configuration cache doesn't allow using `project` inside doLast
    val moduleName = project.name
    val buildDirPath = project.layout.buildDirectory.get().asFile.path
    doLast {
        println("--- Інформація про модуль ---")
        println("Модуль: $moduleName")
        println("Шлях до збірки: $buildDirPath")
    }
}
