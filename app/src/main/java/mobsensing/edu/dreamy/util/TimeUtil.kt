package mobsensing.edu.dreamy.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

fun epochMilliToDayMonthHourMinute(time: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")
    val instant = Instant.ofEpochMilli(time)
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return formatter.format(date)
}

fun epochMilliToDayMonth(time: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    val instant = Instant.ofEpochMilli(time)
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return formatter.format(date)
}

fun epochMilliToHourMinute(time: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    val instant = Instant.ofEpochMilli(time)
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return if (date.minute >= 60) {
        "${date.hour}hr, ${date.minute}min"
    } else {
        "${date.minute}min"
    }
}

// ? Not used
fun milliToHourMinute(millis: Long): String {
    val inMinute = TimeUnit.MICROSECONDS.toMinutes(millis)

    return if (inMinute >= 60) {
        val inHour = TimeUnit.MICROSECONDS.toHours(millis)
        val subMinute = inMinute - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
        "${inHour}hr, ${subMinute}min"
    } else {
        "${inMinute}min"
    }
}
fun durationConverter(start: Long, end: Long): String {
    val duration =
        Instant.ofEpochMilli(start).until(Instant.ofEpochMilli(end), ChronoUnit.MINUTES)

    return LocalTime.MIN.plus(
        Duration.ofMinutes(duration)
    ).toString()
}
