package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test

class ChargingStationSelectorProviderTests {
    @Test
    fun t1() {
        assertThatNoException().isThrownBy {
            val chargingStationSelectorProvider = ChargingStationSelectorProvider(TestChargingStationRepository())
            chargingStationSelectorProvider.getChargingStationSelector(complexId = 1L)
        }
    }
}
