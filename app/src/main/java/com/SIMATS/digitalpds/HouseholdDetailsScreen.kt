package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

// --- Data Models ---
data class HouseholdDetail(
    val householdId: String,
    val fullName: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val education: String,
    val employment: String,
    val rationCard: String,
    val aadhaarMasked: String,
    val profileImage: Int,
    val familyMembers: List<FamilyMemberInfo>,
    val distributionHistory: List<DistributionRecordInfo>
)

data class FamilyMemberInfo(
    val name: String,
    val relation: String,
    val age: Int,
    val gender: String,
    val image: Int,
    val isActive: Boolean = true
)

data class DistributionRecordInfo(
    val date: String,
    val shipmentId: String,
    val items: String,
    val image: Int
)

// --- Mock Data Service ---
object HouseholdDataService {
    private val households = emptyMap<String, HouseholdDetail>()

    fun getHouseholdDetails(id: String): HouseholdDetail? {
        val cleanId = id.removePrefix("#")
        return households[cleanId]
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdDetailsScreen(householdId: String, onBack: () -> Unit) {
    val household = HouseholdDataService.getHouseholdDetails(householdId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        if (household == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No household details found.", color = TextGray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                Text("Household ID: ${household.householdId}", color = TextGray, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = household.profileImage),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(24.dp))

                // Personal Details Grid
                DetailRow("Full Name", household.fullName, "Phone", household.phone)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Age", household.age.toString(), "Gender", household.gender)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Education", household.education, "Employment", household.employment)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Ration Card No.", household.rationCard, "Aadhaar No.", household.aadhaarMasked)

                // Family Members Section
                Spacer(Modifier.height(32.dp))
                Text("Family Members (${household.familyMembers.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                household.familyMembers.forEach { member ->
                    FamilyMemberItem(member)
                }

                // Distribution History Section
                Spacer(Modifier.height(32.dp))
                Text("Distribution History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                if (household.distributionHistory.isEmpty()) {
                    Text("No distribution history found.", color = TextGray)
                } else {
                    household.distributionHistory.forEach { record ->
                        DistributionHistoryItem(record)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// --- Reusable Components ---

@Composable
private fun DetailRow(label1: String, value1: String, label2: String, value2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label1, fontSize = 14.sp, color = TextGray)
            Text(value1, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label2, fontSize = 14.sp, color = TextGray)
            Text(value2, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextBlack)
        }
    }
}

@Composable
private fun FamilyMemberItem(member: FamilyMemberInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = member.image),
            contentDescription = member.name,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${member.relation} • ${member.age} yrs • ${member.gender}", color = TextGray, fontSize = 14.sp)
        }
        if (member.isActive) {
            Box(Modifier.size(10.dp).background(Color(0xFF2E7D32), CircleShape))
        }
    }
}

@Composable
private fun DistributionHistoryItem(record: DistributionRecordInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(record.date, fontSize = 14.sp, color = TextGray)
            Text(record.shipmentId, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
            Text(record.items, fontSize = 14.sp, color = TextGray)
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F4F8))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Verified", color = DealerGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Check, contentDescription = "Verified", tint = DealerGreen, modifier = Modifier.size(16.dp))
            }
        }
        Image(
            painter = painterResource(id = record.image),
            contentDescription = "Kit Image",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HouseholdDetailsScreenPreview() {
    DigitalpdsTheme {
        HouseholdDetailsScreen(householdId = "HH-5612", onBack = {})
    }
}
