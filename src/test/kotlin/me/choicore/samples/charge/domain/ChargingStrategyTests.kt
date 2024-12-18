package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingMode.DISCHARGE
import me.choicore.samples.charge.domain.ChargingMode.SURCHARGE
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
            ChargingStrategy(
                stationId = 1L,
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = date),
            )
        assertThat(chargingStrategy.supports(selectedDate = date)).isTrue
    }

    @Test
    fun `should return false when strategy is not supported for specified date`() {
        val date: LocalDate = LocalDate.now()
        val chargingStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = date),
            )
        assertThat(chargingStrategy.supports(selectedDate = date.plusDays(1))).isFalse
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek::class)
    fun `should return true when strategy is supported for specified day of week`(dayOfWeek: DayOfWeek) {
        val date: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(dayOfWeek))
        val chargingStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = date),
            )
        assertThat(chargingStrategy.supports(selectedDate = date)).isTrue
    }

    @Test
    fun `should attempt and apply charge adjustment with correct amount`() {
        // given
        val chargingUnit =
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = LocalDate.now(),
                startTime = LocalTime.MIN,
                endTime = LocalTime.MAX,
            )

        val chargingStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = DISCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = LocalDate.now()),
            )

        // when
        chargingStrategy.attempt(chargingUnit = chargingUnit)

        // then
        assertThat(chargingUnit.adjustments).hasSize(1)
        assertThat(chargingUnit.adjustments[0].amount).isEqualTo(1296L)
    }

    @Test
    fun `should exempt charge when exempt strategy is applied`() {
        // given
        val chargingUnit =
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = LocalDate.now(),
                startTime = LocalTime.MIN,
                endTime = LocalTime.MAX,
            )

        val chargingStrategy: ChargingStrategy =
            ChargingStrategy.exempt(stationId = 1L, dayOfWeek = LocalDate.now().dayOfWeek)

        // when
        chargingStrategy.attempt(chargingUnit = chargingUnit)

        // then
        assertThat(chargingUnit.adjustments).hasSize(1)
        assertThat(chargingUnit.adjustments[0].amount).isEqualTo(0)
    }
}
