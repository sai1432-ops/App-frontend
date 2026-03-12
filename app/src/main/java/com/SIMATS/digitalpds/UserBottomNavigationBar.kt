package com.SIMATS.digitalpds

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.SIMATS.digitalpds.ui.theme.BackgroundWhite
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue

@Composable
fun UserBottomNavigationBar(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onKitsClick: () -> Unit,
    onLearnClick: () -> Unit,
    onConsultClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = BackgroundWhite,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItemData("Home", Icons.Filled.Home, Icons.Outlined.Home, onHomeClick),
            NavigationItemData("Kits", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, onKitsClick),
            NavigationItemData("Learn", Icons.Filled.School, Icons.Outlined.School, onLearnClick),
            NavigationItemData("Consult", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, onConsultClick),
            NavigationItemData("Profile", Icons.Filled.Person, Icons.Outlined.Person, onProfileClick)
        )

        items.forEach { item ->
            val isSelected = currentScreen == item.label
            NavigationBarItem(
                selected = isSelected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}

private data class NavigationItemData(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
