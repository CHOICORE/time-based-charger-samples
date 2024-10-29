package me.choicore.samples.charge.context

import java.time.LocalDate

class ChargingStationRegistry(
    private val stations: List<ChargingStation>,
) {
    private val cache: MutableMap<LocalDate, ChargingStation> = mutableMapOf()

    fun determine(chargedOn: LocalDate): ChargingStation =
        this.cache.getOrPut(key = chargedOn) {
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
        this.cache.clear()
    }
}
