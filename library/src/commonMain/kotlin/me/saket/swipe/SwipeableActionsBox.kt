package me.saket.swipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A composable that can be swiped left or right for revealing actions.
 *
 * @param swipeThreshold Minimum drag distance before any [SwipeAction] is
 * activated and can be swiped.
 *
 * @param backgroundUntilSwipeThreshold Color drawn behind the content until
 * [swipeThreshold] is reached. When the threshold is passed, this color is
 * replaced by the currently visible [SwipeAction]'s background.
 */
@Composable
fun SwipeableActionsBox(
  modifier: Modifier = Modifier,
  state: SwipeableActionsState = rememberSwipeableActionsState(),
  startActions: List<SwipeAction> = emptyList(),
  endActions: List<SwipeAction> = emptyList(),
  swipeThreshold: Dp = 40.dp,
  backgroundUntilSwipeThreshold: Color = Color.DarkGray,
  content: @Composable BoxScope.() -> Unit
) = BoxWithConstraints(modifier) {
  state.also {
    it.layoutWidth = constraints.maxWidth
    it.swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    it.actions = remember(endActions, startActions, isRtl) {
      ActionFinder(
        left = if (isRtl) endActions else startActions,
        right = if (isRtl) startActions else endActions,
      )
    }
  }

  val backgroundColor: Color by animateColorAsState(
    when {
      state.swipedAction != null -> state.swipedAction!!.value.background
      !state.hasCrossedSwipeThreshold() -> backgroundUntilSwipeThreshold
      state.visibleAction != null -> state.visibleAction!!.value.background
      else -> Color.Transparent
    }
  )

  val scope = rememberCoroutineScope()
  Box(
    modifier = Modifier
      .absoluteOffset { IntOffset(x = state.offset.value.roundToInt(), y = 0) }
      .drawOverContent { state.ripple.draw(scope = this) }
      .draggable(
        orientation = Horizontal,
        enabled = !state.isResettingOnRelease,
        onDragStopped = {
          scope.launch {
            state.handleOnDragStopped()
          }
        },
        state = state.draggableState,
      ),
    content = content
  )

  (state.swipedAction ?: state.visibleAction)?.let { action ->
    ActionIconBox(
      modifier = Modifier.matchParentSize(),
      action = action,
      offset = state.offset.value,
      backgroundColor = backgroundColor,
      content = { action.value.icon() }
    )
  }
}

/**
 * A variant of SwipeableActionsBox with static icons that do not move while swiping.
 * @see SwipeableActionsBox
 */
@Composable
fun SwipeableActionsBoxStaticIcon(
  modifier: Modifier = Modifier,
  state: SwipeableActionsState = rememberSwipeableActionsState(),
  startActions: List<SwipeAction> = emptyList(),
  endActions: List<SwipeAction> = emptyList(),
  swipeThreshold: Dp = 40.dp,
  backgroundUntilSwipeThreshold: Color = Color.DarkGray,
  content: @Composable BoxScope.() -> Unit
) = BoxWithConstraints(modifier) {
  state.also {
    it.layoutWidth = constraints.maxWidth
    it.swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    it.actions = remember(endActions, startActions, isRtl) {
      ActionFinder(
        left = if (isRtl) endActions else startActions,
        right = if (isRtl) startActions else endActions,
      )
    }
  }

  val backgroundColor: Color by animateColorAsState(
    when {
      state.swipedAction != null -> state.swipedAction!!.value.background
      !state.hasCrossedSwipeThreshold() -> backgroundUntilSwipeThreshold
      state.visibleAction != null -> state.visibleAction!!.value.background
      else -> Color.Transparent
    }
  )

  val scope = rememberCoroutineScope()

  (state.swipedAction ?: state.visibleAction)?.let { action ->
    ActionIconBoxStaticIcon(
      modifier = Modifier.matchParentSize(),
      action = action,
      backgroundColor = backgroundColor,
      content = { action.value.icon() }
    )
  }

  Box(
    modifier = Modifier
      .absoluteOffset { IntOffset(x = state.offset.value.roundToInt(), y = 0) }
      .drawOverContent { state.ripple.draw(scope = this) }
      .draggable(
        orientation = Horizontal,
        enabled = !state.isResettingOnRelease,
        onDragStopped = {
          scope.launch {
            state.handleOnDragStopped()
          }
        },
        state = state.draggableState,
      ),
    content = content
  )
}

@Composable
private fun ActionIconBox(
  action: SwipeActionMeta,
  offset: Float,
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Row(
    modifier = modifier
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(width = placeable.width, height = placeable.height) {
          // Align icon with the left/right edge of the content being swiped.
          val iconOffset = if (action.isOnRightSide) constraints.maxWidth + offset else offset - placeable.width
          placeable.placeRelative(x = iconOffset.roundToInt(), y = 0)
        }
      }
      .background(color = backgroundColor),
    horizontalArrangement = if (action.isOnRightSide) Arrangement.Start else Arrangement.End,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    content()
  }
}

@Composable
private fun ActionIconBoxStaticIcon(
  action: SwipeActionMeta,
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Row(
    modifier = modifier.background(color = backgroundColor),
    horizontalArrangement = if (action.isOnRightSide) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    content()
  }
}

private fun Modifier.drawOverContent(onDraw: DrawScope.() -> Unit): Modifier {
  return drawWithContent {
    drawContent()
    onDraw(this)
  }
}
