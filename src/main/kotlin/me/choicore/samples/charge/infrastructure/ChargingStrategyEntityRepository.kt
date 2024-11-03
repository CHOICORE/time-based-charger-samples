package me.choicore.samples.charge.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChargingStrategyEntityRepository : JpaRepository<ChargingStrategyEntity, Long>
