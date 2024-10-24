package me.choicore.samples.charge.context

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

data class Charge(
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
) {
    init {
        require(this.start.isBefore(this.end)) { "The start time must be before the end time." }
    }

    private val _adjustments: MutableList<Adjustment> = mutableListOf()
    val adjustments: List<Adjustment> get() = this._adjustments.toList()
    val amount: Long
        get() {
            val initial: Long = TimeUtils.duration(unit = MINUTES, start = this.start, end = this.end)
            return this._adjustments.fold(initial) { amount, adjustment -> adjustment.adjustTo(amount) }
        }

    fun adjust(strategy: ChargingStrategy) {
        require(strategy.supports(this.date)) { "The specified date does not satisfy the timeline." }

        strategy.timeline.slots.forEach { slot ->
            val basis: TimeSlot = slot
            val applied: TimeSlot? = slot.extractWithin(this.start, this.end)
            if (applied != null) {
                this.addAdjustment(
                    Adjustment(
                        mode = strategy.mode,
                        rate = strategy.rate,
                        basis = basis,
                        applied = applied,
                    ),
                )
            }
        }
    }

    private fun addAdjustment(adjustment: Adjustment) {
        this._adjustments.add(adjustment)
        this._adjustments.sortBy { it.basis.startTimeInclusive }
    }

    data class Adjustment(
        val mode: ChargingMode,
        val rate: Int,
        val basis: TimeSlot,
        val applied: TimeSlot,
    ) {
        val amount: Long =
            mode
                .charge(
                    amount = applied.duration(unit = MINUTES),
                    rate = this.rate,
                ).toLong()

        fun adjustTo(amount: Long): Long = amount - this.computeOriginalAmount() + this.computeChargedAmount()

        private fun computeOriginalAmount(): Long = basis.duration(unit = MINUTES)

        private fun computeChargedAmount(): Long =
            mode
                .charge(
                    amount = applied.duration(unit = MINUTES),
                    rate = this.rate,
                ).toLong()
    }
}
