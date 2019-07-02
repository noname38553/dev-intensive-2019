package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val time = Math.round(((this.time - Date().time) / 100).toDouble() / 10)
    val time3: String

    if (time < 0) {
        when (time) {
            in -1..0 -> time3 = "только что"
            in -45..-1 -> time3 = "несколько секунд назад"
            in -75..-45 -> time3 = "минуту назад"
            in (-60 * 45)..-75 -> time3 = "${Utils.declination(-time / 60, "MINUTE")} назад"
            in (-60 * 75)..(-60 * 45) -> time3 = "час назад"
            in (-60 * 60 * 22)..(-60 * 75) -> time3 = "${Utils.declination(-time / 3600, "HOUR")} назад"
            in (-60 * 60 * 26)..(-60 * 60 * 22) -> time3 = "день назад"
            in (-60 * 60 * 24 * 360)..(-60 * 60 * 26) -> time3 = "${Utils.declination(-time / 86400, "DAY")} назад"
            else -> time3 = "более года назад"
        }
    } else {
        when (time) {
            in 0..1 -> time3 = "сейчас"
            in 1..45 -> time3 = "через несколько секунд"
            in 45..75 -> time3 = "через минуту "
            in 75..(60 * 45) -> time3 = "через ${Utils.declination(time / 60, "MINUTE")}"
            in (60 * 45)..(60 * 75) -> time3 = "через час"
            in (60 * 75)..(60 * 60 * 22) -> time3 = "через ${Utils.declination(time / 3600, "HOUR")}"
            in (60 * 60 * 22)..(60 * 60 * 26) -> time3 = "через день"
            in (60 * 60 * 26)..(60 * 60 * 24 * 360) -> time3 = "через ${Utils.declination(time / 86400, "DAY")}"
            else -> time3 = "более чем через год"
        }
    }

    return time3

}


enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}