package me.choicore.samples.charge.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChargingStrategyTimelineEntityRepository : JpaRepository<ChargingStrategyTimelineEntity, Long>
