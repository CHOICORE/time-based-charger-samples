package me.choicore.samples.charge.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.choicore.samples.common.jpa.AutoIncrement
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "charging_strategy_timeline")
class ChargingStrategyTimelineEntity(
    val strategyId: Long,
    val dayOfWeek: DayOfWeek,
    val specifiedDate: LocalDate?,
    val startTime: LocalTime,
    val endTime: LocalTime,
) : AutoIncrement()
