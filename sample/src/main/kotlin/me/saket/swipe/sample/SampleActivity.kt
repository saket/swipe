package me.saket.swipe.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.twotone.ArrowDownward
import androidx.compose.material.icons.twotone.ArrowUpward
import androidx.compose.material.icons.twotone.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

class SampleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SwipeableBoxPreview()
    }
  }
}

@Preview
@Composable
fun SwipeableBoxPreview() {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center
  ) {
    SwipeableActionsBox(Modifier.padding(bottom = 20.dp)) {
      ListItem()
    }

    val lastName = remember { mutableStateOf<String?>(null) }

    SwipeableActionsBox(
      startActions = listOf(
        SwipeAction(
          icon = {
            SwipeActionIcon(
              name = "reply",
              icon = Icons.TwoTone.LocalOffer,
              lastName = lastName,
            )
          },
          background = Color(0xFFF9A825),
          onSwipe = { println("Reply swiped") },
          isUndo = false,
        ),
        SwipeAction(
          icon = {
            SwipeActionIcon(
              name = "options",
              icon = Icons.Filled.Favorite,
              lastName = lastName,
            )
          },
          background = Color(0xFF66BB6A),
          onSwipe = { println("Options swiped") },
          isUndo = true,
        )
      ),
      endActions = listOf(
        SwipeAction(
          icon = {
            SwipeActionIcon(
              name = "upvote",
              icon = Icons.TwoTone.ArrowUpward,
              lastName = lastName,
            )
          },
          background = Color(0xFFFF8A65),
          onSwipe = { println("Upvote swiped") },
          isUndo = true,
        ),
        SwipeAction(
          icon = {
            SwipeActionIcon(
              name = "downvote",
              icon = Icons.TwoTone.ArrowDownward,
              lastName = lastName,
            )
          },
          background = Color(0xFF9494FF),
          onSwipe = { println("Downvote swiped") }
        )
      ),
      content = {
        ListItem()
      }
    )
  }
}

@Composable
private fun SwipeActionIcon(
  icon: ImageVector,
  name: String,
  lastName: MutableState<String?>,
) {
  val animate = name != lastName.value
    && name.endsWith("vote")
    && lastName.value?.endsWith("vote") == true
  var rotationDegrees by remember { mutableStateOf(if (animate) 180f else 0f) }

  Icon(
    modifier = Modifier
      .padding(16.dp)
      .rotate(rotationDegrees),
    imageVector = icon,
    contentDescription = null
  )

  LaunchedEffect(Unit) {
    if (animate) {
      Animatable(rotationDegrees).animateTo(targetValue = 0f) {
        rotationDegrees = value
      }
    }

    lastName.value = name
  }
}

@Composable
private fun ListItem() {
  Surface(tonalElevation = 2.dp) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        Modifier
          .size(52.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
      )

      Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "Title",
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          text = "Some description",
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}
