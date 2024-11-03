package me.choicore.samples.charge.temp.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChargingPolicyEntityRepository : JpaRepository<ChargingPolicyEntity, Long> {
    fun findAllByComplexId(complexId: Long): List<ChargingPolicyEntity>
}
