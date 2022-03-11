# swipe

https://user-images.githubusercontent.com/2387680/157795117-3a343625-4a34-4303-bb28-d2deea7f9b68.mp4

A composable that can be swiped left or right for revealing actions.

```kotlin
val archive = SwipeAction(
  icon = painterResource(R.drawable.archive),
  background = Color.Green,
  onSwipe = { … }
)

val snooze = SwipeAction(
  icon = painterResource(R.drawable.snooze),
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
