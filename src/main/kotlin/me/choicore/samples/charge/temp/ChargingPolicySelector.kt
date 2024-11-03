package me.choicore.samples.charge.temp

import java.time.LocalDate

class ChargingPolicySelector(
    private val policies: List<ChargingPolicy>,
) {
    private val cachedChargingPolicy: MutableMap<LocalDate, ChargingPolicy> = mutableMapOf()

    fun select(chargedOn: LocalDate): ChargingPolicy =
        this.cachedChargingPolicy.getOrPut(key = chargedOn) {
            this.policies
                .firstOrNull { station ->
                    when {
                        this.hasFullPeriod(station = station) -> chargedOn in station.startsOn!!..station.endsOn!!
                        this.hasOnlyStartsOn(station = station) -> chargedOn >= station.startsOn!!
                        this.hasOnlyEndsOn(station = station) -> chargedOn <= station.endsOn!!
                        else -> true
                    }
                }
                ?: throw IllegalStateException("No applicable ChargingPolicy found for the specified date")
        }

    private fun hasFullPeriod(station: ChargingPolicy): Boolean = station.startsOn != null && station.endsOn != null

    private fun hasOnlyStartsOn(station: ChargingPolicy): Boolean = station.startsOn != null && station.endsOn == null

    private fun hasOnlyEndsOn(station: ChargingPolicy): Boolean = station.startsOn == null && station.endsOn != null

    fun clear() {
        this.cachedChargingPolicy.clear()
    }
}
