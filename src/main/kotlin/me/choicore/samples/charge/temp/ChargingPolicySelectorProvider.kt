package me.choicore.samples.charge.temp

import org.springframework.stereotype.Service

@Service
class ChargingPolicySelectorProvider(
    private val chargingPolicyRepository: ChargingPolicyRepository,
) {
    fun getChargingPolicySelector(complexId: Long): ChargingPolicySelector {
        val policies: List<ChargingPolicy> = chargingPolicyRepository.findAllByComplexId(complexId = complexId)
        return ChargingPolicySelector(policies = policies)
    }
}
