package com.SIMATS.digitalpds

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHouseholdRegistrationScreen(
    onBackClick: () -> Unit,
    onConfirmRegistration: (String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var education by remember { mutableStateOf("") }
    var employment by remember { mutableStateOf("") }
    var rationCard by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }

    var familyMemberName by remember { mutableStateOf("") }
    var familyMemberAge by remember { mutableStateOf("") }
    var familyMemberGender by remember { mutableStateOf("Male") }

    val addedMembers = remember { mutableStateListOf<String>() }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var rationCardError by remember { mutableStateOf<String?>(null) }
    var aadhaarError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    var familyMemberNameError by remember { mutableStateOf<String?>(null) }
    var familyMemberAgeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Household Registration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7FBFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "DEALER ADMINISTRATIVE TOOL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "1. PRIMARY BENEFICIARY",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegistrationTextField(
                label = "Full Name",
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameError = null
                },
                error = fullNameError
            )

            RegistrationTextField(
                label = "Phone Number (10-digit)",
                value = phone,
                keyboardType = KeyboardType.Phone,
                onValueChange = {
                    phone = it.filter { ch -> ch.isDigit() }.take(10)
                    phoneError = null
                },
                error = phoneError
            )

            RegistrationTextField(
                label = "Age",
                value = age,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    age = it.filter { ch -> ch.isDigit() }.take(3)
                    ageError = null
                },
                error = ageError
            )

            GenderSelection(gender) { gender = it }

            RegistrationDropdown(
                label = "Educational Level",
                selectedValue = education,
                onValueSelected = { education = it }
            )

            RegistrationDropdown(
                label = "Employment Status",
                selectedValue = employment,
                onValueSelected = { employment = it }
            )

            RegistrationTextField(
                label = "Ration Card No",
                value = rationCard,
                onValueChange = {
                    rationCard = it.uppercase()
                    rationCardError = null
                },
                error = rationCardError
            )

            RegistrationTextField(
                label = "Aadhaar Card No (12-digit)",
                value = aadhaar,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    aadhaar = it.filter { ch -> ch.isDigit() }.take(12)
                    aadhaarError = null
                },
                error = aadhaarError
            )

            RegistrationTextField(
                label = "Street Address",
                value = streetAddress,
                onValueChange = {
                    streetAddress = it
                    addressError = null
                },
                error = addressError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true

                    if (fullName.isBlank()) {
                        fullNameError = "Full name is required"
                        isValid = false
                    }

                    if (phone.length != 10) {
                        phoneError = "Enter valid 10-digit phone number"
                        isValid = false
                    }

                    if (age.isBlank()) {
                        ageError = "Age is required"
                        isValid = false
                    }

                    if (rationCard.isBlank()) {
                        rationCardError = "Ration card number is required"
                        isValid = false
                    }

                    if (aadhaar.isNotBlank() && aadhaar.length != 12) {
                        aadhaarError = "Aadhaar must be 12 digits"
                        isValid = false
                    }

                    if (streetAddress.isBlank()) {
                        addressError = "Street address is required"
                        isValid = false
                    }

                    if (isValid) {
                        onConfirmRegistration(fullName, rationCard)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    "CONFIRM & REGISTER HOUSEHOLD",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "2. HOUSEHOLD COMPOSITION",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegistrationTextField(
                label = "NUMBER OF FAMILY MEMBERS",
                value = addedMembers.size.toString(),
                keyboardType = KeyboardType.Number,
                onValueChange = {},
                enabled = false
            )

            Text(
                "Please include all family members residing at the same address.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "ADD FAMILY MEMBER",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegistrationTextField(
                label = "Name",
                value = familyMemberName,
                onValueChange = {
                    familyMemberName = it
                    familyMemberNameError = null
                },
                error = familyMemberNameError
            )

            RegistrationTextField(
                label = "Age",
                value = familyMemberAge,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    familyMemberAge = it.filter { ch -> ch.isDigit() }.take(3)
                    familyMemberAgeError = null
                },
                error = familyMemberAgeError
            )

            GenderSelection(familyMemberGender) { familyMemberGender = it }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        var isMemberValid = true

                        if (familyMemberName.isBlank()) {
                            familyMemberNameError = "Member name is required"
                            isMemberValid = false
                        }

                        if (familyMemberAge.isBlank()) {
                            familyMemberAgeError = "Member age is required"
                            isMemberValid = false
                        }

                        if (isMemberValid) {
                            addedMembers.add(
                                "$familyMemberName, Age: $familyMemberAge • $familyMemberGender"
                            )
                            familyMemberName = ""
                            familyMemberAge = ""
                            familyMemberGender = "Male"
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+ ADD TO LIST",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (addedMembers.isEmpty()) {
                Text(
                    "No family members added yet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                addedMembers.forEachIndexed { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            member,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { addedMembers.removeAt(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RegistrationTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    error: String? = null,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error != null,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE9F1F1),
                focusedContainerColor = Color(0xFFE9F1F1),
                disabledContainerColor = Color(0xFFE9F1F1),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp)
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
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
            "Graduate"
        )
        "Employment Status" -> listOf(
            "Unemployed",
            "Daily Wage",
            "Private Job",
            "Government Job",
            "Self Employed"
        )
        else -> emptyList()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFE9F1F1), RoundedCornerShape(8.dp))
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
                    text = if (selectedValue.isBlank()) label else selectedValue,
                    color = if (selectedValue.isBlank()) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenderSelection(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Male", "Female", "Other").forEach { option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(
                        if (selected == option) Color.LightGray.copy(alpha = 0.3f)
                        else Color(0xFFE9F1F1),
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
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
            onConfirmRegistration = { _, _ -> }
        )
    }
}