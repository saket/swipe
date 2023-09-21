import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import com.android.build.gradle.BaseExtension as AndroidBaseExtension
import com.android.build.gradle.BasePlugin as AndroidBasePlugin

buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

// Lists all plugins used throughout the project without applying them.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.mavenPublish) apply false
  alias(libs.plugins.paparazzi) apply false
  alias(libs.plugins.dokka) apply false
}

allprojects {
  plugins.withType<AndroidBasePlugin>().configureEach {
    configure<AndroidBaseExtension> {
      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
      }
    }
  }
  tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }
}
