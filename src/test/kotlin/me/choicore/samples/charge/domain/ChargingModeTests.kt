package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ChargingModeTests {
    @Test
    fun `should return the same amount when charging with none strategy`() {
        val none = ChargingMode.NONE
        val minutes = 1000L
        val rate = 10
        val charged: BigDecimal = none.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualByComparingTo(BigDecimal.valueOf(1000))
    }

    @Test
    fun `should return the surcharge amount when charging with surcharge strategy`() {
        val surcharge = ChargingMode.SURCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: BigDecimal = surcharge.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualByComparingTo(BigDecimal.valueOf(1100))
    }

    @Test
    fun `should return the discharge amount when charging with discharge strategy`() {
        val discharge = ChargingMode.DISCHARGE
        val minutes = 1000L
        val rate = 10
        val charged: BigDecimal = discharge.charge(amount = minutes, rate = rate)
        assertThat(charged).isEqualByComparingTo(BigDecimal.valueOf(900))
    }
}
