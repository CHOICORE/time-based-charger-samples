package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingMode.DISCHARGE
import me.choicore.samples.charge.domain.ChargingMode.SURCHARGE
import me.choicore.samples.charge.domain.ChargingStation.Supports
import me.choicore.samples.charge.domain.ChargingStatus.CHARGED
import me.choicore.samples.charge.domain.ChargingStatus.CHARGING
import me.choicore.samples.charge.domain.ChargingStatus.REGISTERED
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

class ChargerTests {
    @Test
    fun t1() {
        val chargingStationRegistry: ChargingStationRegistry = getChargingStationRegistry()

        val chargingTargets: List<ChargingTarget> =
            listOf(
                ChargingTarget(
                    complexId = 1,
                    building = "A",
                    unit = "101",
                    licensePlate = "123가1234",
                    arrivedAt = LocalDateTime.now(),
                    departedAt = LocalDateTime.now().plusHours(3),
                    status = REGISTERED,
                    lastChargedOn = null,
                ),
                ChargingTarget(
                    complexId = 1,
                    building = "A",
                    unit = "102",
                    licensePlate = "123가1234",
                    arrivedAt = LocalDateTime.now().minusDays(5),
                    departedAt = null,
                    status = REGISTERED,
                    lastChargedOn = null,
                ),
            )

        val chargedOn: LocalDate = LocalDate.now()

        for (chargingTarget: ChargingTarget in chargingTargets) {
            var currentChargedOn: LocalDate = chargingTarget.lastChargedOn ?: chargingTarget.arrivedOn
            while (currentChargedOn <= chargedOn) {
                val chargingUnit: ChargingUnit = chargingTarget.getChargingUnit(chargedOn = currentChargedOn)
                val request =
                    ChargeRequest(
                        chargingStation = chargingStationRegistry.determine(currentChargedOn),
                        chargingUnit = chargingUnit,
                    )
                ChargerChainManager(getChargerChain(), getChargers()).charge(request)
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
                        startsOn = null,
                        endsOn = null,
                    ),
                    ChargingStation(
                        id = 2,
                        name = "신규 정책",
                        complexId = 1,
                        description = "",
                        startsOn = LocalDate.of(2024, 10, 28),
                        endsOn = null,
                    ),
                ).sortedWith(Supports.comparator()),
        )

    private fun getChargerChain() =
        object : ChargerChain {
            override fun charge(chargeRequest: ChargeRequest) {
                val charge: ChargingUnit = chargeRequest.chargingUnit
                println(
                    "Station: ${chargeRequest.chargingStation.name} - Charged on ${charge.chargedOn} from ${charge.startTime} to ${charge.endTime}",
                )
                charge.adjustments.forEach {
                    println("조정 내역: $it")
                }
                println(
                    "총 이용 시간(분): ${
                        TimeUtils.duration(charge.startTime, charge.endTime, MINUTES)
                    }분, 적용 시간(분): ${charge.amount}분",
                )
            }
        }

    private fun getChargers(): List<Charger> =
        listOf(
            object : Charger {
                override fun charge(
                    chargeRequest: ChargeRequest,
                    chargerChain: ChargerChain,
                ) {
                    val chargeStrategy =
                        ChargingStrategy(
                            mode = DISCHARGE,
                            rate = 100,
                            timeline =
                                Timeline(specifyDate = chargeRequest.chargingUnit.chargedOn).apply {
                                    this.addSlot(
                                        TimeSlot(
                                            startTimeInclusive = LocalTime.of(9, 0),
                                            endTimeInclusive = LocalTime.of(18, 0),
                                        ),
                                    )
                                },
                        )
                    chargeRequest.chargingUnit.adjust(chargeStrategy)
                    chargerChain.charge(chargeRequest)
                }
            },
            object : Charger {
                override fun charge(
                    chargeRequest: ChargeRequest,
                    chargerChain: ChargerChain,
                ) {
                    val chargeStrategy =
                        ChargingStrategy(
                            mode = SURCHARGE,
                            rate = 100,
                            timeline =
                                Timeline(specifyDate = chargeRequest.chargingUnit.chargedOn).apply {
                                    this.addSlot(
                                        TimeSlot(
                                            startTimeInclusive = LocalTime.of(18, 0),
                                            endTimeInclusive = LocalTime.of(22, 0),
                                        ),
                                    )
                                },
                        )
                    chargeRequest.chargingUnit.adjust(chargeStrategy)
                    chargerChain.charge(chargeRequest)
                }
            },
        )

    class ChargerChainManager(
        private val originalChargerChain: ChargerChain,
        private val chargers: List<Charger>,
    ) : ChargerChain {
        private val size: Int = chargers.size
        private var currentPosition = 0

        override fun charge(chargeRequest: ChargeRequest) {
            if (this.currentPosition == this.size) {
                this.originalChargerChain.charge(chargeRequest)
                return
            } else {
                this.currentPosition += 1
                val charger: Charger = this.chargers[currentPosition - 1]
                charger.charge(chargeRequest, this)
            }
        }
    }

    interface ChargerChain {
        fun charge(chargeRequest: ChargeRequest)
    }

    interface Charger {
        fun charge(
            chargeRequest: ChargeRequest,
            chargerChain: ChargerChain,
        )
    }

    data class ChargeRequest(
        val chargingStation: ChargingStation,
        val chargingUnit: ChargingUnit,
    )
}
