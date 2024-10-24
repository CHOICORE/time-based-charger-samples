package me.choicore.samples.charge.context

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimelineTests {
    @Test
    fun `should return true when timeline is satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 1)
        assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false when timeline is not satisfied by the specified date`() {
        val timeline = Timeline(LocalDate.of(2024, 1, 1))
        val date: LocalDate = LocalDate.of(2024, 1, 2)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return false when timeline is not satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 17)
        assertThat(date.dayOfWeek).isNotEqualTo(timeline.dayOfWeek)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should return true when timeline is satisfied by the day of the week`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        val date: LocalDate = LocalDate.of(2024, 10, 18)
        assertThat(timeline.satisfiedBy(date)).isTrue
    }

    @Test
    fun `should return false when timeline is satisfied by the day of the week, but the specified date is not`() {
        val timeline = Timeline(LocalDate.of(2024, 10, 18))
        val date: LocalDate = LocalDate.of(2024, 10, 11)
        assertThat(timeline.dayOfWeek).isEqualTo(date.dayOfWeek)
        assertThat(timeline.satisfiedBy(date)).isFalse
    }

    @Test
    fun `should add a new time slot without throwing an exception`() {
        val timeline = Timeline(DayOfWeek.FRIDAY)
        timeline.addSlot(LocalTime.of(9, 0), LocalTime.of(17, 0))

        assertThatNoException().isThrownBy {
            timeline.addSlot(LocalTime.of(17, 0), LocalTime.of(19, 0))
        }
    }

    @Test
    fun `should throw an exception when adding overlapping time slot`() {
        val timeline: Timeline =
            Timeline(LocalDate.of(2024, 10, 18))
                .apply {
                    this.addSlot(TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0)))
                }

        assertThatThrownBy {
            timeline.addSlot(LocalTime.of(16, 59), LocalTime.of(19, 0))
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should not overlap when timelines have no overlapping slots`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(11, 0), LocalTime.of(12, 0))
            }

        assertThat(timeline.overlapsWith(other)).isFalse
        assertThat(other.overlapsWith(timeline)).isFalse
    }

    @Test
    fun `should overlap when timeline's previous slot overlaps`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(11, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 30), LocalTime.of(10, 30))
            }

        assertThat(timeline.overlapsWith(other)).isTrue
        assertThat(other.overlapsWith(timeline)).isTrue
    }

    @Test
    fun `should overlap when timeline's current slot overlaps`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 30), LocalTime.of(10, 30))
            }

        assertThat(timeline.overlapsWith(other)).isTrue
        assertThat(other.overlapsWith(timeline)).isTrue
    }

    @Test
    fun `should overlap when timeline's next slot overlaps`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(10, 0), LocalTime.of(11, 0))
            }

        assertThat(timeline.overlapsWith(other)).isFalse
        assertThat(other.overlapsWith(timeline)).isFalse
    }

    @Test
    fun `should handle multiple slots with some overlapping and some not`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(8, 0), LocalTime.of(9, 0))
                this.addSlot(LocalTime.of(11, 0), LocalTime.of(12, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
                this.addSlot(LocalTime.of(10, 30), LocalTime.of(11, 30))
            }

        assertThat(timeline.overlapsWith(other)).isTrue
        assertThat(other.overlapsWith(timeline)).isTrue
    }

    @Test
    fun `should not overlap when timelines are on different days`() {
        val timeline: Timeline =
            Timeline(DayOfWeek.MONDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        val other: Timeline =
            Timeline(DayOfWeek.TUESDAY).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        assertThat(timeline.overlapsWith(other)).isFalse
        assertThat(other.overlapsWith(timeline)).isFalse
    }

    @Test
    fun `should overlap when timelines have the same specified date`() {
        val date: LocalDate = LocalDate.of(2024, 10, 20)

        val timeline: Timeline =
            Timeline(date).apply {
                this.addSlot(LocalTime.of(9, 0), LocalTime.of(10, 0))
            }

        val other: Timeline =
            Timeline(date).apply {
                this.addSlot(LocalTime.of(9, 30), LocalTime.of(10, 30))
            }

        assertThat(timeline.overlapsWith(other)).isTrue
        assertThat(other.overlapsWith(timeline)).isTrue
    }

    @Test
    fun `should new timeline with full time slot for specified date `() {
        val specifiedDate: LocalDate = LocalDate.of(2024, 10, 18)
        val timeline: Timeline = Timeline.fullTime(specifiedDate)
        assertThat(timeline.slots).hasSize(1)
        assertThat(timeline.slots[0].startTimeInclusive).isEqualTo(LocalTime.MIN)
        assertThat(timeline.slots[0].endTimeInclusive).isEqualTo(LocalTime.MAX)
    }

    @Test
    fun `should new timeline with full time slot for the day of the week`() {
        val dayOfWeek: DayOfWeek = DayOfWeek.FRIDAY
        val timeline: Timeline = Timeline.fullTime(dayOfWeek)
        assertThat(timeline.slots).hasSize(1)
        assertThat(timeline.slots[0].startTimeInclusive).isEqualTo(LocalTime.MIN)
        assertThat(timeline.slots[0].endTimeInclusive).isEqualTo(LocalTime.MAX)
    }
}
