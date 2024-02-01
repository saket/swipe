plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "me.saket.swipe.sample"

  defaultConfig {
    applicationId = namespace
    minSdk = libs.versions.minSdk.get().toInt()
    compileSdk = libs.versions.compileSdk.get().toInt()
    targetSdk = libs.versions.compileSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
  }
  java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
  }
  lint {
    abortOnError = true
  }
}

dependencies {
  implementation(projects.library)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity)
  implementation(libs.compose.foundation)
  implementation(libs.compose.ui)
  implementation(libs.compose.material3)
  implementation(libs.compose.materialIcons)
  implementation(libs.accompanist.systemUi)
}
