package me.saket.swipe2

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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isUnspecified
import me.saket.swipe.animationDurationMs
import me.saket.swipe.resolveMaxSwipePx
import kotlin.math.abs

@Composable
fun rememberSwipeToRevealState(
  maxSwipeDistance: Dp = Dp.Unspecified,
  maxSwipeFraction: Float = Float.NaN,
): SwipeToRevealState {
  return remember {
    RealSwipeToRevealState()
  }.also {
    val real = it as RealSwipeToRevealState
    val maxSwipeDistancePx = if (maxSwipeDistance.isUnspecified) {
      Float.POSITIVE_INFINITY
    } else {
      with(LocalDensity.current) { maxSwipeDistance.toPx() }
    }
    real.resolvedMaxPx = resolveMaxSwipePx(
      maxSwipeDistancePx = maxSwipeDistancePx,
      maxSwipeFraction = maxSwipeFraction,
      layoutWidth = real.layoutWidth,
    )
  }
}

@Stable
sealed interface SwipeToRevealState {
  // todo: doc
  // todo: what's the benefit of keeping this a State<Float> and not a <Float>?
  // todo: why is this a float? aren't pixels always ints?
  // Range: -width to +width
  val offset: State<Float>
}

internal class RealSwipeToRevealState : SwipeToRevealState {
  override var offset = mutableFloatStateOf(0f)
  var resolvedMaxPx: Float = Float.POSITIVE_INFINITY
  var layoutWidth: Int = 0

  val isResettingOnRelease: Boolean by derivedStateOf {
    //swipedAction != null
    false
  }

  val draggableState = DraggableState { delta ->
    val targetOffset = offset.value + delta

    // todo:
    val canSwipeTowardsRight = true //actions.left.isNotEmpty()
    val canSwipeTowardsLeft = true //actions.right.isNotEmpty()

    val isAllowed = isResettingOnRelease
      || targetOffset == 0f
      || (targetOffset > 0f && canSwipeTowardsRight)
      || (targetOffset < 0f && canSwipeTowardsLeft)

    val effectiveDelta = if (isAllowed && resolvedMaxPx != Float.POSITIVE_INFINITY) {
      val absTarget = abs(targetOffset)
      if (absTarget > resolvedMaxPx) {
        val overflow = absTarget - resolvedMaxPx
        delta * (1f / (1f + overflow / resolvedMaxPx))
      } else {
        delta
      }
    } else if (isAllowed) {
      delta
    } else {
      delta / 10
    }

    offset.value += effectiveDelta
  }

  suspend fun handleOnDragStopped() {
    draggableState.drag(MutatePriority.PreventUserInput) {
      Animatable(offset.value).animateTo(
        targetValue = 0f,
        animationSpec = tween(durationMillis = animationDurationMs),
      ) {
        dragBy(value - offset.value)
      }
    }
  }
}
