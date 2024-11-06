package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStrategy.Cycle
import java.time.DayOfWeek
import java.time.LocalDate

class DayOfWeekChargingStrategies(
    station: ChargingStation,
) : AbstractChargingStrategies<DayOfWeek>(station = station) {
    override fun getKey(strategy: ChargingStrategy): DayOfWeek {
        check(value = Cycle.REPEATABLE == strategy.cycle) { "The strategy has a specified date." }
        return strategy.dayOfWeek
    }

    override fun getKeyForDate(date: LocalDate): DayOfWeek = date.dayOfWeek

    override fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy> =
        listOf(ChargingStrategy.standard(stationId = super.station.id!!, dayOfWeek = date.dayOfWeek))
}
