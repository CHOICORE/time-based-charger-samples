package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStrategy.Type
import java.time.LocalDate

class SpecifiedDateChargingStrategyRegistry : AbstractChargingStrategyRegistry<LocalDate>() {
    override fun getKey(strategy: ChargingStrategy): LocalDate {
        check(value = Type.ONCE == strategy.type) { "The strategy does not have a specified date." }
        return strategy.specifiedDate!!
    }

    override fun getKeyForDate(date: LocalDate): LocalDate = date

    override fun getDefaultStrategies(date: LocalDate): List<ChargingStrategy> = emptyList()
}
