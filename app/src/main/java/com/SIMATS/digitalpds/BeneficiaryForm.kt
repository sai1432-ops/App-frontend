package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*

@Composable
fun BeneficiaryForm(
    isLoading: Boolean,
    submitButtonText: String,
    buttonColor: Color,
    contentBeforeSubmit: @Composable () -> Unit = {},
    onSubmit: (String, String?, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var education by remember { mutableStateOf("") }
    var employment by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pdsCard by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var pdsCardError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "1. PRIMARY BENEFICIARY",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = buttonColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        RegistrationTextField(
            label = "Full Name",
            value = name,
            onValueChange = { name = it; nameError = null },
            placeholder = "Enter full name",
            error = nameError
        )

        RegistrationTextField(
            label = "Phone Number",
            value = phone,
            onValueChange = { phone = it; phoneError = null },
            placeholder = "10-digit mobile number",
            keyboardType = KeyboardType.Phone,
            error = phoneError
        )

        RegistrationTextField(
            label = "Email Address (Optional)",
            value = email,
            onValueChange = { email = it },
            placeholder = "name@example.com",
            keyboardType = KeyboardType.Email
        )

        RegistrationTextField(
            label = "Age",
            value = age,
            onValueChange = { age = it; ageError = null },
            placeholder = "Years",
            keyboardType = KeyboardType.Number,
            error = ageError
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Gender", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        GenderSelection(selected = gender, onSelected = { gender = it })

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
            label = "Ration Card / PDS ID No.",
            value = pdsCard,
            onValueChange = { pdsCard = it; pdsCardError = null },
            placeholder = "Unique ID number",
            error = pdsCardError
        )

        RegistrationTextField(
            label = "Street Address",
            value = address,
            onValueChange = { address = it; addressError = null },
            placeholder = "House no., street, area",
            error = addressError
        )

        // Inject content from parent (e.g., family members or ID card pickers)
        contentBeforeSubmit()

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                var hasError = false
                if (name.isBlank()) { nameError = "Name is required"; hasError = true }
                if (phone.length < 10) { phoneError = "Valid phone is required"; hasError = true }
                if (age.isBlank()) { ageError = "Age is required"; hasError = true }
                if (pdsCard.isBlank()) { pdsCardError = "Ration card no. is required"; hasError = true }
                if (address.isBlank()) { addressError = "Address is required"; hasError = true }

                if (!hasError) {
                    onSubmit(name, email.ifBlank { null }, phone, age, gender, education, employment, address, pdsCard)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(submitButtonText, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
