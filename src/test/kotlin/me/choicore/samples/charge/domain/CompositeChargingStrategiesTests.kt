package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.ChargingMode.SURCHARGE
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate

class CompositeChargingStrategiesTests {
    @Test
    fun t1() {
        val surchargeStrategy =
            ChargingStrategy(
                stationId = 1L,
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = LocalDate.now()),
            )
        val strategies =
            CompositeChargingStrategies
                .builder(
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
                ).once(chargingStrategy = surchargeStrategy)
                .once(
                    chargingStrategy =
                        surchargeStrategy.copy(
                            timeline =
                                Timeline.fullTime(
                                    specifyDate = LocalDate.now().plusDays(1),
                                ),
                        ),
                ).repeatable(chargingStrategy = ChargingStrategy.standard(stationId = 1L, dayOfWeek = FRIDAY))
                .repeatable(chargingStrategy = ChargingStrategy.standard(stationId = 1L, dayOfWeek = SUNDAY))
                .repeatable(chargingStrategy = ChargingStrategy.standard(stationId = 1L, dayOfWeek = SATURDAY))
                .build()

        strategies.getChargingStrategies(date = LocalDate.now()).forEach { println(it) }
    }
}
