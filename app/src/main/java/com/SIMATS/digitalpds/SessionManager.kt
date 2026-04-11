package com.SIMATS.digitalpds

import android.content.Context
import android.content.SharedPreferences
import com.SIMATS.digitalpds.network.MonthlyProgressResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MonthlyBrushRecord(
    val date: String,
    val morning: Boolean,
    val evening: Boolean
)

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_PDS_VERIFIED = "pds_verified"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_PROFILE_IMAGE = "profile_image"
        private const val KEY_DEALER_QR_VALUE = "dealer_qr_value"
        private const val KEY_ASSIGNED_DEALER_ID = "assigned_dealer_id"
        private const val KEY_ASSIGNED_DEALER_NAME = "assigned_dealer_name"
        private const val KEY_ASSIGNED_DEALER_LOCATION = "assigned_dealer_location"

        private const val KEY_WEEKLY_SESSIONS_PREFIX = "weekly_sessions"
        private const val KEY_WEEKLY_COUNT_PREFIX = "weekly_count"
        private const val KEY_WEEK_STAMP_PREFIX = "weekly_stamp"

        private const val KEY_MONTHLY_LOG_PREFIX = "monthly_log"
        private const val KEY_MONTHLY_TOTAL_PREFIX = "monthly_total"
    }

    fun saveSession(
        userId: Int,
        name: String,
        email: String,
        phone: String,
        role: String,
        pdsVerified: Boolean = false,
        token: String? = null,
        profileImage: String? = null,
        dealerQrValue: String? = null
    ) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_PDS_VERIFIED, pdsVerified)
            putString(KEY_ACCESS_TOKEN, token)
            putString(KEY_PROFILE_IMAGE, profileImage)
            putString(KEY_DEALER_QR_VALUE, dealerQrValue)
            apply()
        }
    }

    fun setPdsVerified(verified: Boolean) {
        prefs.edit().putBoolean(KEY_PDS_VERIFIED, verified).apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "User")
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, "")
    fun getUserPhone(): String? = prefs.getString(KEY_USER_PHONE, "")
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, "user")
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isPdsVerified(): Boolean = prefs.getBoolean(KEY_PDS_VERIFIED, false)
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getProfileImage(): String? = prefs.getString(KEY_PROFILE_IMAGE, null)
    fun getDealerQrValue(): String? = prefs.getString(KEY_DEALER_QR_VALUE, null)

    fun setAssignedDealerId(id: Int) {
        prefs.edit().putInt(KEY_ASSIGNED_DEALER_ID, id).apply()
    }

    fun getAssignedDealerId(): Int = prefs.getInt(KEY_ASSIGNED_DEALER_ID, -1)

    fun setAssignedDealerName(name: String) {
        prefs.edit().putString(KEY_ASSIGNED_DEALER_NAME, name).apply()
    }

    fun getAssignedDealerName(): String? = prefs.getString(KEY_ASSIGNED_DEALER_NAME, null)

    fun setAssignedDealerLocation(location: String) {
        prefs.edit().putString(KEY_ASSIGNED_DEALER_LOCATION, location).apply()
    }

    fun getAssignedDealerLocation(): String? = prefs.getString(KEY_ASSIGNED_DEALER_LOCATION, null)

    private fun currentWeekStamp(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        return "$year-$week"
    }

    private fun currentMonthStamp(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return "$year-${month.toString().padStart(2, '0')}"
    }

    private fun todayStamp(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun defaultWeeklySessions(): List<Pair<Boolean, Boolean>> {
        return List(7) { false to false }
    }

    fun saveWeeklyCheckinStatus(
        userId: Int,
        completedSessions: List<Pair<Boolean, Boolean>>
    ) {
        if (userId <= 0) return

        val safeSessions = List(7) { index ->
            completedSessions.getOrElse(index) { false to false }
        }

        val serialized = safeSessions.joinToString("|") { pair ->
            val morning = if (pair.first) 1 else 0
            val evening = if (pair.second) 1 else 0
            "$morning,$evening"
        }

        val count = safeSessions.sumOf { (morning, evening) ->
            (if (morning) 1 else 0) + (if (evening) 1 else 0)
        }

        prefs.edit().apply {
            putString("${KEY_WEEKLY_SESSIONS_PREFIX}_$userId", serialized)
            putInt("${KEY_WEEKLY_COUNT_PREFIX}_$userId", count)
            putString("${KEY_WEEK_STAMP_PREFIX}_$userId", currentWeekStamp())
            apply()
        }
    }

    fun getWeeklyCheckinStatus(userId: Int): List<Pair<Boolean, Boolean>> {
        if (userId <= 0) return defaultWeeklySessions()

        val savedWeekStamp = prefs.getString("${KEY_WEEK_STAMP_PREFIX}_$userId", null)
        if (savedWeekStamp != currentWeekStamp()) {
            return defaultWeeklySessions()
        }

        val raw = prefs.getString("${KEY_WEEKLY_SESSIONS_PREFIX}_$userId", null)
            ?: return defaultWeeklySessions()

        val parsed = raw.split("|").mapNotNull { entry ->
            val parts = entry.split(",")
            if (parts.size != 2) return@mapNotNull null
            val morning = parts[0] == "1"
            val evening = parts[1] == "1"
            morning to evening
        }

        return if (parsed.size == 7) parsed else defaultWeeklySessions()
    }

    fun getWeeklyCompletedCount(userId: Int): Int {
        if (userId <= 0) return 0

        val savedWeekStamp = prefs.getString("${KEY_WEEK_STAMP_PREFIX}_$userId", null)
        if (savedWeekStamp != currentWeekStamp()) {
            return 0
        }

        return prefs.getInt("${KEY_WEEKLY_COUNT_PREFIX}_$userId", 0)
    }

    fun saveMonthlyUsage(userId: Int, data: MonthlyProgressResponse) {
        if (userId <= 0) return

        val monthKey = "${KEY_MONTHLY_LOG_PREFIX}_${userId}_${currentMonthStamp()}"
        val serialized = data.sessions.joinToString("|") { session ->
            val m = if (session.morning) 1 else 0
            val e = if (session.evening) 1 else 0
            "${session.date}:$m,$e"
        }

        prefs.edit().apply {
            putString(monthKey, serialized)
            putInt("${KEY_MONTHLY_TOTAL_PREFIX}_${userId}_${currentMonthStamp()}", data.totalCompleted)
            apply()
        }
    }

    fun saveMonthlyCheckin(userId: Int, session: String) {
        if (userId <= 0) return

        val monthKey = "${KEY_MONTHLY_LOG_PREFIX}_${userId}_${currentMonthStamp()}"
        val currentData = prefs.getString(monthKey, "").orEmpty()

        val recordMap = mutableMapOf<String, Pair<Boolean, Boolean>>()

        if (currentData.isNotBlank()) {
            currentData.split("|").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val date = parts[0]
                    val flags = parts[1].split(",")
                    if (flags.size == 2) {
                        recordMap[date] = (flags[0] == "1") to (flags[1] == "1")
                    }
                }
            }
        }

        val today = todayStamp()
        val existing = recordMap[today] ?: (false to false)

        val updated = if (session.uppercase() == "MORNING") {
            true to existing.second
        } else {
            existing.first to true
        }

        recordMap[today] = updated

        val serialized = recordMap
            .toSortedMap()
            .entries
            .joinToString("|") { (date, pair) ->
                val m = if (pair.first) 1 else 0
                val e = if (pair.second) 1 else 0
                "$date:$m,$e"
            }

        prefs.edit().putString(monthKey, serialized).apply()
    }

    fun getCurrentMonthLogs(userId: Int): List<MonthlyBrushRecord> {
        if (userId <= 0) return emptyList()

        val monthKey = "${KEY_MONTHLY_LOG_PREFIX}_${userId}_${currentMonthStamp()}"
        val raw = prefs.getString(monthKey, "").orEmpty()
        if (raw.isBlank()) return emptyList()

        return raw.split("|").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size != 2) return@mapNotNull null

            val date = parts[0]
            val flags = parts[1].split(",")
            if (flags.size != 2) return@mapNotNull null

            MonthlyBrushRecord(
                date = date,
                morning = flags[0] == "1",
                evening = flags[1] == "1"
            )
        }.sortedByDescending { it.date }
    }

    fun getCurrentMonthCompletedCount(userId: Int): Int {
        val count = prefs.getInt("${KEY_MONTHLY_TOTAL_PREFIX}_${userId}_${currentMonthStamp()}", -1)
        if (count != -1) return count

        return getCurrentMonthLogs(userId).sumOf {
            (if (it.morning) 1 else 0) + (if (it.evening) 1 else 0)
        }
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_PHONE)
            remove(KEY_USER_ROLE)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_PDS_VERIFIED)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_PROFILE_IMAGE)
            remove(KEY_DEALER_QR_VALUE)
            remove(KEY_ASSIGNED_DEALER_ID)
            remove(KEY_ASSIGNED_DEALER_NAME)
            remove(KEY_ASSIGNED_DEALER_LOCATION)
            apply()
        }
    }
}
