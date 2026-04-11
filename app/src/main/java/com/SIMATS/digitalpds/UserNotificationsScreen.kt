package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.DigitalpdsTheme
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import com.SIMATS.digitalpds.ui.theme.TextBlack
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class UserNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType
)

enum class NotificationType {
    INFO, WARNING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNotificationsScreen(
    onBackClick: () -> Unit,
    morningCheckedIn: Boolean,
    eveningCheckedIn: Boolean,
    currentTime: LocalTime = LocalTime.now()
) {
    val notifications = remember(morningCheckedIn, eveningCheckedIn, currentTime) {
        generateDailyNotifications(
            morningCheckedIn = morningCheckedIn,
            eveningCheckedIn = eveningCheckedIn,
            currentTime = currentTime
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        if (notifications.isEmpty()) {
            EmptyNotificationsView(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}

fun generateDailyNotifications(
    morningCheckedIn: Boolean,
    eveningCheckedIn: Boolean,
    currentTime: LocalTime
): List<UserNotification> {
    val notifications = mutableListOf<UserNotification>()

    val morningReminderTime = LocalTime.of(7, 0)
    val morningMissedTime = LocalTime.of(10, 0)

    val eveningReminderTime = LocalTime.of(19, 0)
    val eveningMissedTime = LocalTime.of(22, 0)

    if (!morningCheckedIn) {
        if (currentTime.isBefore(morningMissedTime)) {
            notifications.add(
                UserNotification(
                    id = 1,
                    title = "Morning Brushing Reminder",
                    message = "Good morning! Please brush your teeth and complete your morning check-in.",
                    time = formatTime(morningReminderTime),
                    type = NotificationType.INFO
                )
            )
        } else {
            notifications.add(
                UserNotification(
                    id = 2,
                    title = "Morning Check-in Missed",
                    message = "You missed your morning brushing check-in today. Please try to maintain your oral care routine.",
                    time = formatTime(morningMissedTime),
                    type = NotificationType.WARNING
                )
            )
        }
    }

    if (!eveningCheckedIn && !currentTime.isBefore(eveningReminderTime)) {
        if (currentTime.isBefore(eveningMissedTime)) {
            notifications.add(
                UserNotification(
                    id = 3,
                    title = "Evening Brushing Reminder",
                    message = "It is time for your evening brushing. Please brush your teeth and complete your evening check-in.",
                    time = formatTime(eveningReminderTime),
                    type = NotificationType.INFO
                )
            )
        } else {
            notifications.add(
                UserNotification(
                    id = 4,
                    title = "Evening Check-in Missed",
                    message = "You missed your evening brushing check-in today. Stay consistent for better dental health.",
                    time = formatTime(eveningMissedTime),
                    type = NotificationType.WARNING
                )
            )
        }
    }

    return notifications
}

fun formatTime(time: LocalTime): String {
    return time.format(DateTimeFormatter.ofPattern("hh:mm a"))
}

@Composable
fun NotificationItem(notification: UserNotification) {
    val icon = when (notification.type) {
        NotificationType.INFO -> Icons.Outlined.Info
        NotificationType.WARNING -> Icons.Outlined.Warning
    }

    val iconColor = when (notification.type) {
        NotificationType.INFO -> PrimaryBlue
        NotificationType.WARNING -> Color(0xFFFF9800)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No notifications for now",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your daily brushing updates will appear here.",
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserNotificationsScreenMorningPreview() {
    DigitalpdsTheme {
        UserNotificationsScreen(
            onBackClick = {},
            morningCheckedIn = false,
            eveningCheckedIn = false,
            currentTime = LocalTime.of(8, 0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserNotificationsScreenMorningMissedPreview() {
    DigitalpdsTheme {
        UserNotificationsScreen(
            onBackClick = {},
            morningCheckedIn = false,
            eveningCheckedIn = false,
            currentTime = LocalTime.of(11, 0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserNotificationsScreenEveningPreview() {
    DigitalpdsTheme {
        UserNotificationsScreen(
            onBackClick = {},
            morningCheckedIn = true,
            eveningCheckedIn = false,
            currentTime = LocalTime.of(20, 0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserNotificationsScreenAllDonePreview() {
    DigitalpdsTheme {
        UserNotificationsScreen(
            onBackClick = {},
            morningCheckedIn = true,
            eveningCheckedIn = true,
            currentTime = LocalTime.of(21, 0)
        )
    }
}
