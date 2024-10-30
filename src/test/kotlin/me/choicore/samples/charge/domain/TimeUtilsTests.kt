package me.choicore.samples.charge.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TimeUtilsTests {
    @Test
    fun `should adjust duration from 00_00_00 to 23_59_59_999999999 as 1440 minutes`() {
        val duration: Long =
            TimeUtils.duration(
                start = LocalTime.MIN,
                end = LocalTime.MAX,
                unit = ChronoUnit.MINUTES,
            )

        assertThat(duration).isEqualTo(1_440)
    }

    @Test
    fun `should adjust duration from 00_00_00 to 23_59_59_999999999 as 86400 seconds`() {
        val duration: Long =
            TimeUtils.duration(
                start = LocalTime.MIN,
                end = LocalTime.MAX,
                unit = ChronoUnit.SECONDS,
            )

        assertThat(duration).isEqualTo(86_400)
    }

    @Test
    fun `should adjust duration from 00_00_00 to 23_59_59_999999999 as 24 hours`() {
        val duration: Long =
            TimeUtils.duration(
                start = LocalTime.MIN,
                end = LocalTime.MAX,
                unit = ChronoUnit.HOURS,
            )

        assertThat(duration).isEqualTo(24)
    }

    @Test
    fun `should adjust duration from 00_00_00 to 23_59_59_999999999 as 1 days`() {
        val duration: Long =
            TimeUtils.duration(
                start = LocalTime.MIN,
                end = LocalTime.MAX,
                unit = ChronoUnit.DAYS,
            )

        assertThat(duration).isEqualTo(1)
    }
}
