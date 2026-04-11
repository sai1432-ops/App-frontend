package com.SIMATS.digitalpds

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class DealerInfo(
    val id: Int = 0,
    val name: String? = "",
    @SerializedName("company_name") val companyName: String? = "",
    val phone: String? = "",
    val email: String? = "",
    val handle: String? = "",
    val location: String? = "",
    val address: String? = "",
    val city: String? = "",
    val state: String? = "",
    val username: String? = "",
    val activeStatus: String? = "Active",
    val isOnline: Boolean = true,
    val imageRes: Int = R.drawable.user,
    val isEnabled: Boolean = true,
    val emailVerified: Boolean = false
)

data class Deal(
    val title: String,
    val product: String,
    val quantity: String,
    val price: String,
    val status: String,
    val statusColor: Color,
    val date: String
)
