package me.choicore.samples.charge.context

import me.choicore.samples.charge.context.ChargingMode.SURCHARGE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class ScenarioTests {
    @Test
    fun t1() {
        val surchargeTimeline: Timeline =
            Timeline(SUNDAY).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
                this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
            }

        val surchargeStrategy = ChargingStrategy(mode = SURCHARGE, rate = 20, timeline = surchargeTimeline)

        val dayOfWeekChargingStrategyRegistry: DayOfWeekChargingStrategyRegistry =
            DayOfWeekChargingStrategyRegistry().apply {
                this.register(surchargeStrategy)
            }

        val specifyDate: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(SUNDAY))

        val discountTimeline: Timeline =
            Timeline(specifyDate).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
                this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
            }

        val discountStrategy = ChargingStrategy(mode = ChargingMode.DISCHARGE, rate = 10, timeline = discountTimeline)

        val specifiedDateChargingStrategyRegistry: SpecifiedDateChargingStrategyRegistry =
            SpecifiedDateChargingStrategyRegistry().apply {
                this.register(discountStrategy)
            }

        val registries: List<AbstractChargingStrategyRegistry<out TemporalAdjuster>> =
            listOf(
                specifiedDateChargingStrategyRegistry,
                dayOfWeekChargingStrategyRegistry,
            )

        for (registry: ChargingStrategyRegistry in registries) {
            val strategies: List<ChargingStrategy> = registry.getChargingStrategies(specifyDate.plusWeeks(1))
            if (strategies.isNotEmpty()) {
                assertThat(strategies.first()).isEqualTo(surchargeStrategy)
                return
            }
        }
    }
}
