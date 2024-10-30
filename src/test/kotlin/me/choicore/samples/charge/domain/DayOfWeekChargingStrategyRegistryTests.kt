package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.EnumSet

class DayOfWeekChargingStrategyRegistryTests {
    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun `should return registered charging strategy for given day of week`(dayOfWeek: DayOfWeek) {
        // given
        val chargingStrategy = ChargingStrategy(ChargingMode.STANDARD, 100, Timeline.fullTime(dayOfWeek))

        val registry: DayOfWeekChargingStrategyRegistry =
            DayOfWeekChargingStrategyRegistry().apply {
                this.register(chargingStrategy)
            }

        // when
        val date: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(dayOfWeek))
        val result: List<ChargingStrategy> = registry.getChargingStrategies(date)

        // then
        assertThat(result).isNotEmpty
        assertThat(result[0]).isEqualTo(chargingStrategy)
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun `should return default charging strategy for given not registered day of week`(dayOfWeek: DayOfWeek) {
        // given
        val chargingStrategy =
            ChargingStrategy(
                ChargingMode.SURCHARGE,
                10,
                Timeline(dayOfWeek).apply { addSlot(LocalTime.MIDNIGHT, LocalTime.of(4, 0)) },
            )

        val registry: DayOfWeekChargingStrategyRegistry =
            DayOfWeekChargingStrategyRegistry().apply {
                this.register(chargingStrategy)
            }

        // when
        val unregisteredDays: Set<DayOfWeek> = EnumSet.allOf(DayOfWeek::class.java).apply { remove(dayOfWeek) }

        unregisteredDays.forEach { day ->
            val date: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(day))
            val result: List<ChargingStrategy> = registry.getChargingStrategies(date)

            // then
            assertThat(result).isNotEmpty
            assertThat(result[0]).isEqualTo(ChargingStrategy.noop(day))
        }
    }
}
