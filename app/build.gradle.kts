plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = rootProject.file("local.properties")
    .takeIf { it.exists() }
    ?.readLines()
    .orEmpty()
    .mapNotNull { line ->
        val entry = line.trim()
        if (entry.isEmpty() || entry.startsWith("#") || '=' !in entry) {
            null
        } else {
            entry.substringBefore('=').trim() to entry.substringAfter('=').trim()
        }
    }
    .toMap()
val supabaseUrl = localProperties["supabase.url"].orEmpty()
val supabaseAnonKey = localProperties["supabase.anonKey"].orEmpty()

android {
    namespace = "com.example.notas"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.notas"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SUPABASE_URL", "\"${supabaseUrl.escapeBuildConfigString()}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${supabaseAnonKey.escapeBuildConfigString()}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

fun String.escapeBuildConfigString(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")