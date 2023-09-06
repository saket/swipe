import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

group = "me.saket.swipe"
version = "1.3.0-SNAPSHOT"

kotlin {
    androidTarget()
    jvm()
    iosX64()
    iosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    compileSdk = 34
    namespace = "me.saket.swipe"

    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.DEFAULT, automaticRelease = true)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("swipe builds composables that can be swiped left or right for revealing actions.")
        url.set("https://github.com/saket/swipe")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        scm {
            url.set("https://github.com/saket/swipe")
            connection.set("scm:git@github.com:saket/swipe.git")
            developerConnection.set("scm:git@github.com:saket/swipe.git")
        }

        developers {
            developer {
                id.set("saketme")
                name.set("Saket Narayan")
                url.set("https://github.com/saket")
            }
        }
    }
}