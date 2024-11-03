package me.choicore.samples.charge.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.common.jpa.AutoIncrement
import java.time.LocalDate

@Entity
@Table(name = "charging_station")
class ChargingStationEntity(
    val complexId: Long,
    val name: String,
    val description: String?,
    val startsOn: LocalDate?,
    val endsOn: LocalDate?,
    val exemptionThreshold: Int,
    val dischargeAmount: Int,
    val deleted: Boolean = false,
) : AutoIncrement()
