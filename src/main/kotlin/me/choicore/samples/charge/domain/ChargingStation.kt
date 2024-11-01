package me.choicore.samples.charge.domain

import java.time.LocalDate

data class ChargingStation(
    val id: Long? = null,
    val complexId: Long,
    val name: String,
    val description: String?,
    val startsOn: LocalDate?,
    val endsOn: LocalDate?,
    val exemptionThreshold: Int,
    val dischargeAmount: Long,
    val deleted: Boolean = false,
) {
    object Comparators {
        fun withDefaults(): Comparator<ChargingStation> = compareBy({ it.startsOn == null }, { it.endsOn == null }, { it.startsOn })
    }
}
