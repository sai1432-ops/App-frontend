package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

data class AdminBeneficiaryDetail(
    val id: String,
    val name: String,
    val phone: String,
    val location: String,
    val address: String,
    val status: BeneficiaryStatus,
    val createdAt: String,
    val createdByRole: String, // "Dealer" or "User"
    val createdById: String,
    val createdByName: String
)

data class KitHistory(
    val id: String,
    val beneficiaryId: String,
    val kitName: String,
    val kitType: String,
    val quantity: String,
    val status: BeneficiaryStatus,
    val date: String,
    val givenBy: String,
    val trackingId: String? = null,
    val returnDate: String? = null,
    val returnReason: String? = null,
    val notes: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBeneficiaryDetailsScreen(
    beneficiaryId: String,
    onBackClick: () -> Unit = {}
) {
    // Mock data fetching based on beneficiaryId
    val beneficiary = remember(beneficiaryId) {
        AdminBeneficiaryDetail(
            id = beneficiaryId,
            name = "Alice Williams",
            phone = "+1 (555) 123-4567",
            location = "New York, NY",
            address = "123 Broadway, Manhattan, NY 10001",
            status = BeneficiaryStatus.GIVEN,
            createdAt = "15/02/2024",
            createdByRole = "Dealer",
            createdById = "D-1024",
            createdByName = "Central Dealer"
        )
    }

    val history = remember(beneficiaryId) {
        listOf(
            KitHistory(
                id = "K-001",
                beneficiaryId = beneficiaryId,
                kitName = "Starter Kit",
                kitType = "Standard",
                quantity = "1 Unit",
                status = BeneficiaryStatus.GIVEN,
                date = "15/02/2024",
                givenBy = "Central Dealer",
                notes = "Initial distribution"
            ),
            KitHistory(
                id = "K-002",
                beneficiaryId = beneficiaryId,
                kitName = "Refill Pack",
                kitType = "Monthly",
                quantity = "2 Units",
                status = BeneficiaryStatus.RETURNED,
                date = "10/01/2024",
                givenBy = "Admin",
                returnDate = "12/01/2024",
                returnReason = "Packaging damaged"
            )
        )
    }

    var selectedStatus by remember { mutableStateOf(beneficiary.status) }
    var adminNote by remember { mutableStateOf("") }
    var showStatusDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beneficiary Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = Color(0xFF37474F)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = beneficiary.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        StatusBadge(status = selectedStatus)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(beneficiary.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("ID: ${beneficiary.id}", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem(icon = Icons.Default.Phone, label = "Phone", value = beneficiary.phone)
                    DetailItem(icon = Icons.Default.LocationOn, label = "Location", value = beneficiary.location)
                    DetailItem(icon = Icons.Default.Home, label = "Address", value = beneficiary.address)
                    DetailItem(icon = Icons.Default.CalendarToday, label = "Created Date", value = beneficiary.createdAt)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = Color(0xFFF5F7F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Created By", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(
                                    if (beneficiary.createdByRole == "Dealer") Icons.Default.Storefront else Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${beneficiary.createdByName} (${beneficiary.createdByRole})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                            }
                        }
                    }
                }
            }

            // Kit History Section
            Text(
                "Kit Receiving History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            history.forEach { item ->
                HistoryCard(item)
            }

            // Admin Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Admin Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Update Status", fontSize = 12.sp, color = Color.Gray)
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        OutlinedButton(
                            onClick = { showStatusDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedStatus.name)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                        ) {
                            BeneficiaryStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = {
                                        selectedStatus = status
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = adminNote,
                        onValueChange = { adminNote = it },
                        label = { Text("Add Note") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* Save Changes */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Save Changes", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF90A4AE))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextBlack)
        }
    }
}

@Composable
fun HistoryCard(item: KitHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F4F4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(item.kitName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Text("Type: ${item.kitType}", fontSize = 12.sp, color = Color.Gray)
                }
                StatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Quantity", fontSize = 10.sp, color = Color.Gray)
                    Text(item.quantity, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Date", fontSize = 10.sp, color = Color.Gray)
                    Text(item.date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Given By: ", fontSize = 12.sp, color = Color.Gray)
                Text(item.givenBy, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }

            if (item.status == BeneficiaryStatus.RETURNED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Returned on ${item.returnDate}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        Text("Reason: ${item.returnReason}", fontSize = 12.sp, color = Color(0xFFC62828), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            if (!item.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes: ${item.notes}", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBeneficiaryDetailsScreenPreview() {
    AdminBeneficiaryDetailsScreen(beneficiaryId = "B001")
}
