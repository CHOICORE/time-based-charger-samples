package me.choicore.samples.charge.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChargingStationRepository : JpaRepository<ChargingStationEntity, Long>
