package me.choicore.samples.charge.temp.infrastructure

import me.choicore.samples.charge.temp.ChargingPolicy
import me.choicore.samples.charge.temp.ChargingPolicy.Comparators
import me.choicore.samples.charge.temp.ChargingPolicyRepository
import org.springframework.stereotype.Repository

@Repository
class JpaChargingPolicyRepository(
    private val chargingPolicyEntityRepository: ChargingPolicyEntityRepository,
) : ChargingPolicyRepository {
    override fun save(chargingPolicy: ChargingPolicy): ChargingPolicy =
        chargingPolicyEntityRepository.save(ChargingPolicyEntity(chargingPolicy)).toChargingPolicy()

    override fun getById(id: Long): ChargingPolicy {
        val entity: ChargingPolicyEntity =
            chargingPolicyEntityRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Charging policy with id $id not found") }
        return entity.toChargingPolicy()
    }

    override fun findAllByComplexId(complexId: Long): List<ChargingPolicy> =
        chargingPolicyEntityRepository
            .findAllByComplexId(complexId)
            .map { it.toChargingPolicy() }
            .sortedWith(Comparators.withDefaults())
}
