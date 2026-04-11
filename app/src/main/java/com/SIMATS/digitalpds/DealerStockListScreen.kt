package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.StockItem
import com.SIMATS.digitalpds.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerStockListScreen(
    isLoading: Boolean,
    stockItems: List<StockItem>,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Top Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, BackgroundWhite)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Stock Details", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DealerGreen)
                }
            } else if (stockItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No detailed stock records found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(stockItems) { item ->
                        StockDetailCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun StockDetailCard(item: StockItem) {
    val visuals = getItemVisuals(item.itemName)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(visuals.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            visuals.icon, 
                            contentDescription = null, 
                            modifier = Modifier.size(24.dp), 
                            tint = DealerGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            item.itemName.replace("_", " ").uppercase(Locale.ROOT),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Text(
                            "Category: ${visuals.category}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
                
                if (!item.status.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getStatusColor(item.status).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = item.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = getStatusColor(item.status)
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Stock Quantity", fontSize = 12.sp, color = TextGray)
                    Text("${item.quantity ?: 0} Units", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DealerGreen)
                }
                
                if (item.requestedQuantity != null && item.requestedQuantity > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Requested", fontSize = 12.sp, color = TextGray)
                        Text("${item.requestedQuantity} Units", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    }
                }
            }
            
            if (!item.requestedAt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Last updated: ${item.requestedAt}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pending" -> Color(0xFFFFA000)
        "approved", "delivered", "active" -> Color(0xFF388E3C)
        "dispatched", "shipped" -> Color(0xFF1976D2)
        "rejected", "cancelled" -> Color(0xFFD32F2F)
        else -> DealerGreen
    }
}

private data class ItemVisuals(val name: String, val category: String, val icon: ImageVector, val color: Color)

private fun getItemVisuals(name: String): ItemVisuals {
    val upperName = name.trim().uppercase(Locale.ROOT)
    return when {
        upperName.contains("BRUSH") ->
            ItemVisuals("Brush", "Oral Care", Icons.Default.Brush, Color(0xFFE3F2FD))
            
        upperName.contains("PASTE") || upperName.contains("TOOTHPASTE") -> 
            ItemVisuals("Toothpaste", "Oral Care", Icons.Default.CleanHands, Color(0xFFFFF3E0))
            
        upperName.contains("FLYER") || upperName.contains("IEC") || upperName.contains("FLYERS") -> 
            ItemVisuals("IEC Material", "Education", Icons.Default.Description, Color(0xFFF3E5F5))
            
        else -> 
            ItemVisuals(name, "General", Icons.Default.Inventory2, Color(0xFFF5F5F5))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDealerStockListScreen() {
    val sampleStockItems = listOf(
        StockItem(itemName = "Brush", quantity = 50, status = "Approved", requestedAt = "Oct 27, 2023"),
        StockItem(itemName = "Paste", quantity = 30, requestedQuantity = 20, status = "Pending", requestedAt = "Oct 28, 2023"),
        StockItem(itemName = "Flyer", quantity = 100, status = "Dispatched", requestedAt = "Oct 26, 2023")
    )
    
    DigitalpdsTheme {
        DealerStockListScreen(
            isLoading = false,
            stockItems = sampleStockItems,
            onBackClick = {}
        )
    }
}
