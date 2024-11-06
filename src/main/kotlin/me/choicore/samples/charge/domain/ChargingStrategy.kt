package me.choicore.samples.charge.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class ChargingStrategy(
    val id: Long? = null,
    val stationId: Long,
    val mode: ChargingMode,
    val rate: Int,
    val timeline: Timeline,
    var deleted: Boolean = false,
) {
    val dayOfWeek: DayOfWeek = this.timeline.dayOfWeek
    val specifiedDate: LocalDate? = this.timeline.specifiedDate
    val cycle: Cycle = if (this.specifiedDate != null) Cycle.ONCE else Cycle.REPEATABLE

    fun supports(selectedDate: LocalDate): Boolean = this.timeline.satisfiedBy(selectedDate = selectedDate)

    fun attempt(chargingUnit: ChargingUnit) {
        require(value = this.supports(selectedDate = chargingUnit.chargedOn)) { "The specified date does not satisfy the timeline." }
        chargingUnit.adjust(strategy = this)
    }

    enum class Cycle {
        REPEATABLE,
        ONCE,
    }

    companion object {
        fun standard(
            stationId: Long,
            dayOfWeek: DayOfWeek,
        ): ChargingStrategy =
            ChargingStrategy(
                stationId = stationId,
                mode = ChargingMode.NONE,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )

        fun standard(
            stationId: Long,
            timeline: Timeline,
        ): ChargingStrategy =
            ChargingStrategy(
                stationId = stationId,
                mode = ChargingMode.NONE,
                rate = 100,
                timeline = timeline,
            )

        fun exempt(
            stationId: Long,
            dayOfWeek: DayOfWeek,
        ): ChargingStrategy =
            ChargingStrategy(
                stationId = stationId,
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = Timeline.fullTime(dayOfWeek = dayOfWeek),
            )

        fun exempt(
            stationId: Long,
            chargedOn: LocalDate,
        ): ChargingStrategy =
            ChargingStrategy(
                stationId = stationId,
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = Timeline.fullTime(specifyDate = chargedOn),
            )

        fun exempt(
            stationId: Long,
            timeline: Timeline,
        ) {
            ChargingStrategy(
                stationId = stationId,
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline = timeline,
            )
        }
    }
}
