package me.saket.swipe.sample

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.saket.swipe.sample.theme.DarkTheme
import me.saket.swipe.sample.theme.LightTheme
import me.saket.swipe.sample.theme.MainContent

@OptIn(ExperimentalMaterial3Api::class)
class SampleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val uiController = rememberSystemUiController()
      val systemInDarkTheme = isSystemInDarkTheme()
      LaunchedEffect(Unit) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        uiController.setSystemBarsColor(Color.Transparent, darkIcons = !systemInDarkTheme)
        uiController.setNavigationBarColor(Color.Transparent)
      }

      val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (systemInDarkTheme) dynamicDarkColorScheme(this) else dynamicLightColorScheme(this)
      } else {
        if (systemInDarkTheme) DarkTheme else LightTheme
      }

      MaterialTheme(colors) {
        Scaffold(
          topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
          }
        ) { contentPadding ->
          MainContent(modifier = Modifier.padding(contentPadding))
        }
      }
    }
  }
}
