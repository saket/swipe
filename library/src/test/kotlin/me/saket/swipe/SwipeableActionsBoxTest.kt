@file:Suppress("TestFunctionName")

package me.saket.swipe

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ReplyAll
import androidx.compose.material.icons.twotone.Snooze
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.androidHome
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import org.junit.Rule
import org.junit.Test

class SwipeableActionsBoxTest {
  @get:Rule val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL_5,
    showSystemUi = false,
    renderingMode = RenderingMode.SHRINK,
  )

  @Test fun `empty actions`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox {
          BatmanIpsumItem()
        }
      }
    }
  }

  @Test fun `non-empty actions`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          startActions = listOf(replyAll),
          endActions = listOf(snooze),
          content = { BatmanIpsumItem() },
        )
      }
    }
  }

  @Test fun `show a placeholder background until swipe threshold is reached`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          state = rememberSwipeActionsState(initialOffset = 30.dp),
          startActions = listOf(snooze),
          swipeThreshold = 40.dp,
          backgroundUntilSwipeThreshold = Color.GraySuit,
          content = { BatmanIpsumItem(background = Color.Unspecified) }
        )
      }
    }
  }

  @Test fun `distribute widths to actions according on their weights`() {
    paparazzi.snapshot {
      Scaffold {
        val boxWidth = maxWidth
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          val actions = listOf(
            snooze.copy(weight = 0.4),
            replyAll.copy(weight = 0.6)
          )

          SwipeableActionsBox(
            startActions = actions,
            state = rememberSwipeActionsState(initialOffset = boxWidth * 0.4f),
            content = { BatmanIpsumItem() }
          )

          SwipeableActionsBox(
            startActions = actions,
            state = rememberSwipeActionsState(initialOffset = boxWidth * 0.6f),
            content = { BatmanIpsumItem() }
          )
        }
      }
    }
  }

  @Test fun `swipe action's background should not be drawn behind content`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          state = rememberSwipeActionsState(initialOffset = 80.dp),
          startActions = listOf(snooze),
          swipeThreshold = 40.dp,
          backgroundUntilSwipeThreshold = Color.GraySuit,
          content = { BatmanIpsumItem(background = Color.Unspecified) }
        )
      }
    }
  }

  @Test fun `show last swipe action even when swipe distance exceeds content width`() {
    paparazzi.snapshot {
      Scaffold {
        SwipeableActionsBox(
          modifier = Modifier
            .width(200.dp)
            .requiredHeight(100.dp),
          state = rememberSwipeActionsState(initialOffset = (-210).dp),
          endActions = listOf(snooze),
          swipeThreshold = 40.dp,
          backgroundUntilSwipeThreshold = Color.GraySuit,
          content = { BatmanIpsumItem() }
        )
      }
    }
  }

  @Composable
  private fun Scaffold(content: @Composable BoxWithConstraintsScope.() -> Unit) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Whisper),
      content = content,
      contentAlignment = Alignment.Center
    )
  }

  @Composable
  private fun BatmanIpsumItem(
    modifier: Modifier = Modifier,
    background: Color = Color.White
  ) {
    Row(
      modifier
        .fillMaxWidth()
        .shadow(if (background.isSpecified) 1.dp else 0.dp)
        .background(background)
        .padding(20.dp)
        .animateContentSize()
    ) {
      Box(
        Modifier
          .padding(top = 4.dp)
          .size(52.dp)
          .clip(CircleShape)
          .background(Color(0xFF6B4FA9))
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
      }
    }
  }

  @Composable
  fun rememberSwipeActionsState(initialOffset: Dp): SwipeableActionsState {
    return rememberSwipeableActionsState().also {
      it.offsetState.value = LocalDensity.current.run { initialOffset.toPx() }
    }
  }

  companion object {
    val snooze
      @Composable get() = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Snooze),
        background = Color.SeaBuckthorn,
        onSwipe = {},
      )

    val replyAll
      @Composable get() = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.ReplyAll),
        background = Color.Perfume,
        onSwipe = {},
      )
  }
}

val Color.Companion.Whisper get() = Color(0XFFF8F5FA)
val Color.Companion.SeaBuckthorn get() = Color(0xFFF9A825)
val Color.Companion.Perfume get() = Color(0xFFD0BCFF)
val Color.Companion.GraySuit get() = Color(0xFFC1BAC9)
