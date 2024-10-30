package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingMode.DISCHARGE
import me.choicore.samples.charge.domain.ChargingMode.SURCHARGE
import me.choicore.samples.charge.domain.ChargingUnit.Adjustment
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test

class ChargingUnitTests {
    @Test
    fun `should calculate correct amount for full day charge without adjustments`() {
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

        // when & then
        assertThat(chargingUnit.amount).isEqualTo(1440L)
    }

    @Test
    fun `should properly add single adjustment and calculate amount`() {
        // given
        val chargedOn: LocalDate = LocalDate.now()
        val chargingUnit =
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = chargedOn,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(18, 0),
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
        chargingUnit.adjust(
            ChargingStrategy(
                mode = DISCHARGE,
                rate = 10,
                timeline =
                    Timeline(specifyDate = chargedOn).apply {
                        this.addSlot(basis)
                    },
            ),
        )

        // then
        assertThat(chargingUnit.adjustments).hasSize(1)
        val adjustment: Adjustment = chargingUnit.adjustments[0]
        assertThat(adjustment.mode).isEqualTo(DISCHARGE)
        assertThat(adjustment.rate).isEqualTo(10)
        assertThat(adjustment.basis).isEqualTo(basis)
        assertThat(adjustment.applied).isEqualTo(applied)
        assertThat(chargingUnit.amount).isEqualTo(adjustment.amount)
    }

    @Test
    fun `should throw exception for invalid time range`() {
        assertThatThrownBy {
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = LocalDate.now(),
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(9, 0),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should calculate correct amount with multiple valid adjustments`() {
        // given
        val chargedOn: LocalDate = LocalDate.now()
        val chargingUnit =
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = chargedOn,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(18, 0),
            )

        val chargingStrategy =
            ChargingStrategy(
                mode = SURCHARGE,
                rate = 10,
                timeline =
                    Timeline(specifyDate = chargedOn).apply {
                        this.addSlot(LocalTime.of(9, 0), LocalTime.of(12, 0))
                        this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
                    },
            )

        // when
        chargingUnit.adjust(chargingStrategy)

        // then
        assertThat(chargingUnit.adjustments).hasSize(2)
        assertThat(chargingUnit.amount).isEqualTo(570)
    }

    @Test
    fun `should calculate correct amount with single valid adjustment`() {
        // given
        val chargedOn: LocalDate = LocalDate.now()
        val chargingUnit =
            ChargingUnit(
                targetId = 1,
                complexId = 1,
                building = "1",
                unit = "1",
                licensePlate = "1",
                chargedOn = chargedOn,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
            )

        val chargingStrategy =
            ChargingStrategy(
                mode = SURCHARGE,
                rate = 10,
                timeline =
                    Timeline(specifyDate = chargedOn).apply {
                        this.addSlot(LocalTime.of(9, 0), LocalTime.of(12, 0))
                    },
            )

        // when
        chargingUnit.adjust(chargingStrategy)

        // then
        assertThat(chargingUnit.adjustments).hasSize(1)
        assertThat(chargingUnit.amount).isEqualTo(66)
    }
}
