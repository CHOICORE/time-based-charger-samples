package me.choicore.samples.charge.context

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class ChargerTests {
    @Test
    fun t1() {
        val chargeRequest = ChargeRequest(
            chargedOn = LocalDate.now(),
            startTime = LocalTime.of(23, 30),
            endTime = LocalTime.MAX,
        )
        val chargers: List<Charger> = getChargers()
        val chargerChain = object : ChargerChain {
            override fun charge(chargeRequest: ChargeRequest) {
                println("Charged on ${chargeRequest.chargedOn} from ${chargeRequest.startTime} to ${chargeRequest.endTime}")
            }
        }

        DefaultChargerChain(chargerChain, chargers).charge(chargeRequest)
    }

    private fun getChargers(): List<Charger> {
        return listOf(
            object : Charger {
                override fun charge(chargeRequest: ChargeRequest, chargerChain: ChargerChain) {
                    println("Charger 1")
                    chargerChain.charge(chargeRequest)
                }
            },
            object : Charger {
                override fun charge(chargeRequest: ChargeRequest, chargerChain: ChargerChain) {
                    println("Charger 2")
                    chargerChain.charge(chargeRequest)
                }
            },
        )
    }

    class DefaultChargerChain(
        private val originalChargerChain: ChargerChain,
        private val chargers: List<Charger>,
    ) : ChargerChain {
        private val size: Int = chargers.size
        private var currentPosition = 0

        override fun charge(chargeRequest: ChargeRequest) {
            if (this.currentPosition == this.size) {
                this.originalChargerChain.charge(chargeRequest)
                return
            } else {
                this.currentPosition += 1
                this.chargers[currentPosition - 1].charge(chargeRequest, this)
            }
        }
    }


    interface ChargerChain {
        fun charge(chargeRequest: ChargeRequest)
    }

    interface Charger {
        fun charge(chargeRequest: ChargeRequest, chargerChain: ChargerChain)
    }

    data class ChargeRequest(
        val chargedOn: LocalDate,
        val startTime: LocalTime,
        val endTime: LocalTime,
    )
}