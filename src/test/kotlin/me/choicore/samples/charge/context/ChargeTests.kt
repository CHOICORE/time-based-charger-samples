package me.choicore.samples.charge.context

import me.choicore.samples.charge.context.Charge.Adjustment
import me.choicore.samples.charge.context.ChargingMode.DISCHARGE
import me.choicore.samples.charge.context.ChargingMode.SURCHARGE
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
        val specifyDate: LocalDate = LocalDate.now()
        val charge =
            Charge(
                date = specifyDate,
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
            ChargingStrategy(
                mode = DISCHARGE,
                rate = 10,
                timeline =
                    Timeline(specifyDate = specifyDate).apply {
                        this.addSlot(basis)
                    },
            ),
        )

        // then
        assertThat(charge.adjustments).hasSize(1)
        val adjustment: Adjustment = charge.adjustments[0]
        assertThat(adjustment.mode).isEqualTo(DISCHARGE)
        assertThat(adjustment.rate).isEqualTo(10)
        assertThat(adjustment.basis).isEqualTo(basis)
        assertThat(adjustment.applied).isEqualTo(applied)
        assertThat(charge.amount).isEqualTo(adjustment.amount)
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

    @Test
    fun `should calculate correct amount with multiple valid adjustments`() {
        // given
        val specifyDate: LocalDate = LocalDate.now()
        val charge =
            Charge(
                date = specifyDate,
                start = LocalTime.of(9, 0),
                end = LocalTime.of(18, 0),
            )

        val chargingStrategy =
            ChargingStrategy(
                mode = SURCHARGE,
                rate = 10,
                timeline =
                    Timeline(specifyDate = specifyDate).apply {
                        this.addSlot(LocalTime.of(9, 0), LocalTime.of(12, 0))
                        this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
                    },
            )

        // when
        charge.adjust(chargingStrategy)

        // then
        assertThat(charge.adjustments).hasSize(2)
        assertThat(charge.amount).isEqualTo(570)
    }
}
