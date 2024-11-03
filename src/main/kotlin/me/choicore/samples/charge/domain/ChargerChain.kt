package me.choicore.samples.charge.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ChargerChain {
    fun charge(chargeRequest: ChargeRequest)

    class DefaultChargerChain(
        private val chargers: List<Charger>,
    ) : ChargerChain {
        private val size: Int = chargers.size
        private var currentPosition = 0

        override fun charge(chargeRequest: ChargeRequest) {
            if (this.currentPosition >= this.size) {
                return
            }

            this.currentPosition++
            val nextCharger: Charger = this.chargers[this.currentPosition - 1]
            log.info("ChargerChain: ${nextCharger.javaClass.simpleName} ($currentPosition/$size)")
            nextCharger.charge(chargeRequest, this)
        }

        companion object {
            val log: Logger = LoggerFactory.getLogger(DefaultChargerChain::class.java)
        }
    }

    companion object {
        fun create(chargers: List<Charger>): ChargerChain = DefaultChargerChain(chargers)
    }
}