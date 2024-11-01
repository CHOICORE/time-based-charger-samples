package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStation.Comparators
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ChargingStationRegistryTests {
    private lateinit var chargingStationRegistry: ChargingStationRegistry

    @BeforeEach
    fun setUp() {
        chargingStationRegistry =
            ChargingStationRegistry(
                stations =
                    listOf(
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
                        ChargingStation(
                            id = 2,
                            complexId = 1,
                            name = "신규 정책",
                            description = "",
                            exemptionThreshold = 30,
                            dischargeAmount = 120,
                            startsOn = LocalDate.of(2024, 11, 1),
                            endsOn = null,
                        ),
                        ChargingStation(
                            id = 3,
                            complexId = 1,
                            name = "시범 정책",
                            description = "",
                            exemptionThreshold = 30,
                            dischargeAmount = 120,
                            startsOn = LocalDate.of(2024, 11, 1),
                            endsOn = LocalDate.of(2024, 11, 30),
                        ),
                        ChargingStation(
                            id = 4,
                            complexId = 1,
                            name = "신규 정책",
                            description = "",
                            exemptionThreshold = 30,
                            dischargeAmount = 120,
                            startsOn = null,
                            endsOn = LocalDate.of(2024, 11, 1),
                        ),
                    ).sortedWith(Comparators.withDefaults()),
            )
    }

    @AfterEach
    fun tearDown() {
        chargingStationRegistry.clear()
    }

    @Test
    fun `should apply trial policy within period`() {
        // given
        val trialPolicyDate: LocalDate = LocalDate.of(2024, 11, 15)

        // when
        val result: ChargingStation =
            chargingStationRegistry.determine(chargedOn = trialPolicyDate)

        // then
        assertThat(result.id).isEqualTo(3)
        assertThat(result.name).isEqualTo("시범 정책")
    }

    @Test
    fun `should apply policy on start date`() {
        // given
        val startDate: LocalDate = LocalDate.of(2024, 11, 1)

        // when
        val result: ChargingStation = chargingStationRegistry.determine(chargedOn = startDate)

        // then
        assertThat(result.id).isEqualTo(3)
        assertThat(result.name).isEqualTo("시범 정책")
    }

    @Test
    fun `should apply policy on end date`() {
        // given
        val endDate: LocalDate = LocalDate.of(2024, 11, 30)

        // when
        val result: ChargingStation = chargingStationRegistry.determine(chargedOn = endDate)

        // then
        assertThat(result.id).isEqualTo(3)
        assertThat(result.name).isEqualTo("시범 정책")
    }

    @Test
    fun `should apply policy after start date`() {
        // given
        val futureDate: LocalDate = LocalDate.of(2024, 12, 1)

        // when
        val result: ChargingStation = chargingStationRegistry.determine(chargedOn = futureDate)

        // then
        assertThat(result.id).isEqualTo(2)
        assertThat(result.name).isEqualTo("신규 정책")
    }

    @Test
    fun `should apply policy before end date`() {
        // given
        val pastDate: LocalDate = LocalDate.of(2024, 10, 1)

        // when
        val result: ChargingStation = chargingStationRegistry.determine(chargedOn = pastDate)

        // then
        assertThat(result.id).isEqualTo(4)
        assertThat(result.name).isEqualTo("신규 정책")
    }

    @Test
    fun `should throw exception when no stations available`() {
        // given
        val emptyRegistry = ChargingStationRegistry(emptyList())
        val testDate: LocalDate = LocalDate.of(2024, 11, 15)

        // when/then
        assertThatThrownBy { emptyRegistry.determine(chargedOn = testDate) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("No applicable ChargingStation found")
    }

    @Test
    fun `should apply first defined policy when multiple policies exist`() {
        // given
        val registry =
            ChargingStationRegistry(
                listOf(
                    ChargingStation(
                        id = 5,
                        complexId = 1,
                        name = "First Policy",
                        description = "",
                        exemptionThreshold = 30,
                        dischargeAmount = 120,
                        startsOn = LocalDate.of(2024, 12, 1),
                        endsOn = LocalDate.of(2024, 12, 31),
                    ),
                    ChargingStation(
                        id = 6,
                        complexId = 1,
                        name = "Second Policy",
                        description = "",
                        exemptionThreshold = 30,
                        dischargeAmount = 120,
                        startsOn = LocalDate.of(2024, 12, 1),
                        endsOn = LocalDate.of(2024, 12, 31),
                    ),
                ).sortedWith(Comparators.withDefaults()),
            )

        // when
        val result: ChargingStation = registry.determine(chargedOn = LocalDate.of(2024, 12, 15))

        // then
        assertThat(result.id).isEqualTo(5)
        assertThat(result.name).isEqualTo("First Policy")
    }
}
