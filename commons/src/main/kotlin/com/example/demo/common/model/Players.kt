package com.example.demo.common.model

fun otherPlayer(player: Players): Players = when (player) {
    Players.HUMAN -> Players.COMPUTER
    else -> Players.HUMAN
}

enum class Players {
    HUMAN, COMPUTER

}

