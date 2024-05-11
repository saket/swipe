import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
}


kotlin {
  js(IR) {
    moduleName = "sample"
    browser {
      commonWebpackConfig {
        outputFileName = "sample.js"
      }
    }
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    moduleName = "sample"
    browser {
      commonWebpackConfig {
        outputFileName = "sample.js"
      }
    }
    binaries.executable()
  }

  sourceSets {
    val jsWasmMain by creating {
      dependencies {
        implementation(projects.library)
        implementation(projects.sample.shared)
        implementation(compose.runtime)
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material3)
        @OptIn(ExperimentalComposeLibrary::class)
        implementation(compose.components.resources)
      }
    }
    val jsMain by getting {
      dependsOn(jsWasmMain)
    }
    val wasmJsMain by getting {
      dependsOn(jsWasmMain)
    }
  }
}


compose.experimental {
  web.application {}
}
