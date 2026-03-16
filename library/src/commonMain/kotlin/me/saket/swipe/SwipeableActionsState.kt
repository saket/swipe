package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
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
  internal var swipeMaxDistancePx: Float by mutableFloatStateOf(0f)
  internal var swipeFrictionEasing: Easing by mutableStateOf(LinearEasing)
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

    // How to deal with the side that has multiple actions?
    // temporarily limit to enable friction for the side that only has one action
    val isAllowedForFriction = (targetOffset > 0f && actions.left.size == 1)
      || (targetOffset < 0f && actions.right.size == 1)

    if (swipeMaxDistancePx > 0f && isAllowed && isAllowedForFriction) {
      val progress = swipeFrictionEasing.transform(abs(offsetState.value) / swipeMaxDistancePx)
      val progressReverse = (1f - progress).coerceIn(0f, 1f)

      offsetState.value += delta * progressReverse
      return@DraggableState
    }

    offsetState.value += if (isAllowed) delta else delta / 10
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
