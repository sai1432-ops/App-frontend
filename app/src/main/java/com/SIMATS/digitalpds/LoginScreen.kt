package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@Composable
fun LoginScreen(
    onUserLoginClick: () -> Unit,
    onDealerLoginClick: () -> Unit,
    onAdminLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Your Role",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Choose how you want to continue",
            fontSize = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Admin Role Button (New)
        RoleButton(
            title = "Admin",
            subtitle = "Oversee system operations and users",
            iconRes = R.drawable.gov,
            themeColor = Color(0xFFD32F2F), // Red
            onClick = onAdminLoginClick
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        // User Role Button
        RoleButton(
            title = "User",
            subtitle = "Access family dental health services",
            iconRes = R.drawable.user,
            themeColor = PrimaryBlue,
            onClick = onUserLoginClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Dealer Role Button
        RoleButton(
            title = "Dealer",
            subtitle = "Manage kit distribution and inventory",
            iconRes = R.drawable.dealer,
            themeColor = Color(0xFF2D6A6A), // Dark Green
            onClick = onDealerLoginClick
        )
    }
}

@Composable
fun RoleButton(
    title: String,
    subtitle: String,
    iconRes: Int,
    themeColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(16.dp),
                color = themeColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
            
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_play), // Placeholder arrow
                contentDescription = null,
                tint = themeColor.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    DigitalpdsTheme {
        LoginScreen(onUserLoginClick = {}, onDealerLoginClick = {}, onAdminLoginClick = {})
    }
}
