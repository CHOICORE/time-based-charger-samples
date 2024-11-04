package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStrategy.Cycle
import java.time.LocalDate

class SpecifiedDateChargingStrategies : AbstractChargingStrategyRegistry<LocalDate>() {
    override fun getKey(strategy: ChargingStrategy): LocalDate {
        check(value = Cycle.ONCE == strategy.cycle) { "The strategy does not have a specified date." }
        return strategy.specifiedDate!!
    }

    override fun getKeyForDate(date: LocalDate): LocalDate = date

    override fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy> = emptyList()
}
