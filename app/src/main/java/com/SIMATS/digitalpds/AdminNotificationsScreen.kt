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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.digitalpds.network.AdminNotification
import com.SIMATS.digitalpds.network.AdminNotificationType
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationsScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: (AdminNotification) -> Unit = {},
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        val token = com.SIMATS.digitalpds.SessionManager(context).getAccessToken() ?: ""
        adminViewModel.fetchNotifications(token)
    }

    val notifications = adminViewModel.notifications
    val isLoading = adminViewModel.isLoading
    val errorMessage = adminViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TextBlack) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading && notifications.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFD32F2F))
            } else if (errorMessage != null && notifications.isEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No notifications yet", color = TextGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
        AdminNotificationType.STOCK_REQUEST -> {
            icon = Icons.Default.Inventory
            iconColor = Color(0xFF1976D2)
            iconBgColor = Color(0xFFE3F2FD)
        }
        AdminNotificationType.NEW_DEALER -> {
            icon = Icons.Default.PersonAdd
            iconColor = Color(0xFF2E7D32)
            iconBgColor = Color(0xFFE8F5E9)
        }
        AdminNotificationType.ALERT -> {
            icon = Icons.Default.Warning
            iconColor = Color(0xFFD32F2F)
            iconBgColor = Color(0xFFFFEBEE)
        }
        AdminNotificationType.GENERAL -> {
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
    DigitalpdsTheme {
        AdminNotificationsScreen(
            adminViewModel = viewModel()
        )
    }
}
