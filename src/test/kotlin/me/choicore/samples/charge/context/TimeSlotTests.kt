package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import java.time.LocalTime

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
        val extractedTimeSlot = timeSlot.extractWithin(LocalTime.of(10, 0), LocalTime.of(14, 0))
        assertThat(extractedTimeSlot).isNotNull
        assertThat(extractedTimeSlot?.startTimeInclusive).isEqualTo(LocalTime.of(10, 0))
        assertThat(extractedTimeSlot?.endTimeInclusive).isEqualTo(LocalTime.of(14, 0))
    }

    @Test
    fun `should return null when extracting time slot within the specified range`() {
        val timeSlot = TimeSlot(LocalTime.of(9, 0), LocalTime.of(15, 0))
        val extractedTimeSlot = timeSlot.extractWithin(LocalTime.of(8, 0), LocalTime.of(9, 0))
        assertThat(extractedTimeSlot).isNull()
    }

    @Test
    fun `should return true if the time slot is full time`() {
        val timeSlot = TimeSlot(LocalTime.MIN, LocalTime.MAX)
        assertThat(timeSlot.isFullTime()).isTrue
    }
}
