package com.omsworld.familycare.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Replaces DateTime_c.java.
 */
object DateTimeUtil {

    private const val SERVER_FMT = "yyyy-MM-dd HH:mm:ss"
    private const val ISO_FMT = "yyyy-MM-dd'T'HH:mm:ss"

    fun formatDateTime(input: String): String {
        return try {
            val cal = Calendar.getInstance(TimeZone.getDefault())
            val cYear = cal.get(Calendar.YEAR).toString()
            val cDay = cal.get(Calendar.DAY_OF_MONTH).toString()
            val cMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)

            when (input) {
                "currentTime" ->
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                "currentDateTime" ->
                    SimpleDateFormat(SERVER_FMT, Locale.getDefault()).format(cal.time)
                else -> {
                    val sdf = SimpleDateFormat(SERVER_FMT, Locale.getDefault())
                    val date = sdf.parse(input) ?: return input
                    cal.time = date
                    val year = cal.get(Calendar.YEAR).toString()
                    val day = cal.get(Calendar.DAY_OF_MONTH).toString()
                    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
                    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                    if (cYear == year) {
                        if (cMonth == month && cDay == day) time else "$day $month"
                    } else input
                }
            }
        } catch (e: Exception) {
            input
        }
    }

    /**
     * Chat-style timestamp (WhatsApp-style).
     *  - Same day     → "10:30 AM"
     *  - Yesterday    → "Yesterday 10:30 AM"
     *  - This week    → "Tue 10:30 AM"
     *  - This year    → "7 Oct, 10:30 AM"
     *  - Older        → "7 Oct 2025, 10:30 AM"
     */
    fun changeFormat(input: String): String = try {
        // Try several server formats
        val date: Date? = try {
            SimpleDateFormat(SERVER_FMT, Locale.getDefault()).parse(input)
        } catch (_: Exception) {
            try {
                SimpleDateFormat(ISO_FMT, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(input)
            } catch (_: Exception) {
                null
            }
        }

        if (date == null) {
            input
        } else {
            val now = Calendar.getInstance()
            val msgCal = Calendar.getInstance().apply { time = date }

            val sameYear = now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR)
            val sameDay = sameYear &&
                    now.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)

            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }
            val isYesterday = sameYear &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)

            val diffMillis = now.timeInMillis - date.time
            val diffDays = diffMillis / (24L * 60 * 60 * 1000)

            val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)

            when {
                sameDay -> time
                isYesterday -> "Yesterday $time"
                diffDays < 7 -> SimpleDateFormat("EEE", Locale.getDefault()).format(date) + " $time"
                sameYear -> SimpleDateFormat("d MMM, ", Locale.getDefault()).format(date) + time
                else -> SimpleDateFormat("d MMM yyyy, ", Locale.getDefault()).format(date) + time
            }
        }
    } catch (e: Exception) {
        input
    }

    fun dateExistsInRange(from: String, to: String, compare: String): Boolean = try {
        val sdf = SimpleDateFormat(SERVER_FMT, Locale.getDefault())
        val fromD = sdf.parse(from)
        val toD = sdf.parse(to)
        val comp = sdf.parse(compare)
        comp != null && fromD != null && toD != null &&
                comp.after(fromD) && comp.before(toD)
    } catch (e: Exception) {
        false
    }

    fun now(): String = SimpleDateFormat(SERVER_FMT, Locale.getDefault()).format(Date())

    fun today(): String =
        SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(Date())
}