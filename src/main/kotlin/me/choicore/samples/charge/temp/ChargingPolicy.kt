package me.choicore.samples.charge.temp

import java.time.LocalDate

data class ChargingPolicy(
    val policyId: Long? = null,
    val complexId: Long,
    val name: String,
    val description: String?,
    val exemptionThreshold: Int,
    val dischargeAmount: Int,
    val startsOn: LocalDate?,
    val endsOn: LocalDate?,
    val deleted: Boolean = false,
) {
    object Comparators {
        fun withDefaults(): Comparator<ChargingPolicy> = compareBy({ it.startsOn == null }, { it.endsOn == null }, { it.startsOn })
    }
}
