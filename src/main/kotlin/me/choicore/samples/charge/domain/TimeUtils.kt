package me.choicore.samples.charge.domain

import java.time.LocalTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun duration(
        start: LocalTime,
        end: LocalTime,
        unit: ChronoUnit,
    ): Long = getElapsedTime(start = start, end = end, unit = unit)

    private fun getElapsedTime(
        start: LocalTime,
        end: LocalTime,
        unit: ChronoUnit,
    ): Long =
        when {
            isFullTime(start, end) -> getFullDayDuration(unit)
            else -> {
                val duration: Long = unit.between(start, end)
                if (end == LocalTime.MAX) duration.plus(1) else duration
            }
        }

    private fun getFullDayDuration(unit: ChronoUnit): Long =
        when (unit) {
            ChronoUnit.DAYS -> 1
            ChronoUnit.HOURS -> 24
            ChronoUnit.MINUTES -> 1440
            ChronoUnit.SECONDS -> 86400
            else -> throw IllegalArgumentException("Unsupported unit for full day calculation")
        }

    private fun isFullTime(
        start: LocalTime,
        end: LocalTime,
    ): Boolean = start == LocalTime.MIN && end == LocalTime.MAX
}
