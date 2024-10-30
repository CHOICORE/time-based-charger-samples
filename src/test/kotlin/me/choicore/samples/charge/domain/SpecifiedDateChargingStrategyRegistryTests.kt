package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SpecifiedDateChargingStrategyRegistryTests {
    @Test
    fun `should return registered charging strategy for given specified date`() {
        // given
        val date: LocalDate = LocalDate.now()

        val surchargingStrategy = ChargingStrategy(ChargingMode.SURCHARGE, 10, Timeline.fullTime(date))

        val registry: SpecifiedDateChargingStrategyRegistry =
            SpecifiedDateChargingStrategyRegistry().apply {
                this.register(surchargingStrategy)
            }

        // when
        val result: List<ChargingStrategy> = registry.getChargingStrategies(date)

        // given
        assertThat(result).isNotEmpty
        assertThat(result[0]).isEqualTo(surchargingStrategy)
    }

    @Test
    fun `should return default charging strategy for given not registered date`() {
        // given
        val date: LocalDate = LocalDate.now()

        val surchargingStrategy = ChargingStrategy(ChargingMode.SURCHARGE, 10, Timeline.fullTime(date))

        val registry: SpecifiedDateChargingStrategyRegistry =
            SpecifiedDateChargingStrategyRegistry().apply {
                this.register(surchargingStrategy)
            }

        // when
        val result: List<ChargingStrategy> = registry.getChargingStrategies(date.plusDays(1))

        // given
        assertThat(result).isEmpty()
    }
}
