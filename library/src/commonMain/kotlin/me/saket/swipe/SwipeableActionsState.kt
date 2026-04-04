package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
  return remember { SwipeableActionsState() }
}

/**
 * The state of a [SwipeableActionsBox].
 */
@Stable
class SwipeableActionsState internal constructor() {
  /**
   * The current position (in pixels) of a [SwipeableActionsBox].
   */
  val offset: State<Float> get() = offsetState
  internal var offsetState = mutableStateOf(0f)

  /**
   * Whether [SwipeableActionsBox] is currently animating to reset its offset after it was swiped.
   */
  val isResettingOnRelease: Boolean by derivedStateOf {
    swipedAction != null
  }

  internal var layoutWidth: Int by mutableIntStateOf(0)
  internal var swipeThresholdPx: Float by mutableFloatStateOf(0f)
  internal var maxSwipeDistancePx: Float = Float.POSITIVE_INFINITY
  internal var maxSwipeFraction: Float = Float.NaN
  internal val ripple = SwipeRippleState()

  internal var actions: ActionFinder by mutableStateOf(
    ActionFinder(left = emptyList(), right = emptyList())
  )
  internal val visibleAction: SwipeActionMeta? by derivedStateOf {
    actions.actionAt(offsetState.value, totalWidth = layoutWidth)
  }
  internal var swipedAction: SwipeActionMeta? by mutableStateOf(null)

  internal val draggableState = DraggableState { delta ->
    val targetOffset = offsetState.value + delta

    val canSwipeTowardsRight = actions.left.isNotEmpty()
    val canSwipeTowardsLeft = actions.right.isNotEmpty()

    val isAllowed = isResettingOnRelease
      || targetOffset == 0f
      || (targetOffset > 0f && canSwipeTowardsRight)
      || (targetOffset < 0f && canSwipeTowardsLeft)

    val resolvedMaxPx = resolveMaxSwipePx(
      maxSwipeDistancePx = maxSwipeDistancePx,
      maxSwipeFraction = maxSwipeFraction,
      layoutWidth = layoutWidth,
    )
    val clampedOffset = if (isAllowed && resolvedMaxPx != Float.POSITIVE_INFINITY) {
      targetOffset.coerceIn(-resolvedMaxPx, resolvedMaxPx)
    } else if (isAllowed) {
      targetOffset
    } else {
      offsetState.value + delta / 10
    }

    offsetState.value = clampedOffset
  }

  internal fun hasCrossedSwipeThreshold(): Boolean {
    return abs(offsetState.value) > swipeThresholdPx
  }

  internal suspend fun handleOnDragStopped() = coroutineScope {
    launch {
      if (hasCrossedSwipeThreshold()) {
        visibleAction?.let { action ->
          swipedAction = action
          action.value.onSwipe()
          ripple.animate(action = action)
        }
      }
    }
    launch {
      draggableState.drag(MutatePriority.PreventUserInput) {
        Animatable(offsetState.value).animateTo(
          targetValue = 0f,
          animationSpec = tween(durationMillis = animationDurationMs),
        ) {
          dragBy(value - offsetState.value)
        }
      }
      swipedAction = null
    }
  }
}

internal fun resolveMaxSwipePx(
  maxSwipeDistancePx: Float,
  maxSwipeFraction: Float,
  layoutWidth: Int,
): Float {
  return when {
    !maxSwipeFraction.isNaN() -> layoutWidth * maxSwipeFraction
    maxSwipeDistancePx != Float.POSITIVE_INFINITY -> maxSwipeDistancePx
    else -> Float.POSITIVE_INFINITY
  }
}
