package me.choicore.samples.charge.context

import me.choicore.samples.charge.context.ChargingMode.DISCHARGE
import me.choicore.samples.charge.context.ChargingMode.SURCHARGE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

class ChargingStrategyTests {
    @Test
    fun `should return true when strategy is supported for specified date`() {
        val date: LocalDate = LocalDate.now()
        val chargingStrategy =
            ChargingStrategy(mode = SURCHARGE, rate = 10, timeline = Timeline.fullTime(specifyDate = date))
        assertThat(chargingStrategy.supports(selectedDate = date)).isTrue
    }

    @Test
    fun `should return false when strategy is not supported for specified date`() {
        val date: LocalDate = LocalDate.now()
        val chargingStrategy =
            ChargingStrategy(mode = SURCHARGE, rate = 10, timeline = Timeline.fullTime(specifyDate = date))
        assertThat(chargingStrategy.supports(selectedDate = date.plusDays(1))).isFalse
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun `should return true when strategy is supported for specified day of week`(dayOfWeek: DayOfWeek) {
        val date: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(dayOfWeek))
        val chargingStrategy =
            ChargingStrategy(
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = date),
            )
        assertThat(chargingStrategy.supports(selectedDate = date)).isTrue
    }

    @Test
    fun `should attempt and apply charge adjustment with correct amount`() {
        // given
        val charge =
            Charge(
                date = LocalDate.now(),
                start = LocalTime.MIN,
                end = LocalTime.MAX,
            )

        val chargingStrategy =
            ChargingStrategy(
                mode = DISCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = LocalDate.now()),
            )

        // when
        chargingStrategy.attempt(charge = charge)

        // then
        assertThat(charge.adjustments).hasSize(1)
        assertThat(charge.adjustments[0].amount).isEqualTo(1296L)
        println(charge.amount)
    }
}
