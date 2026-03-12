package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    STOCK_REQUEST, NEW_DEALER, ALERT, GENERAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationsScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: (AdminNotification) -> Unit = {}
) {
    // Mock Notifications Data
    val notifications = listOf(
        AdminNotification(
            "1",
            "New Stock Request",
            "Dealer @jdoe_dealer has requested 500 Adult Brushes.",
            "2 mins ago",
            NotificationType.STOCK_REQUEST,
            isRead = false
        ),
        AdminNotification(
            "2",
            "Dealer Registration",
            "New dealer application received from Robert Smith.",
            "1 hour ago",
            NotificationType.NEW_DEALER,
            isRead = false
        ),
        AdminNotification(
            "3",
            "Low Stock Alert",
            "Central warehouse is running low on Fluoride Paste.",
            "3 hours ago",
            NotificationType.ALERT,
            isRead = true
        ),
        AdminNotification(
            "4",
            "System Update",
            "DigitalPDS version 2.1.0 is now live with new reporting features.",
            "Yesterday",
            NotificationType.GENERAL,
            isRead = true
        ),
        AdminNotification(
            "5",
            "Stock Request Approved",
            "Stock request #SR-9921 for Dealer @jsmith has been dispatched.",
            "2 days ago",
            NotificationType.STOCK_REQUEST,
            isRead = true
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification) }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: AdminNotification,
    onClick: () -> Unit
) {
    val icon: ImageVector
    val iconColor: Color
    val iconBgColor: Color

    when (notification.type) {
        NotificationType.STOCK_REQUEST -> {
            icon = Icons.Default.Inventory
            iconColor = Color(0xFF1976D2)
            iconBgColor = Color(0xFFE3F2FD)
        }
        NotificationType.NEW_DEALER -> {
            icon = Icons.Default.PersonAdd
            iconColor = Color(0xFF2E7D32)
            iconBgColor = Color(0xFFE8F5E9)
        }
        NotificationType.ALERT -> {
            icon = Icons.Default.Warning
            iconColor = Color(0xFFD32F2F)
            iconBgColor = Color(0xFFFFEBEE)
        }
        NotificationType.GENERAL -> {
            icon = Icons.Default.Notifications
            iconColor = Color(0xFF7B1FA2)
            iconBgColor = Color(0xFFF3E5F5)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF0F7FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = if (notification.isRead) FontWeight.Bold else FontWeight.ExtraBold,
                        color = TextBlack
                    )
                    if (!notification.isRead) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = "Unread",
                            modifier = Modifier.size(8.dp),
                            tint = Color(0xFF2196F3)
                        )
                    }
                }
                
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = TextGray,
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 20.sp
                )
                
                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminNotificationsScreenPreview() {
    AdminNotificationsScreen()
}
