package fr.epita.mymeteo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTo24HourFormat(time12Hour: String): String {
    val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = inputFormat.parse(time12Hour)
    return outputFormat.format(date)
}

private fun parseTime(time: String, format: String): Date {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(time)
}

fun isWithinTwoHours(time1: String, time2: String): Boolean {
    val time24HourFormat = "HH:mm"

    // Convert 12-hour time to 24-hour time and parse both times
    val date24Hour = parseTime(time1, time24HourFormat)
    val date12Hour = parseTime(time2, time24HourFormat)

    // Calculate the difference in milliseconds
    val differenceInMillis = date24Hour.time - date12Hour.time
    val oneHourInMillis = 60 * 60 * 1000

    // Check if the difference is within -1 hour to +1 hour range
    return differenceInMillis in -oneHourInMillis..oneHourInMillis
}