# swipe

https://user-images.githubusercontent.com/2387680/157795117-3a343625-4a34-4303-bb28-d2deea7f9b68.mp4

`swipe` builds composables that can be swiped left or right for revealing actions. Unlike [SwipeToDismiss](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#SwipeToDismiss(androidx.compose.material.DismissState,androidx.compose.ui.Modifier,kotlin.collections.Set,kotlin.Function1,kotlin.Function1,kotlin.Function1)), it is designed for swipe actions that _do not_ dismiss their composable.

```groovy
implementation "me.saket.swipe:swipe:1.3.0"
```

```kotlin
val archive = SwipeAction(
  icon = rememberVectorPainter(Icons.TwoTone.Archive),
  background = Color.Green,
  onSwipe = { … }
)

val snooze = SwipeAction(
  icon = { Text("Snooze") },
  background = Color.Yellow,
  isUndo = true,
  onSwipe = { … },
)

SwipeableActionsBox(
  startActions = listOf(archive),
  endActions = listOf(snooze)
) {
  // Swipeable content goes here.
}
```

## License

```
Copyright 2022 Saket Narayan.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
