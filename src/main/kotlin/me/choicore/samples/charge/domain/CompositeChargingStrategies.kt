package me.choicore.samples.charge.domain

import java.time.LocalDate
import java.time.temporal.TemporalAdjuster

class CompositeChargingStrategies private constructor(
    private val registries: Map<ChargingStrategy.Cycle, AbstractChargingStrategyRegistry<out TemporalAdjuster>>,
) : ChargingStrategyRegistry {
    constructor() : this(
        mapOf(
            ONCE_STRATEGY to SpecifiedDateChargingStrategies(),
            REPEATABLE_STRATEGY to DayOfWeekChargingStrategies(),
        ),
    )

    class Builder {
        private val specifiedDateChargingStrategies: SpecifiedDateChargingStrategies = SpecifiedDateChargingStrategies()
        private val dayOfWeekChargingStrategies: DayOfWeekChargingStrategies = DayOfWeekChargingStrategies()

        fun once(chargingStrategy: ChargingStrategy): Builder {
            this.specifiedDateChargingStrategies.register(strategy = chargingStrategy)
            return this
        }

        fun repeatable(chargingStrategy: ChargingStrategy): Builder {
            this.dayOfWeekChargingStrategies.register(strategy = chargingStrategy)
            return this
        }

        fun build(): CompositeChargingStrategies =
            CompositeChargingStrategies(
                mapOf(
                    ONCE_STRATEGY to this.specifiedDateChargingStrategies,
                    REPEATABLE_STRATEGY to this.dayOfWeekChargingStrategies,
                ),
            )
    }

    override fun register(strategy: ChargingStrategy) {
        registries[strategy.cycle]?.register(strategy)
            ?: throw IllegalArgumentException(
                "Unsupported strategy type: ${strategy.cycle}. Supported types: $ONCE_STRATEGY, $REPEATABLE_STRATEGY",
            )
    }

    override fun getChargingStrategies(date: LocalDate): List<ChargingStrategy> =
        this.getOnceStrategies(date).ifEmpty { getRepeatableStrategies(date) }

    private fun getOnceStrategies(date: LocalDate): List<ChargingStrategy> = this.registries[ONCE_STRATEGY]!!.getChargingStrategies(date)

    private fun getRepeatableStrategies(date: LocalDate): List<ChargingStrategy> =
        this.registries[REPEATABLE_STRATEGY]!!.getChargingStrategies(date)

    companion object {
        private val ONCE_STRATEGY = ChargingStrategy.Cycle.ONCE
        private val REPEATABLE_STRATEGY = ChargingStrategy.Cycle.REPEATABLE

        fun builder(): Builder = Builder()
    }
}
