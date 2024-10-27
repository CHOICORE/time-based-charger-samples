package me.choicore.samples.common.jpa

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class AutoIncrement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private var _id: Long? = null

    val id: Long
        get() {
            return this._id
                ?: throw IllegalStateException("Primary key is not set. Maybe the entity is not persistent yet.")
        }
}
