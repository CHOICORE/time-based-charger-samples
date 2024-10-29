package me.choicore.samples.charge.context

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ChargingTarget(
    val id: Long? = null,
    val complexId: Long,
    val building: String,
    val unit: String,
    val licensePlate: String,
    val arrivedAt: LocalDateTime,
    val departedAt: LocalDateTime?,
    var status: ChargingStatus,
    var lastChargedOn: LocalDate? = null,
) {
    val arrivedOn: LocalDate = arrivedAt.toLocalDate()

    val departedOn: LocalDate? = departedAt?.toLocalDate()

    fun getChargingUnit(chargedOn: LocalDate): ChargingUnit {
        val startTime: LocalTime =
            if (this.arrivedOn == chargedOn) {
                this.arrivedAt.toLocalTime()
            } else {
                LocalTime.MIN
            }

        val endTime: LocalTime =
            if (this.departedOn == chargedOn) {
                this.departedAt!!.toLocalTime()
            } else {
                LocalTime.MAX
            }

        return ChargingUnit(
            chargedOn = chargedOn,
            startTime = startTime,
            endTime = endTime,
        )
    }
}
