import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
}

kotlin {
  @Suppress("OPT_IN_USAGE")
  targetHierarchy.default()

  androidTarget()
  jvm()
  iosX64()
  iosArm64()
  iosSimulatorArm64()
  js(IR) {
    browser()
  }
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(projects.library)
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        implementation(compose.components.resources)
      }
    }

    val jsWasmMain by creating {
      dependsOn(commonMain)
    }

    val jsMain by getting {
      dependsOn(jsWasmMain)
    }

    val wasmJsMain by getting {
      dependsOn(jsWasmMain)
    }
  }
}

android {
  namespace = "me.saket.swipe"

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
    compileSdk = libs.versions.compileSdk.get().toInt()
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
