package me.choicore.samples.charge.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class OperatingSchedule(
    val monday: DayOfWeekSchedule,
    val tuesday: DayOfWeekSchedule,
    val wednesday: DayOfWeekSchedule,
    val thursday: DayOfWeekSchedule,
    val friday: DayOfWeekSchedule,
    val saturday: DayOfWeekSchedule,
    val sunday: DayOfWeekSchedule,
    val specifies: List<SpecifiedDateSchedule>,
) {
    interface Schedule {
        fun scheduledFor(date: LocalDate): Boolean
    }

    data class DayOfWeekSchedule(
//        val id: Long? = null,
//        val stationId: Long,
        val dayOfWeek: DayOfWeek,
//        val mode: ChargingMode,
//        val rate: Int,
//        val timeline: Timeline,
    ) : Schedule {
        override fun scheduledFor(date: LocalDate): Boolean = this.dayOfWeek == date.dayOfWeek
    }

    data class SpecifiedDateSchedule(
//        val id: Long? = null,
//        val stationId: Long,
        val specifiedDate: LocalDate,
//        val mode: ChargingMode,
//        val rate: Int,
//        val timeline: Timeline,
    ) : Schedule {
        override fun scheduledFor(date: LocalDate): Boolean = this.specifiedDate == date
    }

    class OperatingScheduleBuilder {
        private var monday: DayOfWeekSchedule? = null
        private var tuesday: DayOfWeekSchedule? = null
        private var wednesday: DayOfWeekSchedule? = null
        private var thursday: DayOfWeekSchedule? = null
        private var friday: DayOfWeekSchedule? = null
        private var saturday: DayOfWeekSchedule? = null
        private var sunday: DayOfWeekSchedule? = null
        private var specifies: MutableMap<LocalDate, SpecifiedDateSchedule> = mutableMapOf()

        fun register(schedule: Schedule): OperatingScheduleBuilder {
            when (schedule) {
                is DayOfWeekSchedule -> {
                    when (schedule.dayOfWeek) {
                        DayOfWeek.MONDAY -> monday = schedule
                        DayOfWeek.TUESDAY -> tuesday = schedule
                        DayOfWeek.WEDNESDAY -> wednesday = schedule
                        DayOfWeek.THURSDAY -> thursday = schedule
                        DayOfWeek.FRIDAY -> friday = schedule
                        DayOfWeek.SATURDAY -> saturday = schedule
                        DayOfWeek.SUNDAY -> sunday = schedule
                    }
                }

                is SpecifiedDateSchedule -> {
                    specifies[schedule.specifiedDate] = schedule
                }
            }
            return this
        }

        fun build(): OperatingSchedule =
            OperatingSchedule(
                monday = monday!!,
                tuesday = tuesday!!,
                wednesday = wednesday!!,
                thursday = thursday!!,
                friday = friday!!,
                saturday = saturday!!,
                sunday = sunday!!,
                specifies = specifies.values.toList(),
            )
    }

    companion object {
        fun builder(): OperatingScheduleBuilder = OperatingScheduleBuilder()
    }
}
