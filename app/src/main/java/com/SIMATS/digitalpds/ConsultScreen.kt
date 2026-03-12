package com.SIMATS.digitalpds

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.SIMATS.digitalpds.network.ClinicResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.BackgroundWhite
import com.SIMATS.digitalpds.ui.theme.PrimaryBlue
import com.SIMATS.digitalpds.ui.theme.TextBlack
import com.SIMATS.digitalpds.ui.theme.TextGray
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

data class DentalAppItem(
    val name: String,
    val websiteUrl: String,
    val logoRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultScreen(
    userId: Int,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onBookVisitClick: (ClinicResponse) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appScrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()

    var clinics by remember { mutableStateOf<List<ClinicResponse>>(emptyList()) }
    var isLoadingClinics by remember { mutableStateOf(false) }
    var selectedClinic by remember { mutableStateOf<ClinicResponse?>(null) }
    var hasSearchedNearby by remember { mutableStateOf(false) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val dentalApps = remember {
        listOf(
            DentalAppItem(
                name = "Clove Dental",
                websiteUrl = "https://clovedental.in/",
                logoRes = R.drawable.clove_logo
            ),
            DentalAppItem(
                name = "32 Dental Care",
                websiteUrl = "https://www.32dentalcare.org/",
                logoRes = R.drawable.dental32_logo
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun fetchNearbyClinics() {
        isLoadingClinics = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.getNearbyClinics(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                maxKm = 15.0
                            )

                            if (response.isSuccessful) {
                                clinics = response.body().orEmpty()
                            } else {
                                clinics = emptyList()
                                Toast.makeText(
                                    context,
                                    "Failed to fetch nearby clinics",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            clinics = emptyList()
                            Toast.makeText(
                                context,
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoadingClinics = false
                        }
                    }
                } else {
                    clinics = emptyList()
                    isLoadingClinics = false
                    Toast.makeText(
                        context,
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                clinics = emptyList()
                isLoadingClinics = false
                Toast.makeText(
                    context,
                    "Failed to get location",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasSearchedNearby = true
            fetchNearbyClinics()
        } else {
            Toast.makeText(
                context,
                "Location permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val filteredClinics = clinics.filter {
        it.clinicName.contains(searchQuery, ignoreCase = true) ||
                it.district.contains(searchQuery, ignoreCase = true) ||
                it.address.contains(searchQuery, ignoreCase = true)
    }

    selectedClinic?.let { clinic ->
        val mapQuery = buildString {
            append(clinic.clinicName)
            if (clinic.address.isNotBlank()) append(", ${clinic.address}")
            if (clinic.district.isNotBlank()) append(", ${clinic.district}")
        }

        val websiteSearchQuery = buildString {
            append(clinic.clinicName)
            if (clinic.district.isNotBlank()) append(" ${clinic.district}")
        }

        ModalBottomSheet(
            onDismissRequest = { selectedClinic = null },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = clinic.clinicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Text(
                    text = clinic.address.ifBlank { clinic.district },
                    fontSize = 13.sp,
                    color = TextGray
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedClinic = null
                            openGoogleMapsSearch(context, mapQuery)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Open in Maps", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedClinic = null
                            if (!clinic.contactNumber.isNullOrBlank()) {
                                openDialer(context, clinic.contactNumber)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Phone number not available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Call", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedClinic = null
                            openGoogleSearch(context, websiteSearchQuery)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Public, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Website / Search", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Consult",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            UserBottomNavigationBar(
                currentScreen = "Consult",
                onHomeClick = onHomeClick,
                onKitsClick = onKitsClick,
                onLearnClick = onLearnClick,
                onConsultClick = { },
                onProfileClick = onProfileClick
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = { Text("Find dental clinics, hospitals...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F4F8),
                        unfocusedContainerColor = Color(0xFFF1F4F8),
                        disabledContainerColor = Color(0xFFF1F4F8),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            item {
                Text(
                    text = "Dental Apps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .horizontalScroll(appScrollState),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    dentalApps.forEach { app ->
                        DentalAppCard(
                            item = app,
                            onClick = { openUrl(context, app.websiteUrl) }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Use Current Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Allow location access to find nearby dental clinics list on screen.",
                            fontSize = 13.sp,
                            color = TextGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        permission
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    hasSearchedNearby = true
                                    fetchNearbyClinics()
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Find Nearby Clinics")
                        }
                    }
                }
            }

            if (hasSearchedNearby) {
                item {
                    Text(
                        text = "Nearby Dental Clinics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                if (isLoadingClinics) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                } else if (filteredClinics.isEmpty()) {
                    item {
                        Text(
                            text = "No nearby clinics found",
                            fontSize = 14.sp,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    items(filteredClinics) { clinic ->
                        ClinicCard(
                            clinic = clinic,
                            onClick = {
                                selectedClinic = clinic
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DentalAppCard(
    item: DentalAppItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(128.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = item.logoRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextBlack,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ClinicCard(
    clinic: ClinicResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = clinic.clinicName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = clinic.district,
                fontSize = 13.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = clinic.distanceKm?.let { "${it} km away" } ?: clinic.address,
                fontSize = 13.sp,
                color = PrimaryBlue
            )

            if (clinic.address.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clinic.address,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tap to view options",
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}

fun openUrl(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun openDialer(context: Context, phone: String) {
    context.startActivity(
        Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:$phone")
        )
    )
}

fun openGoogleMapsSearch(context: Context, query: String) {
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
            )
        )
    }
}

fun openGoogleSearch(context: Context, query: String) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
        )
    )
}