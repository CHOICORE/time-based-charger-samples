package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ChargingStationRegistrarTests {
    @Test
    fun `should save charging station`() {
        // given
        val chargingStation =
            ChargingStation(
                complexId = 1,
                name = "Test Station",
                description = "Test Description",
                startsOn = LocalDate.now(),
                endsOn = LocalDate.now().plusDays(1),
                exemptionThreshold = 10,
                dischargeAmount = 20,
            )
        val chargingStationRepository = TestChargingStationRepository()
        val registrar = ChargingStationRegistrar(chargingStationRepository)

        // when
        val result: ChargingStation = registrar.register(chargingStation)

        // then
        assertThat(result.id).isNotNull
        assertThat(result.complexId).isEqualTo(chargingStation.complexId)
        assertThat(result.name).isEqualTo(chargingStation.name)
        assertThat(result.description).isEqualTo(chargingStation.description)
        assertThat(result.startsOn).isEqualTo(chargingStation.startsOn)
        assertThat(result.endsOn).isEqualTo(chargingStation.endsOn)
        assertThat(result.exemptionThreshold).isEqualTo(chargingStation.exemptionThreshold)
        assertThat(result.dischargeAmount).isEqualTo(chargingStation.dischargeAmount)
        assertThat(result.deleted).isFalse
    }

    @Test
    fun `should save charging station with default values`() {
        // given
        val chargingStation =
            ChargingStation(
                complexId = 1,
                name = "Test Station",
                description = "Test Description",
                exemptionThreshold = 10,
                dischargeAmount = 20,
            )
        val chargingStationRepository = TestChargingStationRepository()
        val registrar = ChargingStationRegistrar(chargingStationRepository)

        // when
        val result: ChargingStation = registrar.register(chargingStation)

        // then
        assertThat(result.id).isNotNull
        assertThat(result.startsOn).isNull()
        assertThat(result.endsOn).isNull()
    }

    @Test
    fun `should save charging station with default values and null fields`() {
        // given
        val chargingStation =
            ChargingStation(
                complexId = 1,
                name = "Test Station",
                description = null,
                exemptionThreshold = 10,
                dischargeAmount = 20,
                startsOn = null,
                endsOn = null,
            )
        val chargingStationRepository = TestChargingStationRepository()
        val registrar = ChargingStationRegistrar(chargingStationRepository)

        // when
        val result: ChargingStation = registrar.register(chargingStation)

        // then
        assertThat(result.id).isNotNull
        assertThat(result.description).isNull()
        assertThat(result.startsOn).isNull()
        assertThat(result.endsOn).isNull()
    }
}
