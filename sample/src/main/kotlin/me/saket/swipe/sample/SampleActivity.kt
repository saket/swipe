package me.saket.swipe.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.saket.swipe2.SwipeToReveal
import me.saket.swipe2.rememberSwipeToRevealState

class SampleActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val colors = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(this)
      } else {
        dynamicLightColorScheme(this)
      }
      MaterialTheme(colors) {
        Scaffold(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          floatingActionButton = {
            ComposeButton()
          },
        ) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
          ) {
            Text(
              text = "Inbox",
              color = MaterialTheme.colorScheme.secondary,
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
              verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
              itemsIndexed(MailThreads) { index, thread ->
                SwipeableEmailItem(
                  thread = thread,
                  index = index,
                  count = MailThreads.size,
                )
              }
              item {
                Spacer(Modifier.height(16.dp))
                Text(
                  text = "Limited swipe distance (200dp)",
                  color = MaterialTheme.colorScheme.secondary,
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
              itemsIndexed(MailThreads.take(2)) { index, thread ->
                SwipeToReveal(
                  state = rememberSwipeToRevealState(maxSwipeDistance = 200.dp)
                ) {
                  EmailItem(
                    thread = thread,
                    index = index,
                    count = 2,
                  )
                }
              }
              item {
                Spacer(Modifier.height(16.dp))
                Text(
                  text = "Limited swipe (50% of width)",
                  color = MaterialTheme.colorScheme.secondary,
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
              itemsIndexed(MailThreads.take(2)) { index, thread ->
                SwipeToReveal(
                  state = rememberSwipeToRevealState(maxSwipeFraction = 0.5f)
                ) {
                  EmailItem(
                    thread = thread,
                    index = index,
                    count = 2,
                  )
                }
              }
              item {
                Spacer(Modifier.height(72.dp))
              }
            }
          }
        }
      }
    }
  }

  @Composable
  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  private fun SwipeableEmailItem(thread: MailThread, index: Int, count: Int) {
    SwipeToReveal(
      state = rememberSwipeToRevealState()
    ) {
      EmailItem(
        thread = thread,
        index = index,
        count = count,
      )
    }
  }

  @Composable
  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  private fun EmailItem(thread: MailThread, index: Int, count: Int) {
    SegmentedListItem(
      modifier = Modifier.fillMaxWidth(),
      onClick = {},
      leadingContent = {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        )
      },
      content = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        ) {
          Text(
            text = thread.sender,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (thread.unread) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = thread.subject,
            fontWeight = if (thread.unread) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = thread.preview,
            color = LocalContentColor.current.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      },
      trailingContent = {
        Text(
          text = thread.time,
          style = MaterialTheme.typography.bodySmallEmphasized,
          color = LocalContentColor.current.copy(alpha = 0.7f),
        )
      },
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      shapes = ListItemDefaults.segmentedShapes(
        index = index,
        count = count,
      ),
    )
  }

  @Composable
  private fun ComposeButton() {
    ExtendedFloatingActionButton(
      modifier = Modifier.padding(bottom = 28.dp),
      containerColor = MaterialTheme.colorScheme.primary,
      onClick = {},
    ) {
      Icon(
        imageVector = Icons.Filled.Create,
        contentDescription = null,
      )
      Spacer(
        Modifier.width(8.dp)
      )
      Text(
        text = "Compose",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
      )
    }
  }

  private data class MailThread(
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
    val unread: Boolean,
  )

  companion object {
    private val MailThreads = listOf(
      MailThread(
        sender = "Annie, Rob, Jess, me",
        subject = "Trip to Helen’s",
        preview = "Woohoo! Helen sent a helpful pdf in case...",
        time = "Now",
        unread = true,
      ),
      MailThread(
        sender = "Transatlantic Air",
        subject = "Your itinerary is confirmed: BGZUAW",
        preview = "We’ve got you covered with all the details...",
        time = "11:35 AM",
        unread = true,
      ),
      MailThread(
        sender = "Times & Tribune",
        subject = "Your June issue arrives today!",
        preview = "Get ready for exciting summer stories arri...",
        time = "10:23 AM",
        unread = true,
      ),
      MailThread(
        sender = "Helen, Annie, me",
        subject = "Checking in",
        preview = "Hey Annie - Thanks for reaching out! Thin...",
        time = "10:11 AM",
        unread = false,
      ),
      MailThread(
        sender = "Maanika Manohoran",
        subject = "Revised organic search numbers",
        preview = "We foresee some big smiles ahead! We’ll...",
        time = "May 19",
        unread = false,
      ),
      MailThread(
        sender = "Addie Lane",
        subject = "It’s that time of year again...",
        preview = "Hi friends, it’s happening! Time to dust off...",
        time = "May 19",
        unread = false,
      ),
      MailThread(
        sender = "Justin, Curtis, Blake, me",
        subject = "Call Summary",
        preview = "That sounds perfect! Thank you again...",
        time = "May 20",
        unread = false,
      ),
      MailThread(
        sender = "Virtual Bank",
        subject = "Important update for your account",
        preview = "We’ve received your Virtual Signature a...",
        time = "May 19",
        unread = false,
      ),
    )
  }
}
