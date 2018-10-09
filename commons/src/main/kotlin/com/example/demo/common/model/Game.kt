package com.example.demo.common.model

data class Game(val id: Integer? = null,
                var remainingItems: Int = 0,
                var initialItems: Int = 13,
                var finished: Boolean = false,
                var nextPlayer: Players = Players.HUMAN)