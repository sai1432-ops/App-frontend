package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@Composable
fun WelcomeScreen(onGetStartedClick: () -> Unit) {
    val softBlue = PrimaryBlue
    val cyanGradient = Color(0xFF00BCD4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logos Header (SSE and Saveetha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sse),
                    contentDescription = "SSE Logo",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
                
                Image(
                    painter = painterResource(id = R.drawable.saveetha),
                    contentDescription = "Saveetha Logo",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Govt Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gov),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("GOVERNMENT OF INDIA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGraySub, letterSpacing = 0.5.sp)
                    Text("Digital Health Mission", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Main Graphic (Clean and Minimalist)
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "Mukh Swasthya Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Text Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Welcome to",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = softBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    "Mukh Swasthya",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Smart oral healthcare for every citizen. Join the digital revolution in Indian dental care.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = textGraySub,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = softBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("GET STARTED", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                "Powered by SIMATS Engineering",
                fontSize = 12.sp,
                color = textGraySub,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    DigitalpdsTheme {
        WelcomeScreen(onGetStartedClick = {})
    }
}
