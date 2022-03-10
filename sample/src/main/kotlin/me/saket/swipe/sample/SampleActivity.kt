package me.saket.swipe.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Archive
import androidx.compose.material.icons.twotone.ReplyAll
import androidx.compose.material.icons.twotone.Snooze
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
class SampleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val colors = lightColorScheme().copy(
        background = Color(0XFFF8F5FA),
        surface = Color(0XFFF8F5FA)
      )

      MaterialTheme(colors) {
        Scaffold(
          topBar = { LargeTopAppBar(title = { Text("Inbox") }) }
        ) {
          SwipeableBoxPreview()
        }
      }
    }
  }
}

@Composable
private fun SwipeableBoxPreview() {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Center
  ) {
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
      startActions = listOf(replyAll),
      endActions = listOf(snooze, archive),
      swipeThreshold = 40.dp,
      backgroundUntilSwipeThreshold = Color(0xFFC1BAC9),
      content = {
        ListItem(
          isSnoozed = isSnoozed
        )
      }
    )
  }
}

@Composable
private fun ListItem(
  modifier: Modifier = Modifier,
  isSnoozed: Boolean
) {
  Row(
    modifier
      .fillMaxWidth()
      .shadow(1.dp)
      .background(Color.White)
      .padding(20.dp)
      .animateContentSize()
  ) {
    Box(
      Modifier
        .padding(top = 4.dp)
        .size(52.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
    )

    Column(Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "The Batman",
        style = MaterialTheme.typography.titleMedium
      )
      Text(
        modifier = Modifier.padding(top = 2.dp),
        text = "Fear is a tool. When that light hits the sky, it’s not just a call. It’s a warning. For them.",
        style = MaterialTheme.typography.bodyMedium
      )

      if (isSnoozed) {
        Text(
          modifier = Modifier
            .padding(top = 16.dp)
            .background(Color.SeaBuckthorn.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
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
