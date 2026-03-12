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
import androidx.compose.runtime.Composable
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
fun PrecautionsAndCareScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Precautions & Care",
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

            CareTopicItem(
                title = "Daily Flossing",
                description = "Floss daily to remove plaque and debris from between teeth, preventing gum disease.",
                imageRes = R.drawable.brush // Placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            CareTopicItem(
                title = "Healthy Diet",
                description = "Consume a balanced diet rich in fruits and vegetables to support overall oral health.",
                imageRes = R.drawable.brush // Placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            CareTopicItem(
                title = "Kit Maintenance",
                description = "Clean your toothbrush regularly and replace it every three months to maintain hygiene.",
                imageRes = R.drawable.brush // Placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))

            CareTopicItem(
                title = "Regular Rinsing",
                description = "Use mouthwash after brushing to kill bacteria and freshen breath, enhancing oral care.",
                imageRes = R.drawable.brush // Placeholder
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CareTopicItem(title: String, description: String, imageRes: Int) {
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
            Text("Learn More", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrecautionsAndCareScreenPreview() {
    PrecautionsAndCareScreen(onBackClick = {})
}
