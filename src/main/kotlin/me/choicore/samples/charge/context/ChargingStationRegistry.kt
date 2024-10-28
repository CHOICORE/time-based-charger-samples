package me.choicore.samples.charge.context

import java.time.LocalDate

class ChargingStationRegistry(
    private val stations: List<ChargingStation>,
) {
    private val cache: MutableMap<LocalDate, ChargingStation> = mutableMapOf()

    fun determine(specifiedDate: LocalDate): ChargingStation =
        this.cache.getOrPut(key = specifiedDate) {
            this.findChargingStation(
                specifiedDate = specifiedDate,
            )
        }

    private fun findChargingStation(specifiedDate: LocalDate): ChargingStation =
        this.stations
            .firstOrNull { station ->
                when {
                    hasFullPeriod(station = station) -> specifiedDate in station.startsOn!!..station.endsOn!!
                    hasOnlyStartsOn(station = station) -> specifiedDate >= station.startsOn!!
                    hasOnlyEndsOn(station = station) -> specifiedDate <= station.endsOn!!
                    else -> true
                }
            }
            ?: throw IllegalStateException("No applicable ChargingStation found for the specified date")

    private fun hasFullPeriod(station: ChargingStation): Boolean = station.startsOn != null && station.endsOn != null

    private fun hasOnlyStartsOn(station: ChargingStation): Boolean = station.startsOn != null && station.endsOn == null

    private fun hasOnlyEndsOn(station: ChargingStation): Boolean = station.startsOn == null && station.endsOn != null

    fun clear() {
        this.cache.clear()
    }
}
