package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingMode.SURCHARGE
import me.choicore.samples.charge.domain.ChargingStation.Comparators
import me.choicore.samples.charge.domain.ChargingStatus.CHARGED
import me.choicore.samples.charge.domain.ChargingStatus.CHARGING
import me.choicore.samples.charge.domain.ChargingStatus.REGISTERED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class ScenarioTests {
    @Test
    @DisplayName("정책 적용 시나리오")
    fun t1() {
        val surchargeTimeline: Timeline =
            Timeline(SUNDAY).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
                this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
            }

        val surchargeStrategy = ChargingStrategy(mode = SURCHARGE, rate = 20, timeline = surchargeTimeline)

        val dayOfWeekChargingStrategyRegistry: DayOfWeekChargingStrategyRegistry =
            DayOfWeekChargingStrategyRegistry().apply {
                this.register(surchargeStrategy)
            }

        val specifyDate: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(SUNDAY))

        val discountTimeline: Timeline =
            Timeline(specifyDate).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(12, 0))
                this.addSlot(LocalTime.of(14, 0), LocalTime.of(16, 0))
            }

        val discountStrategy = ChargingStrategy(mode = ChargingMode.DISCHARGE, rate = 10, timeline = discountTimeline)

        val specifiedDateChargingStrategyRegistry: SpecifiedDateChargingStrategyRegistry =
            SpecifiedDateChargingStrategyRegistry().apply {
                this.register(discountStrategy)
            }

        val registries: List<AbstractChargingStrategyRegistry<out TemporalAdjuster>> =
            listOf(
                specifiedDateChargingStrategyRegistry,
                dayOfWeekChargingStrategyRegistry,
            )

        for (registry: ChargingStrategyRegistry in registries) {
            val strategies: List<ChargingStrategy> = registry.getChargingStrategies(date = specifyDate.plusWeeks(1))
            if (strategies.isNotEmpty()) {
                assertThat(strategies.first()).isEqualTo(surchargeStrategy)
                return
            }
        }
    }

    @Test
    @DisplayName("정산 시나리오")
    fun t2() {
        val chargedOn: LocalDate = LocalDate.now()
        val chargingTargets: List<ChargingTarget> =
            listOf(
                ChargingTarget(
                    id = 1,
                    complexId = 1,
                    building = "A",
                    unit = "101",
                    licensePlate = "123가1234",
                    arrivedAt = LocalDateTime.now(),
                    departedAt = LocalDateTime.now().plusMinutes(25),
                    status = REGISTERED,
                    lastChargedOn = null,
                ),
            )

        val chargingStationRegistry: ChargingStationRegistry = getChargingStationRegistry()

        for (chargingTarget: ChargingTarget in chargingTargets) {
            var currentChargedOn: LocalDate = chargingTarget.lastChargedOn ?: chargingTarget.arrivedOn
            while (currentChargedOn <= chargedOn) {
                val chargingStation: ChargingStation = chargingStationRegistry.determine(chargedOn = currentChargedOn)
                val chargingUnit: ChargingUnit = chargingTarget.getChargingUnit(chargedOn = currentChargedOn)

                chargingTarget.lastChargedOn = currentChargedOn
                chargingTarget.status = if (chargingTarget.departedOn == currentChargedOn) CHARGED else CHARGING
                currentChargedOn = currentChargedOn.plusDays(1)
            }
        }
    }

    private fun getChargingStationRegistry(): ChargingStationRegistry =
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
                        name = "신규 정책",
                        complexId = 1,
                        description = "",
                        exemptionThreshold = 30,
                        dischargeAmount = 120,
                        startsOn = LocalDate.of(2024, 10, 28),
                        endsOn = null,
                    ),
            ).sortedWith(Comparators.withDefaults()),
        )
}
