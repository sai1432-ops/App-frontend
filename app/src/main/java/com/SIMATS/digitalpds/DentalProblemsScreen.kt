package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalProblemsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Dental Problems",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = false, onClick = {}, label = { Text("Gums") })
                FilterChip(selected = false, onClick = {}, label = { Text("Teeth") })
                FilterChip(selected = false, onClick = {}, label = { Text("Kids") })
            }

            Spacer(modifier = Modifier.height(24.dp))

            DentalProblemItem(
                title = "Cavities",
                description = "Cavities are permanently damaged areas in teeth that develop into tiny holes. They are caused by a combination of factors, including bacteria in your mouth, frequent snacking, sipping sugary drinks, and not cleaning your teeth well.",
                imageRes = R.drawable.hobot // Using placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            DentalProblemItem(
                title = "Gingivitis (Gum Disease)",
                description = "Gingivitis is a common and mild form of gum disease (periodontal disease) that causes irritation, redness and swelling (inflammation) of your gingiva, the part of your gum around the base of your teeth.",
                imageRes = R.drawable.hobot // Using placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            DentalProblemItem(
                title = "Tooth Sensitivity",
                description = "Tooth sensitivity, also known as dentin hypersensitivity, is a common condition that causes pain or discomfort in your teeth in response to certain stimuli, such as hot or cold temperatures, sweet or sour foods, or brushing.",
                imageRes = R.drawable.hobot // Using placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            DentalProblemItem(
                title = "Mouth Sores",
                description = "Mouth sores are small, painful lesions that can develop on the inside of your mouth, including your cheeks, gums, tongue, or the roof of your mouth. They can be caused by a variety of factors, such as stress, injury, or certain medical conditions.",
                imageRes = R.drawable.hobot // Using placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DentalProblemItem(title: String, description: String, imageRes: Int) {
    Column {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = TextGray,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("View Precautions", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DentalProblemsScreenPreview() {
    DentalProblemsScreen(onBackClick = {})
}
