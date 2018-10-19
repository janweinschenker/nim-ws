package com.example.demo.entity

fun otherPlayer(player: Players): Players = when (player) {
    Players.HUMAN -> Players.COMPUTER
    else -> Players.HUMAN
}

enum class Players {
    HUMAN, COMPUTER

}

