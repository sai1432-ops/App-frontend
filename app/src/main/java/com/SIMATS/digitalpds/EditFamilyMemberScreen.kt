package com.SIMATS.digitalpds

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.network.FamilyMemberRequest
import com.SIMATS.digitalpds.network.FamilyMemberResponse
import com.SIMATS.digitalpds.network.RetrofitClient
import com.SIMATS.digitalpds.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFamilyMemberScreen(
    userId: Int,
    member: FamilyMemberResponse,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    onRemoveSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(member.memberName) }
    var age by remember { mutableStateOf(member.age.toString()) }
    var relation by remember { mutableStateOf(member.relation) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Edit Family Member",
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
                actions = {
                    IconButton(onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.deleteFamilyMember(member.id, userId)
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Member removed", Toast.LENGTH_SHORT).show()
                                    onRemoveSuccess()
                                } else {
                                    Toast.makeText(context, "Failed to remove member", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color.Red
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            CustomInputField(
                label = "Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "Enter full name"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Age Field
            CustomInputField(
                label = "Age",
                value = age,
                onValueChange = { age = it },
                placeholder = "Enter age"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Relation Field
            CustomInputField(
                label = "Relation",
                value = relation,
                onValueChange = { relation = it },
                placeholder = "e.g. Spouse, Child, Parent"
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isBlank() || age.isBlank() || relation.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val request = FamilyMemberRequest(
                                userId = userId,
                                memberName = name,
                                age = age.toIntOrNull() ?: 0,
                                relation = relation
                            )
                            val response = RetrofitClient.apiService.updateFamilyMember(member.id, request)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Member updated successfully", Toast.LENGTH_SHORT).show()
                                onSaveSuccess()
                            } else {
                                Toast.makeText(context, "Failed to update member", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditFamilyMemberScreenPreview() {
    DigitalpdsTheme {
        EditFamilyMemberScreen(
            userId = 1,
            member = FamilyMemberResponse(
                id = 1,
                userId = 1,
                memberName = "John Doe",
                age = 30,
                relation = "Brother",
                brushingTarget = 14,
                weeklyBrushCount = 10
            ),
            onBackClick = {},
            onSaveSuccess = {},
            onRemoveSuccess = {}
        )
    }
}
