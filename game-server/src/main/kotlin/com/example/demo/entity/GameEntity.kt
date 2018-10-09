package com.example.demo.entity

import com.example.demo.common.model.Players
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * The entity representing a nim game.
 */
@Entity
@Table(name = "game")
class GameEntity(@Id
                 var id: Long = Random().nextLong(),
                 var initialItems: Int,
                 var remainingItems: Int = initialItems,
                 var finished: Boolean = false,
                 var nextPlayer: Players = Players.HUMAN,
                 var winner: Players? = null) {

    // Empty constructor needed for mapstruct.
    constructor() : this(initialItems = 13)
}
