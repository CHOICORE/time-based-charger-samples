package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingStatus.CHARGED
import me.choicore.samples.charge.domain.ChargingStatus.CHARGING
import me.choicore.samples.charge.domain.ChargingStatus.REGISTERED
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

class ScenarioTests {
    @Test
    fun t1() {
        val chargingTargets: List<ChargingTarget> =
            listOf(
                ChargingTarget(
                    id = 1,
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
                    id = 2,
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
                ChargerChain
                    .create(emptyList())
                    .charge(ChargeRequest(chargedOn = currentChargedOn))
                chargingTarget.lastChargedOn = currentChargedOn
                chargingTarget.status = if (chargingTarget.departedOn == currentChargedOn) CHARGED else CHARGING
                currentChargedOn = currentChargedOn.plusDays(1)
            }
        }
    }

    @Test
    fun t2() {
        val dayOfWeek = LocalDate.now().dayOfWeek
        val surchargeStrategy =
            ChargingStrategy(
                mode = ChargingMode.SURCHARGE,
                rate = 10,
                timeline =
                    Timeline(dayOfWeek).apply {
                        addSlot(
                            startTimeInclusive = LocalTime.of(0, 0),
                            endTimeInclusive = LocalTime.of(4, 0),
                        )
                    },
            )
        val dischargeStrategy1 =
            ChargingStrategy(
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline =
                    Timeline(dayOfWeek).apply {
                        addSlot(
                            startTimeInclusive = LocalTime.of(6, 0),
                            endTimeInclusive = LocalTime.of(8, 0),
                        )
                    },
            )
        val dischargeStrategy2 =
            ChargingStrategy(
                mode = ChargingMode.DISCHARGE,
                rate = 100,
                timeline =
                    Timeline(dayOfWeek).apply {
                        addSlot(
                            startTimeInclusive = LocalTime.of(9, 0),
                            endTimeInclusive = LocalTime.of(18, 0),
                        )
                    },
            )

        val strategies: CompositeChargingStrategies =
            CompositeChargingStrategies
                .builder()
                .repeatable(chargingStrategy = surchargeStrategy)
                .repeatable(chargingStrategy = dischargeStrategy1)
                .repeatable(chargingStrategy = dischargeStrategy2)
                .build()

        val chargingUnit =
            ChargingUnit(
                id = 1,
                targetId = 1,
                complexId = 1,
                building = "A",
                unit = "101",
                licensePlate = "123가1234",
                chargedOn = LocalDate.now(),
                startTime = LocalTime.MIN,
                endTime = LocalTime.of(23, 0),
            )

        println("청구 날짜: ${chargingUnit.chargedOn} start: ${chargingUnit.startTime} end: ${chargingUnit.endTime}")
        strategies.getChargingStrategies(date = LocalDate.now()).forEach { it.attempt(chargingUnit) }
        chargingUnit.adjustments.forEach {
            println(
                "감지된 구간: ${it.basis}, 실제 적용된 구간: ${it.applied} ${it.rate}% ${it.mode.label()}, 정산: ${it.amount}분",
            )
        }
        println(
            "총 이용 시간: ${
                TimeUtils.duration(
                    chargingUnit.startTime,
                    chargingUnit.endTime,
                    MINUTES,
                )
            }분, 총 부과 시간: ${chargingUnit.amount}분",
        )
    }

    fun ChargingMode.label(): String =
        when (this) {
            ChargingMode.NONE -> " - "
            ChargingMode.SURCHARGE -> "할증"
            ChargingMode.DISCHARGE -> "할인"
        }
}
