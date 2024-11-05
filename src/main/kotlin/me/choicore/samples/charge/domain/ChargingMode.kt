package me.choicore.samples.charge.domain

import java.math.BigDecimal

enum class ChargingMode {
    NONE {
        override fun charge(
            amount: BigDecimal,
            rate: BigDecimal,
        ): BigDecimal = amount

        fun charge(amount: BigDecimal) {
            charge(amount = amount, rate = BigDecimal.ZERO)
        }
    },
    DISCHARGE {
        override fun charge(
            amount: BigDecimal,
            rate: BigDecimal,
        ): BigDecimal = amount.multiply(BigDecimal(100).subtract(rate).divide(BigDecimal(100)))
    },
    SURCHARGE {
        override fun charge(
            amount: BigDecimal,
            rate: BigDecimal,
        ): BigDecimal = amount.multiply(rate.add(BigDecimal(100)).divide(BigDecimal(100)))
    },
    ;

    abstract fun charge(
        amount: BigDecimal,
        rate: BigDecimal,
    ): BigDecimal

    fun charge(
        amount: Long,
        rate: Int,
    ): BigDecimal = charge(amount = BigDecimal(amount), rate = BigDecimal(rate))
}
