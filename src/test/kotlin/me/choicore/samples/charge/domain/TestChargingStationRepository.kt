package me.choicore.samples.charge.domain

import me.choicore.samples.charge.domain.repository.ChargingStationRepository
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

class TestChargingStationRepository : ChargingStationRepository {
    private val inMemory: MutableMap<Long, ChargingStation> = mutableMapOf()
    private val autoIncrement = AtomicLong(1L)

    init {
        this.save(
            ChargingStation(
                id = autoIncrement.getAndIncrement(),
                name = "기본 정책",
                complexId = 1,
                description = "",
                exemptionThreshold = 30,
                dischargeAmount = 120,
                startsOn = null,
                endsOn = null,
            ),
        )
        this.save(
            ChargingStation(
                id = autoIncrement.getAndIncrement(),
                complexId = 1,
                name = "신규 정책",
                description = "",
                exemptionThreshold = 30,
                dischargeAmount = 120,
                startsOn = LocalDate.of(2024, 11, 1),
                endsOn = null,
            ),
        )
        this.save(
            ChargingStation(
                id = autoIncrement.getAndIncrement(),
                complexId = 1,
                name = "시범 정책",
                description = "",
                exemptionThreshold = 30,
                dischargeAmount = 120,
                startsOn = LocalDate.of(2024, 11, 1),
                endsOn = LocalDate.of(2024, 11, 30),
            ),
        )

        this.save(
            ChargingStation(
                id = autoIncrement.getAndIncrement(),
                complexId = 1,
                name = "신규 정책",
                description = "",
                exemptionThreshold = 30,
                dischargeAmount = 120,
                startsOn = null,
                endsOn = LocalDate.of(2024, 10, 31),
            ),
        )
    }

    override fun save(chargingStation: ChargingStation): ChargingStation {
        val id: Long = chargingStation.id ?: autoIncrement.getAndIncrement()
        return chargingStation.copy(id = id).apply { inMemory[id] = this }
    }

    override fun findAllByComplexId(complexId: Long): List<ChargingStation> = inMemory.values.filter { it.complexId == complexId }
}
