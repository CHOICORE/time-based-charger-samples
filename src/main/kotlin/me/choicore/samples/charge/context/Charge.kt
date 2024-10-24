package me.choicore.samples.charge.context

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

data class Charge(
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
) {
    private val _adjustments: MutableList<Adjustment> = mutableListOf()
    val adjustments: List<Adjustment> get() = this._adjustments.toList()
    val amount: Long
        get() {
            var amount: Long = TimeUtils.duration(unit = MINUTES, start = this.start, end = this.end)
            for (adjustment: Adjustment in adjustments) {
                amount -= adjustment.basis.duration(unit = MINUTES)
                amount += adjustment.amount
            }
            return amount
        }

    fun adjust(
        mode: ChargingMode,
        rate: Int,
        applied: TimeSlot,
        basis: TimeSlot,
    ) {
        this._adjustments.add(Adjustment(mode = mode, rate = rate, basis = basis, applied = applied))
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
    }
}