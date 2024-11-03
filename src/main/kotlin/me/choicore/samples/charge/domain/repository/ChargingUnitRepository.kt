package me.choicore.samples.charge.domain.repository

import me.choicore.samples.charge.domain.ChargingUnit

interface ChargingUnitRepository {
    fun save(chargingUnit: ChargingUnit): ChargingUnit
}
