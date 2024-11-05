package me.choicore.samples.charge.domain

enum class ChargingMode {
    NONE {
        override fun charge(
            amount: Long,
            rate: Int,
        ): Double = amount.toDouble()

        fun charge(amount: Long) {
            charge(amount = amount, rate = 0)
        }
    },
    DISCHARGE {
        override fun charge(
            amount: Long,
            rate: Int,
        ): Double = (100.0 - rate) * amount / 100.0
    },
    SURCHARGE {
        override fun charge(
            amount: Long,
            rate: Int,
        ): Double = amount * (rate + 100.0) / 100.0
    },
    ;

    abstract fun charge(
        amount: Long,
        rate: Int,
    ): Double
}
