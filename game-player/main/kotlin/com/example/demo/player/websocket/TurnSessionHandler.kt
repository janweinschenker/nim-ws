package com.example.demo.player.websocket

import com.example.demo.common.model.TurnNotification
import com.example.demo.player.game.Player
import mu.KLogging
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import java.lang.reflect.Type

/**
 * Handle websocket session functionality.
 */
class TurnSessionHandler(val gameId: Long, val player: Player) : StompSessionHandlerAdapter() {

    companion object : KLogging()


    /**
     * called directly after the session has been established.
     */
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        session.subscribe("/topic/turns/$gameId", this)
        logger.info("New session for turn notifications: {} subscribed to game {}", session.sessionId, gameId)
    }

    /**
     * called in case an exception is thrown.
     */
    override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
        exception.printStackTrace()
    }

    /**
     * Get the type of the payload object.
     */
    override fun getPayloadType(headers: StompHeaders): Type {
        return TurnNotification::class.java
    }

    /**
     * handle a frame received by the server.
     */
    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        when ((payload as TurnNotification).finished) {
            true -> {
                logger.info("Game is finished. {} won.", payload.winner)
            }
            false -> {
                player.playTurnAfter(payload)
            }
        }


    }

}