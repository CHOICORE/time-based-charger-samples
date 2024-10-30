package me.choicore.samples.charge.domain

interface ChargingStationRepository {
    fun save(chargingStation: ChargingStation): ChargingStation

    fun getChargingStationsByComplexId(complexId: Long): List<ChargingStation>
}
