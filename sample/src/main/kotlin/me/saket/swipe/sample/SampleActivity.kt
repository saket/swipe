package me.saket.swipe.sample

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Archive
import androidx.compose.material.icons.twotone.ReplyAll
import androidx.compose.material.icons.twotone.Snooze
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import me.saket.swipe.sample.theme.DarkTheme
import me.saket.swipe.sample.theme.LightTheme

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
          LazyColumn(Modifier.padding(contentPadding).fillMaxSize()) {
            items(20) { index ->
              SwipeableBoxPreview(
                Modifier.fillMaxWidth()
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SwipeableBoxPreview(modifier: Modifier = Modifier) {
  var isSnoozed by rememberSaveable { mutableStateOf(false) }
  var isArchived by rememberSaveable { mutableStateOf(false) }

  val replyAll = SwipeAction(
    icon = rememberVectorPainter(Icons.TwoTone.ReplyAll),
    background = Color.Perfume,
    onSwipe = { println("Reply swiped") },
    isUndo = false,
  )
  val snooze = SwipeAction(
    icon = rememberVectorPainter(Icons.TwoTone.Snooze),
    background = Color.SeaBuckthorn,
    onSwipe = { isSnoozed = !isSnoozed },
    isUndo = isSnoozed,
  )
  val archive = SwipeAction(
    icon = rememberVectorPainter(Icons.TwoTone.Archive),
    background = Color.Fern,
    onSwipe = { isArchived = !isArchived },
    isUndo = isArchived,
  )

  SwipeableActionsBox(
    modifier = modifier,
    startActions = listOf(replyAll),
    endActions = listOf(snooze, archive),
    swipeThreshold = 40.dp,
    backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.surfaceColorAtElevation(40.dp)
  ) {
    BatmanIpsumItem(
      isSnoozed = isSnoozed
    )
  }
}

@Composable
private fun BatmanIpsumItem(
  modifier: Modifier = Modifier,
  isSnoozed: Boolean
) {
  Row(
    modifier
      .fillMaxWidth()
      .shadow(1.dp)
      .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
      .padding(vertical = 16.dp, horizontal = 20.dp)
      .animateContentSize()
  ) {
    Box(
      Modifier
        .padding(top = 2.dp)
        .size(52.dp)
        .background(MaterialTheme.colorScheme.primary, CircleShape)
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "The Batman",
        style = MaterialTheme.typography.titleMedium
      )
      Text(
        modifier = Modifier.padding(top = 4.dp),
        text = "Fear is a tool. When that light hits the sky, it’s not just a call. It’s a warning. For them.",
        style = MaterialTheme.typography.bodyMedium
      )

      if (isSnoozed) {
        Text(
          modifier = Modifier
            .padding(top = 16.dp)
            .background(Color.SeaBuckthorn.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          text = "Snoozed until tomorrow",
          style = MaterialTheme.typography.labelLarge
        )
      }
    }
  }
}

val Color.Companion.SeaBuckthorn get() = Color(0xFFF9A825)
val Color.Companion.Fern get() = Color(0xFF66BB6A)
val Color.Companion.Perfume get() = Color(0xFFD0BCFF)
