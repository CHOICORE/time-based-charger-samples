package me.choicore.samples.charge.context

import me.choicore.samples.charge.context.ChargingMode.DISCHARGE
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test

class ChargeTests {
    @Test
    fun `should calculate correct amount for full day charge without adjustments`() {
        // given
        val charge =
            Charge(
                date = LocalDate.now(),
                start = LocalTime.MIN,
                end = LocalTime.MAX,
            )

        // when & then
        assertThat(charge.amount).isEqualTo(1440L)
    }

    @Test
    fun `should properly add single adjustment and calculate amount`() {
        // given
        val charge =
            Charge(
                date = LocalDate.now(),
                start = LocalTime.of(9, 0),
                end = LocalTime.of(18, 0),
            )

        val basis =
            TimeSlot(
                startTimeInclusive = LocalTime.of(9, 0),
                endTimeInclusive = LocalTime.of(18, 0),
            )

        val applied =
            TimeSlot(
                startTimeInclusive = LocalTime.of(9, 0),
                endTimeInclusive = LocalTime.of(18, 0),
            )

        // when
        charge.adjust(
            mode = DISCHARGE,
            rate = 10,
            applied = applied,
            basis = basis,
        )

        // then
        assertThat(charge.adjustments).hasSize(1)
        val adjustment = charge.adjustments[0]
        assertThat(adjustment.mode).isEqualTo(DISCHARGE)
        assertThat(adjustment.rate).isEqualTo(10)
        assertThat(adjustment.basis).isEqualTo(basis)
        assertThat(adjustment.applied).isEqualTo(applied)

        // amount(540) - basis duration(540) + adjustment amount
        val expectedAmount = adjustment.amount
        assertThat(charge.amount).isEqualTo(expectedAmount)
    }

    @Test
    fun `should throw exception for invalid time range`() {
        assertThatThrownBy {
            Charge(
                date = LocalDate.now(),
                start = LocalTime.of(18, 0),
                end = LocalTime.of(9, 0),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
