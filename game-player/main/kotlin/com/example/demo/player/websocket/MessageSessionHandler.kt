package com.example.demo.player.websocket

import com.example.demo.common.model.Message
import mu.KLogging
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import java.lang.reflect.Type

class MessageSessionHandler(val gameId: Long) : StompSessionHandlerAdapter() {

    companion object : KLogging()


    /**
     * called directly after the session has been established.
     */
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        session.subscribe("/topic/messages/$gameId", this)
        TurnSessionHandler.logger.info("New session for messages: {} subscribed to game {}", session.sessionId, gameId)
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
        return Message::class.java
    }

    /**
     * handle a frame received by the server.
     */
    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        val message: Message = payload as Message
        logger.info { message.text }
    }
}