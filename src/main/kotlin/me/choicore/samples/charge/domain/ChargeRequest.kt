package me.choicore.samples.charge.domain

import java.time.LocalDate

data class ChargeRequest(
    val chargedOn: LocalDate,
) {
    class Context
}
