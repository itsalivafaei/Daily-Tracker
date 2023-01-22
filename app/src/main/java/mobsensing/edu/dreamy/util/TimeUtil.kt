package mobsensing.edu.dreamy.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

fun durationConverter(start: Long, end: Long): String {
    val duration =
        Instant.ofEpochMilli(start).until(Instant.ofEpochMilli(end), ChronoUnit.MINUTES)

    return LocalTime.MIN.plus(
        Duration.ofMinutes(duration)
    ).toString()
}
