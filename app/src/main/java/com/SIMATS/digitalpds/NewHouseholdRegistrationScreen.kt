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

@Composable
private fun ImagePickerSection(
    label: String,
    selectedUri: Uri?,
    onPickImage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (selectedUri != null) "Change" else "Upload", fontSize = 14.sp)
        }
        if (selectedUri != null) {
            Text(
                "Selected",
                fontSize = 11.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RegistrationTextField(
    label: String,
    value: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    error: String? = null,
    enabled: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextGray.copy(alpha = 0.5f), fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            isError = error != null,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DealerGreen,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF9F9F9),
                unfocusedContainerColor = Color(0xFFF9F9F9),
                errorBorderColor = Color.Red
            ),
            shape = RoundedCornerShape(14.dp)
        )

        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RegistrationDropdown(
    label: String,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = when (label) {
        "Educational Level" -> listOf(
            "No Formal Education",
            "Primary",
            "Secondary",
            "Intermediate",
            "Graduate",
            "Post Graduate"
        )
        "Employment Status" -> listOf(
            "Unemployed",
            "Student",
            "Daily Wage",
            "Private Job",
            "Government Job",
            "Self Employed",
            "Retired"
        )
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedValue.isBlank()) "Select $label" else selectedValue,
                        color = if (selectedValue.isBlank()) TextGray.copy(alpha = 0.5f) else TextBlack,
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, 
                        contentDescription = null,
                        tint = DealerGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                option, 
                                fontSize = 14.sp,
                                fontWeight = if (selectedValue == option) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedValue == option) DealerGreen else TextBlack
                            ) 
                        },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GenderSelection(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf("Male", "Female", "Other").forEach { option ->
            val isSelected = selected == option
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { onSelected(option) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) DealerGreen else Color(0xFFF9F9F9),
                border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        option,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextGray
                    )
                }
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
