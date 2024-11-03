package me.choicore.samples.charge.temp.infrastructure

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.choicore.samples.charge.temp.ChargingPolicy
import java.time.LocalDate

@Entity
@Table(name = "charging_policy")
class ChargingPolicyEntity private constructor(
    val complexId: Long,
    val name: String,
    val description: String?,
    val exemptionThreshold: Int,
    val dischargeAmount: Int,
    val startsOn: LocalDate?,
    val endsOn: LocalDate?,
    val deleted: Boolean = false,
) {
    constructor(chargingPolicy: ChargingPolicy) : this(
        complexId = chargingPolicy.complexId,
        name = chargingPolicy.name,
        description = chargingPolicy.description,
        exemptionThreshold = chargingPolicy.exemptionThreshold,
        dischargeAmount = chargingPolicy.dischargeAmount,
        startsOn = chargingPolicy.startsOn,
        endsOn = chargingPolicy.endsOn,
        deleted = chargingPolicy.deleted,
    ) {
        this._id = chargingPolicy.policyId
    }

    @Id
    @Column(name = "policy_id")
    private var _id: Long? = null

    val id: Long
        get() = _id!!

    fun toChargingPolicy(): ChargingPolicy =
        ChargingPolicy(
            policyId = _id,
            complexId = complexId,
            name = name,
            description = description,
            exemptionThreshold = exemptionThreshold,
            dischargeAmount = dischargeAmount,
            startsOn = startsOn,
            endsOn = endsOn,
            deleted = deleted,
        )
}
