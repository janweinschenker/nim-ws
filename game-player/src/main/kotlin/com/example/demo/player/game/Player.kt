package com.example.demo.player.game

import com.example.demo.common.model.Players
import com.example.demo.common.model.TurnNotification
import com.example.demo.player.restclient.Client
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class Player(val client: Client,
             @Value("\${nimdojo.config.player:}") val playerName: String) {

    companion object : KLogging()

    fun playTurnAfter(notification: TurnNotification) {
        if (isItMyTurn(notification)) {
            val playerTakes = computerMove(notification.remainingItems!!)
            client.patchGame(notification.gameId!!, playerTakes)
        }
    }

    private fun isItMyTurn(notification: TurnNotification): Boolean = notification.nextPlayer == Players.valueOf(playerName)

    /**
     * Very simple and not very intelligent calculation of next the computer's move.
     * @param the gameEntity
     *
     * return the number of tokens the computer will take
     */
    private fun computerMove(count: Int): Int = when (count) {
        2 -> 1
        3 -> 2
        else -> if (Math.random() <= 0.5) 1 else 2
    }


}