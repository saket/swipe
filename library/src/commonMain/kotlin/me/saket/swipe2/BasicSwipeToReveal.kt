package me.saket.swipe2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch
import me.saket.swipe.horizontalDraggable
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun BasicSwipeToReveal(
  state: SwipeToRevealState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingActions: @Composable RowScope.() -> Unit,
  trailingActions: @Composable RowScope.() -> Unit,
  content: @Composable BasicSwipeToRevealScope.() -> Unit,
) {
  check(state is RealSwipeToRevealState)  // SwipeToRevealState is sealed.

  val leadingActionsRow = @Composable {
    Row {
      leadingActions()
    }
  }

  val trailingActionsRow = @Composable {
    Row {
      trailingActions()
    }
  }

  val contentWithScope = @Composable {
    val coroutineScope = rememberCoroutineScope()
    Box(
      // todo: does this need a fillMaxWidth()?
      Modifier.horizontalDraggable(
        enabled = enabled,
        state = state.draggableState,
        //startDragImmediately = TODO(),  // todo: can this be added?
        onDragStopped = {
          coroutineScope.launch {
            state.handleOnDragStopped()
          }
        },
      )
    ) {
      content(
        RealBasicSwipeToRevealScope(this)
      )
    }
  }

  Layout(
    modifier = modifier,
    content = {
      leadingActionsRow()
      trailingActionsRow()
      contentWithScope()
    }
  ) { measurables, constraints ->
    val (
      leadingMeasurable,
      trailingMeasurable,
      contentMeasurable,
    ) = measurables

    val contentPlaceable = contentMeasurable.measure(constraints)
    state.layoutWidth = contentPlaceable.width
    val offsetAsInt = state.offset.value.roundToInt()

    val actionsConstraints = Constraints(
      maxWidth = abs(offsetAsInt),
      maxHeight = contentPlaceable.height,
    )
    val leadingPlaceable = if (offsetAsInt > 0f) {
      leadingMeasurable.measure(actionsConstraints)
    } else {
      null
    }
    val trailingPlaceable = if (offsetAsInt < 0f) {
      trailingMeasurable.measure(actionsConstraints)
    } else {
      null
    }
    layout(contentPlaceable.width, contentPlaceable.height) {
      leadingPlaceable?.place(x = 0, y = 0)
      trailingPlaceable?.place(x = contentPlaceable.width - (-offsetAsInt), y = 0)
      contentPlaceable.place(x = offsetAsInt, y = 0)
    }
  }
}

interface BasicSwipeToRevealScope : BoxScope {
}

private class RealBasicSwipeToRevealScope(
  private val boxScope: BoxScope
) : BasicSwipeToRevealScope, BoxScope by boxScope {

}

//private fun Modifier.absoluteOffsetWithSize(
//  offset: (size: IntSize) -> IntOffset
//): Modifier {
//  return this.layout { measurable, constraints ->
//    val placeable = measurable.measure(constraints)
//    layout(placeable.width, placeable.height) {
//      val xOffset = offset(IntSize(placeable.width, placeable.height)).x
//      placeable.placeWithLayer(x = xOffset, y = 0)
//    }
//  }
//}
