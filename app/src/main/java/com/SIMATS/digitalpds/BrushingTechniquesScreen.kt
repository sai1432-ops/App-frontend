package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
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
fun BrushingTechniquesScreen(
    onBackClick: () -> Unit,
    onVideoClick: (BrushingVideo) -> Unit = {}
) {
    val videos = listOf(
        BrushingVideo("The 45-Degree Angle Technique", "3m 45s", "Dr. Anjali Rao", R.drawable.brush),
        BrushingVideo("Circular Motion Brushing", "2m 30s", "Dr. Vikram Patel", R.drawable.brush),
        BrushingVideo("Cleaning Back Molars", "4m 15s", "Dr. Meera Sharma", R.drawable.brush),
        BrushingVideo("Tongue Cleaning Guide", "1m 50s", "Dr. Anjali Rao", R.drawable.brush),
        BrushingVideo("Brushing for Braces", "5m 10s", "Dr. Vikram Patel", R.drawable.brush),
        BrushingVideo("Proper Brushing Tutorial", "2m 15s", "Expert Guide", R.drawable.brush, "https://youtu.be/7kGXQDwT6IA?si=NMsHCQTXxlB2CKLm")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Brushing Techniques",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                Text(
                    "All Brushing Videos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Master your oral hygiene with these expert-led tutorials.",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            items(videos) { video ->
                BrushingVideoItem(video, onVideoClick)
            }
        }
    }
}

data class BrushingVideo(
    val title: String,
    val duration: String,
    val doctor: String,
    val thumbnailRes: Int,
    val videoUrl: String = ""
)

@Composable
fun BrushingVideoItem(video: BrushingVideo, onClick: (BrushingVideo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(video) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = video.thumbnailRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "${video.duration} · ${video.doctor}",
                fontSize = 14.sp,
                color = TextGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrushingTechniquesScreenPreview() {
    DigitalpdsTheme {
        BrushingTechniquesScreen(onBackClick = {})
    }
}
