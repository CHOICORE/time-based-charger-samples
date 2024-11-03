package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.repository.ChargingStationRepository

class ChargingStationRegistrar(
    private val chargingStationRepository: ChargingStationRepository,
) {
    fun register(chargingStation: ChargingStation): ChargingStation = chargingStationRepository.save(chargingStation)
}
