package me.choicore.samples.charge.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChargingUnitEntityRepository : JpaRepository<ChargingUnitEntity, Long>
