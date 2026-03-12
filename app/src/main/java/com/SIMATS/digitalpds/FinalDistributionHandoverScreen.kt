package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalDistributionHandoverScreen(
    onBackClick: () -> Unit,
    onCompleteClick: (Int, Int, Int, Int) -> Unit
) {
    var adultBrushes by remember { mutableIntStateOf(0) }
    var childBrushes by remember { mutableIntStateOf(0) }
    var fluoridePaste by remember { mutableIntStateOf(0) }
    var iecPamphlets by remember { mutableIntStateOf(0) }

    val isButtonEnabled = adultBrushes > 0 || childBrushes > 0 || fluoridePaste > 0 || iecPamphlets > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Final Distribution Handover",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE9EEF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Household ID: ****5612",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = "Head: Rajesh Kumar",
                        fontSize = 15.sp,
                        color = TextBlack
                    )
                    Text(
                        text = "Category: PHH (Primary Household)",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Kits for Handover",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            DistributionItemRow(
                name = "Adult Brushes",
                count = adultBrushes,
                onIncrease = { adultBrushes++ },
                onDecrease = { if (adultBrushes > 0) adultBrushes-- }
            )
            DistributionItemRow(
                name = "Child Brushes",
                count = childBrushes,
                onIncrease = { childBrushes++ },
                onDecrease = { if (childBrushes > 0) childBrushes-- }
            )
            DistributionItemRow(
                name = "Fluoride Paste",
                count = fluoridePaste,
                onIncrease = { fluoridePaste++ },
                onDecrease = { if (fluoridePaste > 0) fluoridePaste-- }
            )
            DistributionItemRow(
                name = "IEC Pamphlets",
                count = iecPamphlets,
                onIncrease = { iecPamphlets++ },
                onDecrease = { if (iecPamphlets > 0) iecPamphlets-- }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Old Kit Return: Verified",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DealerGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DealerGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onCompleteClick(adultBrushes, childBrushes, fluoridePaste, iecPamphlets) },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    "Complete Distribution & Generate Receipt",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DistributionItemRow(
    name: String,
    count: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = name,
                fontSize = 14.sp,
                color = TextGray
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F4F8))
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextBlack)
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onIncrease,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F4F8))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextBlack)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalDistributionHandoverScreenPreview() {
    DigitalpdsTheme {
        FinalDistributionHandoverScreen(onBackClick = {}, onCompleteClick = { _, _, _, _ -> })
    }
}