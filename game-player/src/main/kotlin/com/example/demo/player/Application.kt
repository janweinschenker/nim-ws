package com.example.demo.player

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.socket.messaging.WebSocketStompClient


@SpringBootApplication
open class Application {
    /**
     * The Websocket client to receive events from the server.
     */
    @Autowired
    lateinit var stompClient: WebSocketStompClient

    @Value("\${nimdojo.config.gameId:}")
    lateinit var gameId: String

    @Value("\${nimdojo.config.wsTurnUrl:}")
    lateinit var turnUrl: String

    @Value("\${nimdojo.config.wsMsgUrl:}")
    lateinit var msgUrl: String


}


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}