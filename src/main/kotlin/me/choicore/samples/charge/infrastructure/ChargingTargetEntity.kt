package me.choicore.samples.charge.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.charge.domain.ChargingStatus
import me.choicore.samples.charge.domain.ChargingTarget
import me.choicore.samples.common.jpa.AutoIncrement
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "charging_target")
class ChargingTargetEntity(
    val complexId: Long,
    val building: String,
    val unit: String,
    val licensePlate: String,
    val arrivedAt: LocalDateTime,
    val departedAt: LocalDateTime?,
    var status: ChargingStatus,
    var lastChargedOn: LocalDate? = null,
) : AutoIncrement() {
    constructor(chargingTarget: ChargingTarget) : this(
        complexId = chargingTarget.complexId,
        building = chargingTarget.building,
        unit = chargingTarget.unit,
        licensePlate = chargingTarget.licensePlate,
        arrivedAt = chargingTarget.arrivedAt,
        departedAt = chargingTarget.departedAt,
        status = chargingTarget.status,
        lastChargedOn = chargingTarget.lastChargedOn,
    )
}
