package me.choicore.samples.charge.domain

data class ChargeRequest(
    val target: ChargingTarget,
    val station: ChargingStation,
)
