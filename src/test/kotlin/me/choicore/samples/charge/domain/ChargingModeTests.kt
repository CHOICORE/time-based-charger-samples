package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChargingModeTests {
    @Test
    fun `should return the same amount when charging with none strategy`() {
        val none = ChargingMode.NONE
        val minutes = 1000L
        val rate = 10
        val charged: Double = none.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualTo(1000.0)
    }

    @Test
    fun `should return the surcharge amount when charging with surcharge strategy`() {
        val surcharge = ChargingMode.SURCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: Double = surcharge.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualTo(1100.0)
    }

    @Test
    fun `should return the discharge amount when charging with discharge strategy`() {
        val discharge = ChargingMode.DISCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: Double = discharge.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualTo(900.0)
    }
}
