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
                mode = SURCHARGE,
                rate = 10,
                timeline = Timeline.fullTime(specifyDate = LocalDate.now()),
            )
        val strategies =
            CompositeChargingStrategies
                .builder()
                .once(chargingStrategy = surchargeStrategy)
                .repeatable(chargingStrategy = ChargingStrategy.noop(FRIDAY))
                .repeatable(chargingStrategy = ChargingStrategy.noop(SUNDAY))
                .repeatable(chargingStrategy = ChargingStrategy.noop(SATURDAY))
                .build()
    }
}
