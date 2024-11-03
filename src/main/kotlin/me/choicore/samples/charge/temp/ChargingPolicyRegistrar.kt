package me.choicore.samples.charge.temp

import org.springframework.stereotype.Service

@Service
class ChargingPolicyRegistrar(
    private val chargingPolicyRepository: ChargingPolicyRepository,
) {
    fun register(chargingPolicy: ChargingPolicy): ChargingPolicy = chargingPolicyRepository.save(chargingPolicy)
}
