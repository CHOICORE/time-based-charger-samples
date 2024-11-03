package me.choicore.samples.charge.domain

import java.time.LocalDate

class ChargingStationSelector(
    stations: List<ChargingStation>,
) {
    private val stations: List<ChargingStation> = stations.sortedWith(ChargingStation.Comparators.withDefaults())
    private val cachedChargingStation: MutableMap<LocalDate, ChargingStation> = mutableMapOf()

    fun select(chargedOn: LocalDate): ChargingStation =
        this.cachedChargingStation.getOrPut(key = chargedOn) {
            this.stations
                .firstOrNull { station ->
                    when {
                        this.hasFullPeriod(station = station) -> chargedOn in station.startsOn!!..station.endsOn!!
                        this.hasOnlyStartsOn(station = station) -> chargedOn >= station.startsOn!!
                        this.hasOnlyEndsOn(station = station) -> chargedOn <= station.endsOn!!
                        else -> true
                    }
                }
                ?: throw IllegalStateException("No applicable ChargingStation found for the specified date")
        }

    private fun hasFullPeriod(station: ChargingStation): Boolean = station.startsOn != null && station.endsOn != null

    private fun hasOnlyStartsOn(station: ChargingStation): Boolean = station.startsOn != null && station.endsOn == null

    private fun hasOnlyEndsOn(station: ChargingStation): Boolean = station.startsOn == null && station.endsOn != null

    fun clear() {
        this.cachedChargingStation.clear()
    }
}
