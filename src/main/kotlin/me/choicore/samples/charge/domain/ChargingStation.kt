package me.choicore.samples.charge.domain

import java.time.LocalDate

data class ChargingStation(
    val id: Long? = null,
    val complexId: Long,
    val name: String,
    val description: String?,
    val startsOn: LocalDate? = null,
    val endsOn: LocalDate? = null,
    val exemptionThreshold: Int,
    val dischargeAmount: Int,
    val deleted: Boolean = false,
) {
    object Comparators {
        fun withDefaults(): Comparator<ChargingStation> = compareBy({ it.startsOn == null }, { it.endsOn == null }, { it.startsOn })
    }
}
