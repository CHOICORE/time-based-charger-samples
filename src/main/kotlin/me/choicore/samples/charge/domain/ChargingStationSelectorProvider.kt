package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.repository.ChargingStationRepository

class ChargingStationSelectorProvider(
    private val chargingStationRepository: ChargingStationRepository,
) {
    fun getChargingStationSelector(complexId: Long): ChargingStationSelector {
        val stations: List<ChargingStation> = chargingStationRepository.findAllByComplexId(complexId = complexId)
        return ChargingStationSelector(stations = stations)
    }
}
