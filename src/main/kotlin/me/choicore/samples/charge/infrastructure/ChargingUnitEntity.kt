package me.choicore.samples.charge.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.charge.domain.ChargingUnit
import me.choicore.samples.common.jpa.AutoIncrement
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "charging_unit")
class ChargingUnitEntity(
    val targetId: Long,
    val complexId: Long,
    val building: String,
    val unit: String,
    val licensePlate: String,
    val chargedOn: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    var deleted: Boolean = false,
) : AutoIncrement() {
    constructor(chargingUnit: ChargingUnit) : this(
        targetId = chargingUnit.targetId,
        complexId = chargingUnit.complexId,
        building = chargingUnit.building,
        unit = chargingUnit.unit,
        licensePlate = chargingUnit.licensePlate,
        chargedOn = chargingUnit.chargedOn,
        startTime = chargingUnit.startTime,
        endTime = chargingUnit.endTime,
        deleted = chargingUnit.deleted,
    )
}
