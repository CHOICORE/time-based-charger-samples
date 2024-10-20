package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ChargingStrategyTests {
    @Test
    fun `should return true when strategy is supported for specified date`() {
        val date: LocalDate = LocalDate.now()
        val chargingStrategy = ChargingStrategy(ChargingMode.SURCHARGE, 10, Timeline.fullTime(date))
        assertThat(chargingStrategy.supports(date)).isTrue
    }

    @Test
    fun `should return false when strategy is not supported for specified date`() {
        val date: LocalDate = LocalDate.now()
        val chargingStrategy = ChargingStrategy(ChargingMode.SURCHARGE, 10, Timeline.fullTime(date))
        assertThat(chargingStrategy.supports(date.plusDays(1))).isFalse
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun `should return true when strategy is supported for specified day of week`(dayOfWeek: DayOfWeek) {
        val date: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(dayOfWeek))
        val chargingStrategy = ChargingStrategy(ChargingMode.SURCHARGE, 10, Timeline.fullTime(date))
        assertThat(chargingStrategy.supports(date)).isTrue
    }
}
