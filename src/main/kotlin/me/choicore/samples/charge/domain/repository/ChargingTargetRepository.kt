package me.choicore.samples.charge.domain.repository

import me.choicore.samples.charge.domain.ChargingTarget

interface ChargingTargetRepository {
    fun save(chargingTarget: ChargingTarget): ChargingTarget
}
