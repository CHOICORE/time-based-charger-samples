package me.choicore.samples.charge.domain

import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * `TimeSlot` 클래스는 시작 시간과 종료 시간을 포함하는 시간 구간을 표현합니다.
 *
 * @property startTimeInclusive 이 시간 슬롯의 시작 시간(포함).
 * @property endTimeInclusive 이 시간 슬롯의 종료 시간(포함).
 * @constructor `startTimeInclusive`가 `endTimeInclusive`보다 이전이어야 합니다.
 */
data class TimeSlot(
    val startTimeInclusive: LocalTime,
    val endTimeInclusive: LocalTime,
) {
    init {
        require(this.startTimeInclusive.isBefore(this.endTimeInclusive)) {
            "startTimeInclusive must be before endTimeInclusive"
        }
    }

    fun duration(unit: ChronoUnit): Long =
        TimeUtils.duration(
            start = this.startTimeInclusive,
            end = this.endTimeInclusive,
            unit = unit,
        )

    /**
     * 주어진 다른 `TimeSlot`과 이 `TimeSlot`이 겹치는지 여부를 확인합니다.
     *
     * @param other 비교할 다른 `TimeSlot`.
     * @return 두 `TimeSlot`이 겹치는 경우 `true`; 그렇지 않으면 `false`.
     */
    fun overlapsWith(other: TimeSlot): Boolean = startTimeInclusive < other.endTimeInclusive && endTimeInclusive > other.startTimeInclusive

    /**
     * 주어진 시간 범위 내에서 이 `TimeSlot`의 부분을 추출합니다.
     *
     * @param startTimeInclusive 추출할 시작 시간 (포함).
     * @param endTimeInclusive 추출할 종료 시간 (포함).
     * @return 지정된 범위 내에 있는 `TimeSlot`의 부분. 범위 내에 겹치는 부분이 없다면 `null`을 반환합니다.
     */
    fun extractWithin(
        startTimeInclusive: LocalTime,
        endTimeInclusive: LocalTime,
    ): TimeSlot? {
        if (this.isFullTime()) {
            return TimeSlot(startTimeInclusive, endTimeInclusive)
        } else {
            val start: LocalTime = maxOf(this.startTimeInclusive, startTimeInclusive)
            val end: LocalTime = minOf(this.endTimeInclusive, endTimeInclusive)
            return if (start.isBefore(end)) TimeSlot(start, end) else null
        }
    }

    /**
     * 이 시간 구간이 전체 시간을 나타내는지 확인합니다.
     *
     * @return 이 `TimeSlot`이 하루 전체를 나타내면 `true`, 그렇지 않으면 `false`.
     */
    fun isFullTime(): Boolean = this.startTimeInclusive == LocalTime.MIN && this.endTimeInclusive == LocalTime.MAX
}
