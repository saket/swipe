import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform") version "1.9.0" apply false
  id("com.vanniktech.maven.publish") version "0.24.0" apply false
  id("org.jetbrains.compose") version "1.5.0" apply false
  id("com.android.application") version "8.0.2" apply false
  id("com.android.library") version "8.0.2" apply false
}

buildscript {

  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
  }

  dependencies {
    classpath("com.vanniktech:gradle-maven-publish-plugin:0.24.0")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
  }
}
