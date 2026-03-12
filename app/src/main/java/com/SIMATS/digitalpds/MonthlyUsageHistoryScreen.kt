package com.SIMATS.digitalpds

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

data class UsageFrequency(
    val name: String,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyUsageHistoryScreen(
    compliancePercentage: Int = 0,
    kitsConsumed: Int = 0,
    frequencies: List<UsageFrequency> = emptyList(),
    consumptionPoints: List<Float> = emptyList(),
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Monthly Usage History",
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

            // Compliance Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Family Compliance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextBlack
                    )
                    Text(
                        "$compliancePercentage%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        "Total Kits Consumed: $kitsConsumed",
                        fontSize = 14.sp,
                        color = TextBlack
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.fam),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Brushing Frequency",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (frequencies.isEmpty()) {
                Text(
                    "No brushing data available for this period.",
                    fontSize = 14.sp,
                    color = TextGray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                frequencies.forEach { freq ->
                    FrequencyBar(freq.name, freq.progress)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Supply Depletion",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Toothpaste Consumption",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (consumptionPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No consumption history found.", color = TextGray)
                }
            } else {
                ConsumptionGraph(consumptionPoints)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FrequencyBar(label: String, progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .background(Color(0xFFF5F7F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color(0xFFE0E0E0))
            )
        }
    }
}

@Composable
fun ConsumptionGraph(points: List<Float>) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (points.size < 2) return@Canvas
                
                val path = Path()
                val width = size.width
                val height = size.height
                val stepX = width / (points.size - 1)

                path.moveTo(0f, height * (1 - points[0]))
                
                for (i in 1 until points.size) {
                    val prevX = (i - 1) * stepX
                    val prevY = height * (1 - points[i - 1])
                    val currX = i * stepX
                    val currY = height * (1 - points[i])
                    
                    val midX = (prevX + currX) / 2
                    path.quadraticBezierTo(prevX, prevY, midX, (prevY + currY) / 2)
                }
                path.lineTo(width, height * (1 - points.last()))

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Jan", "Feb", "Mar", "Apr", "May").forEach { month ->
                Text(text = month, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthlyUsageHistoryScreenPreview() {
    DigitalpdsTheme {
        MonthlyUsageHistoryScreen(onBackClick = {})
    }
}
