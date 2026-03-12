package com.SIMATS.digitalpds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmKitReceiptScreen(
    onBackClick: () -> Unit,
    onFinalizeClick: (
        oldKitReturned: Boolean,
        brushChecked: Boolean,
        pasteChecked: Boolean,
        iecMaterialsChecked: Boolean
    ) -> Unit = { _, _, _, _ -> },
    onHomeClick: () -> Unit = {},
    onKitsClick: () -> Unit = {},
    onLearnClick: () -> Unit = {},
    onConsultClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var brushChecked by remember { mutableStateOf(false) }
    var pasteChecked by remember { mutableStateOf(false) }
    var iecMaterialsChecked by remember { mutableStateOf(false) }
    var oldKitReturned by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Confirm Kit Receipt",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black) },
                    label = { Text("Home", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onKitsClick,
                    icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = "Kits", tint = Color.Black) },
                    label = { Text("Kits", color = Color.Black) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLearnClick,
                    icon = { Icon(Icons.Filled.School, contentDescription = "Learn", tint = Color.Black) },
                    label = { Text("Learn", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onConsultClick,
                    icon = { Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Consult", tint = Color.Black) },
                    label = { Text("Consult", color = Color.Black) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black) },
                    label = { Text("Profile", color = Color.Black) }
                )
            }
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.brush),
                contentDescription = "Kit",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            KitCheckboxItem(
                label = "Brush",
                checked = brushChecked,
                onCheckedChange = { brushChecked = it }
            )
            KitCheckboxItem(
                label = "Paste",
                checked = pasteChecked,
                onCheckedChange = { pasteChecked = it }
            )
            KitCheckboxItem(
                label = "IEC Materials",
                checked = iecMaterialsChecked,
                onCheckedChange = { iecMaterialsChecked = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Did you return the old kit?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = oldKitReturned == true,
                    onClick = { oldKitReturned = true }
                )
                Text("Yes", modifier = Modifier.padding(start = 8.dp))

                Spacer(modifier = Modifier.width(32.dp))

                RadioButton(
                    selected = oldKitReturned == false,
                    onClick = { oldKitReturned = false }
                )
                Text("No", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onFinalizeClick(
                        oldKitReturned ?: false,
                        brushChecked,
                        pasteChecked,
                        iecMaterialsChecked
                    )
                },
                enabled = brushChecked && pasteChecked && iecMaterialsChecked && oldKitReturned != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    "Scan QR to Finalize",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun KitCheckboxItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp),
            color = TextBlack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmKitReceiptScreenPreview() {
    DigitalpdsTheme {
        ConfirmKitReceiptScreen(onBackClick = {})
    }
}