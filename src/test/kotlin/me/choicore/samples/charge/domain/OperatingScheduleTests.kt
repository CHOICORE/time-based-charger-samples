package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.OperatingSchedule.DayOfWeekSchedule
import me.choicore.samples.charge.domain.OperatingSchedule.SpecifiedDateSchedule
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate

class OperatingScheduleTests {
    @Test
    fun t1() {
        val operatingSchedule =
            OperatingSchedule
                .builder()
                .register(DayOfWeekSchedule(MONDAY))
                .register(DayOfWeekSchedule(TUESDAY))
                .register(DayOfWeekSchedule(WEDNESDAY))
                .register(DayOfWeekSchedule(THURSDAY))
                .register(DayOfWeekSchedule(FRIDAY))
                .register(DayOfWeekSchedule(SATURDAY))
                .register(DayOfWeekSchedule(SUNDAY))
                .register(SpecifiedDateSchedule(LocalDate.now().minusDays(2)))
                .register(SpecifiedDateSchedule(LocalDate.now().minusDays(1)))
                .register(SpecifiedDateSchedule(LocalDate.now()))
                .build()

        println(operatingSchedule)
    }
}
