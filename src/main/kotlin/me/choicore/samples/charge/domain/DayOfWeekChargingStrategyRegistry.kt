package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStrategy.Type
import java.time.DayOfWeek
import java.time.LocalDate

class DayOfWeekChargingStrategyRegistry : AbstractChargingStrategyRegistry<DayOfWeek>() {
    override fun getKey(strategy: ChargingStrategy): DayOfWeek {
        check(value = Type.REPEATABLE == strategy.type) { "The strategy has a specified date." }
        return strategy.dayOfWeek
    }

    override fun getKeyForDate(date: LocalDate): DayOfWeek = date.dayOfWeek

    override fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy> = listOf(ChargingStrategy.noop(dayOfWeek = date.dayOfWeek))
}
