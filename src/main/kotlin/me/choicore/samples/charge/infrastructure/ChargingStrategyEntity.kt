package me.choicore.samples.charge.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.charge.domain.ChargingMode
import me.choicore.samples.common.jpa.AutoIncrement

@Entity
@Table(name = "charging_strategy")
class ChargingStrategyEntity(
    val stationId: Long,
    val mode: ChargingMode,
    val rate: Int,
) : AutoIncrement()
