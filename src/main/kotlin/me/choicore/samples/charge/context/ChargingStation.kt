package me.choicore.samples.charge.context

import java.time.LocalDate

data class ChargingStation(
    val id: Long? = null,
    val complexId: Long,
    val name: String,
    val description: String?,
    val startsOn: LocalDate?,
    val endsOn: LocalDate?,
    val deleted: Boolean = false,
) {
    object Supports {
        fun comparator(): Comparator<ChargingStation> = compareBy({ it.startsOn == null }, { it.endsOn == null }, { it.startsOn })
    }
}
