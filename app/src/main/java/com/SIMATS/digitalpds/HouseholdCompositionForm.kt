package com.SIMATS.digitalpds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.digitalpds.ui.theme.*
import com.SIMATS.digitalpds.network.FamilyMemberInput

@Composable
fun HouseholdCompositionForm(
    addedMembers: SnapshotStateList<FamilyMemberInput>
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "2. HOUSEHOLD MEMBERS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DealerGreen
            )
            
            TextButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD MEMBER", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (addedMembers.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Text(
                    "No family members added yet.",
                    modifier = Modifier.padding(24.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            addedMembers.forEachIndexed { index, member ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = DealerGreen.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DealerGreen)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${member.relation} • ${member.age} years", fontSize = 12.sp, color = Color.Gray)
                        }

                        IconButton(onClick = { addedMembers.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var newMemberName by remember { mutableStateOf("") }
        var newMemberAge by remember { mutableStateOf("") }
        var newMemberRelation by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newMemberName.isNotBlank() && newMemberAge.isNotBlank()) {
                            addedMembers.add(
                                FamilyMemberInput(
                                    name = newMemberName,
                                    age = newMemberAge,
                                    relation = newMemberRelation
                                )
                            )
                            showDialog = false
                        }
                    }
                ) {
                    Text("ADD", color = DealerGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            },
            title = { Text("Add Family Member", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { newMemberName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberAge,
                        onValueChange = { newMemberAge = it },
                        label = { Text("Age") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberRelation,
                        onValueChange = { newMemberRelation = it },
                        label = { Text("Relation") },
                        placeholder = { Text("e.g. Spouse, Son, Mother") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
