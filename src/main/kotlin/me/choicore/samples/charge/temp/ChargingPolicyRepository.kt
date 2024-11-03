package me.choicore.samples.charge.temp

interface ChargingPolicyRepository {
    fun save(chargingPolicy: ChargingPolicy): ChargingPolicy

    fun getById(id: Long): ChargingPolicy

    fun findAllByComplexId(complexId: Long): List<ChargingPolicy>
}
