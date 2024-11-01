package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TimeSlotTests {
    @Test
    fun `should not throw an exception for valid time slot`() {
        assertThatNoException().isThrownBy {
            TimeSlot(LocalTime.of(9, 0), LocalTime.of(9, 1))
        }
    }

    @Test
    fun `should throw an exception for time slot with same start and end time`() {
        assertThatIllegalArgumentException()
            .isThrownBy {
                TimeSlot(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
            }
    }

    @Test
    fun `should throw an exception for invalid time slot where start time is after end time`() {
        assertThatIllegalArgumentException()
            .isThrownBy {
                TimeSlot(LocalTime.MAX, LocalTime.MIN)
            }
    }

    @Test
    fun `should return true if other timeSlot is within the current timeSlot`() {
        val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
        val otherTimeSlot = TimeSlot(LocalTime.of(14, 0), LocalTime.of(19, 0))
        assertThat(timeSlot.overlapsWith(otherTimeSlot)).isTrue
    }

    @Test
    fun `should return false if other timeSlot is without the current timeSlot`() {
        val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
        val otherTimeSlot = TimeSlot(LocalTime.of(15, 0), LocalTime.of(19, 0))
        assertThat(timeSlot.overlapsWith(otherTimeSlot)).isFalse
    }

    @Test
    fun `should return extracted time slot within the specified range`() {
        val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
        val extractedTimeSlot: TimeSlot? = timeSlot.extractWithin(LocalTime.of(10, 0), LocalTime.of(14, 0))
        assertThat(extractedTimeSlot).isNotNull
        assertThat(extractedTimeSlot?.startTimeInclusive).isEqualTo(LocalTime.of(10, 0))
        assertThat(extractedTimeSlot?.endTimeInclusive).isEqualTo(LocalTime.of(14, 0))
    }

    @Test
    fun `should return null when extracting time slot within the specified range`() {
        val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
        val extractedTimeSlot: TimeSlot? = timeSlot.extractWithin(LocalTime.of(8, 0), LocalTime.of(9, 0))
        assertThat(extractedTimeSlot).isNull()
    }

    @Test
    fun `should return true if the time slot is full time`() {
        val timeSlot = TimeSlot(LocalTime.MIN, LocalTime.MAX)
        assertThat(timeSlot.isFullTime()).isTrue
    }

    @ParameterizedTest
    @EnumSource(names = ["HOURS", "MINUTES", "SECONDS"])
    fun `should calculate the duration in specified unit`(unit: ChronoUnit) {
        val startTimeInclusive: LocalTime = LocalTime.of(9, 0)
        val endTimeInclusive: LocalTime = LocalTime.of(15, 0)
        val timeSlot = TimeSlot(startTimeInclusive, endTimeInclusive)

        assertThat(timeSlot.duration(unit = unit)).isEqualTo(
            TimeUtils.duration(
                start = startTimeInclusive,
                end = endTimeInclusive,
                unit = unit,
            ),
        )
    }

    @Test
    fun `should return a time slot covering the entire day for FULL_TIME`() {
        assertThat(TimeSlot.FULL_TIME).isEqualTo(TimeSlot(LocalTime.MIN, LocalTime.MAX))
    }
}
