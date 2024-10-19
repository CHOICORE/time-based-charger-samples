package me.choicore.samples.charge.context

enum class ChargingStrategy {
    NOOP {
        override fun charge(
            amount: Long,
            rate: Int,
        ): Double = amount.toDouble()
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
    }, ;

    abstract fun charge(
        amount: Long,
        rate: Int,
    ): Double
}
