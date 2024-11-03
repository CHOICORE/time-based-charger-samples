package me.choicore.samples.charge.domain

interface Charger {
    fun charge(chargeRequest: ChargeRequest, chargerChain: ChargerChain)
}