package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SpecifiedDateChargingStrategiesTests {
    @Test
    fun `should return registered charging strategy for given specified date`() {
        // given
        val date: LocalDate = LocalDate.now()

        val surchargingStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = ChargingMode.SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = date),
            )

        val registry: SpecifiedDateChargingStrategies =
            SpecifiedDateChargingStrategies(
                ChargingStation(
                    id = 1,
                    name = "기본 정책",
                    complexId = 1,
                    description = "",
                    exemptionThreshold = 30,
                    dischargeAmount = 120,
                    startsOn = null,
                    endsOn = null,
                ),
            ).apply {
                this.register(strategy = surchargingStrategy)
            }

        // when
        val result: List<ChargingStrategy> = registry.getChargingStrategies(date = date)

        // given
        assertThat(result).isNotEmpty
        assertThat(result[0]).isEqualTo(surchargingStrategy)
    }

    @Test
    fun `should return default charging strategy for given not registered date`() {
        // given
        val date: LocalDate = LocalDate.now()

        val surchargingStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = ChargingMode.SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(date),
            )

        val registry: SpecifiedDateChargingStrategies =
            SpecifiedDateChargingStrategies(
                ChargingStation(
                    id = 1,
                    name = "기본 정책",
                    complexId = 1,
                    description = "",
                    exemptionThreshold = 30,
                    dischargeAmount = 120,
                    startsOn = null,
                    endsOn = null,
                ),
            ).apply {
                this.register(strategy = surchargingStrategy)
            }

        // when
        val result: List<ChargingStrategy> = registry.getChargingStrategies(date = date.plusDays(1))

        // given
        assertThat(result).isEmpty()
    }
}
