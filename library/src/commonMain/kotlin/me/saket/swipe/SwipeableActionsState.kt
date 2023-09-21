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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
  return remember { SwipeableActionsState() }.apply {
    density = LocalDensity.current
  }
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
  internal var swipeThreshold: Dp by mutableStateOf(0.dp)
  internal val ripple = SwipeRippleState()
  internal lateinit var density: Density

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

    // Add some resistance if needed.
    val isAllowed = (targetOffset > 0f && canSwipeTowardsRight) || (targetOffset < 0f && canSwipeTowardsLeft)
    offsetState.value += if (isAllowed) delta else delta / 10
  }

  internal fun hasCrossedSwipeThreshold(): Boolean {
    return abs(offsetState.value) > density.run { swipeThreshold.toPx() }
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
