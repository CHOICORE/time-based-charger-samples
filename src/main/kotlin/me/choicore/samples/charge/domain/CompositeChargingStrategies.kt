package me.choicore.samples.charge.domain

import java.time.LocalDate
import java.time.temporal.TemporalAdjuster

class CompositeChargingStrategies private constructor(
    private val registries: Map<ChargingStrategy.Cycle, AbstractChargingStrategies<out TemporalAdjuster>>,
) : ChargingStrategies {
    constructor(station: ChargingStation) : this(
        mapOf(
            ONCE_STRATEGY to SpecifiedDateChargingStrategies(station = station),
            REPEATABLE_STRATEGY to DayOfWeekChargingStrategies(station = station),
        ),
    )

    class CompositeChargingStrategiesBuilder(
        station: ChargingStation,
    ) {
        private val specifiedDateChargingStrategies: SpecifiedDateChargingStrategies =
            SpecifiedDateChargingStrategies(station = station)
        private val dayOfWeekChargingStrategies: DayOfWeekChargingStrategies =
            DayOfWeekChargingStrategies(station = station)

        fun once(chargingStrategy: ChargingStrategy): CompositeChargingStrategiesBuilder {
            this.specifiedDateChargingStrategies.register(strategy = chargingStrategy)
            return this
        }

        fun repeatable(chargingStrategy: ChargingStrategy): CompositeChargingStrategiesBuilder {
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
        this.getOnceStrategies(date).ifEmpty { getRepeatableStrategies(date = date) }

    private fun getOnceStrategies(date: LocalDate): List<ChargingStrategy> =
        this.registries[ONCE_STRATEGY]!!.getChargingStrategies(
            date = date,
        )

    private fun getRepeatableStrategies(date: LocalDate): List<ChargingStrategy> =
        this.registries[REPEATABLE_STRATEGY]!!.getChargingStrategies(date = date)

    companion object {
        private val ONCE_STRATEGY = ChargingStrategy.Cycle.ONCE
        private val REPEATABLE_STRATEGY = ChargingStrategy.Cycle.REPEATABLE

        fun builder(station: ChargingStation): CompositeChargingStrategiesBuilder = CompositeChargingStrategiesBuilder(station = station)
    }
}
