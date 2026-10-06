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

    fun changeFormat(input: String): String = try {
        val sdf = SimpleDateFormat(SERVER_FMT, Locale.getDefault())
        val date = sdf.parse(input)
        date?.let {
            SimpleDateFormat("d MMM yyyy  h:mm a", Locale.getDefault()).format(it)
        } ?: input
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