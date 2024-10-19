package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ChargingStrategyTests {
    @Test
    fun `should return the same amount when charging with noop strategy`() {
        val noop = ChargingStrategy.NOOP
        val minutes = 1000L
        val rate = 10
        val charged: Double = noop.charge(minutes, rate)
        Assertions.assertThat(charged).isEqualTo(1000.0)
    }

    @Test
    fun `should return the surcharge amount when charging with surcharge strategy`() {
        val surcharge = ChargingStrategy.SURCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: Double = surcharge.charge(minutes, rate)
        Assertions.assertThat(charged).isEqualTo(1100.0)
    }

    @Test
    fun `should return the discharge amount when charging with discharge strategy`() {
        val discharge = ChargingStrategy.DISCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: Double = discharge.charge(minutes, rate)
        Assertions.assertThat(charged).isEqualTo(900.0)
    }
}
