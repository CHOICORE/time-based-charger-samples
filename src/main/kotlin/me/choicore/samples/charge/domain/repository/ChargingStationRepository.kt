package me.choicore.samples.charge.domain.repository

import me.choicore.samples.charge.domain.ChargingStation

interface ChargingStationRepository {
    fun save(chargingStation: ChargingStation): ChargingStation

    fun findAllByComplexId(complexId: Long): List<ChargingStation>
}
