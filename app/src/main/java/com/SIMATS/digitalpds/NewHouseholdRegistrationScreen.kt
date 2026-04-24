package com.SIMATS.digitalpds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHouseholdRegistrationScreen(
    onBackClick: () -> Unit,
    onConfirmRegistration: (String, String?, String, String, String, String, String, String, String, List<com.SIMATS.digitalpds.network.FamilyMemberInput>, Uri?, Uri?) -> Unit,
    isLoading: Boolean = false
) {
    val addedMembers = remember { mutableStateListOf<com.SIMATS.digitalpds.network.FamilyMemberInput>() }
    var pdsFrontUri by remember { mutableStateOf<Uri?>(null) }
    var pdsBackUri by remember { mutableStateOf<Uri?>(null) }

    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdsFrontUri = uri
    }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdsBackUri = uri
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // Top Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DealerGreen, BackgroundWhite)
                    )
                )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Add Beneficiary",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
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
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            "REGISTRATION FORM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = DealerGreen,
                            letterSpacing = 2.sp
                        )
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp).width(40.dp),
                            thickness = 3.dp,
                            color = DealerGreen.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BeneficiaryForm(
                            isLoading = isLoading,
                            submitButtonText = "CONFIRM & REGISTER HOUSEHOLD",
                            buttonColor = DealerGreen,
                            contentBeforeSubmit = {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "PDS Card Images",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DealerGreen
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ImagePickerSection("Front Side", pdsFrontUri) { frontLauncher.launch("image/*") }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        ImagePickerSection("Back Side", pdsBackUri) { backLauncher.launch("image/*") }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                HouseholdCompositionForm(addedMembers = addedMembers)
                            },
                            onSubmit = { name, email, phone, age, gender, education, employment, address, pdsCard ->
                                onConfirmRegistration(name, email, phone, age, gender, education, employment, address, pdsCard, addedMembers.toList(), pdsFrontUri, pdsBackUri)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewHouseholdRegistrationScreenPreview() {
    DigitalpdsTheme {
        NewHouseholdRegistrationScreen(
            onBackClick = {},
            onConfirmRegistration = { _, _, _, _, _, _, _, _, _, _, _, _ -> }
        )
    }
}

