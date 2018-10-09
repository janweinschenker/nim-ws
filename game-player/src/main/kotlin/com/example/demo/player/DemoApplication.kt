package com.example.demo.player

import com.example.demo.player.game.Player
import com.example.demo.player.restclient.Client
import com.example.demo.player.websocket.MessageSessionHandler
import com.example.demo.player.websocket.TurnSessionHandler
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient


@SpringBootApplication
open class DemoApplication {

    companion object : KLogging()

    /**
     * Player implementation (a would-be AI)
     */
    @Autowired
    lateinit var player: Player

    /**
     * The Rest client to send Restful requests
     */
    @Autowired
    lateinit var messageClient: Client

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

    /**
     * Run the client application
     */
    @Bean
    open fun run(): CommandLineRunner = CommandLineRunner {
        logger.info { "message: ${messageClient.postGame("/games/2?count=13")}" }
        logger.info { "message: ${messageClient.postGame("/games/3?count=13")}" }
        messageClient.getGames("/games")?.forEach { game -> logger.info(game.toString()) }

        // connect with the websocket server and use the session handlers to handle the events coming
        // from the server.
        stompClient.connect(turnUrl, TurnSessionHandler(gameId.toLong(), player))
        stompClient.connect(msgUrl, MessageSessionHandler(gameId.toLong()))
    }

    /**
     * Create a websocket client and register with the game server websocket.
     */
    @Bean
    open fun stompClient(): WebSocketStompClient {
        val stompClient = WebSocketStompClient(StandardWebSocketClient())
        stompClient.messageConverter = MappingJackson2MessageConverter()
        stompClient.taskScheduler = ConcurrentTaskScheduler()
        return stompClient
    }

}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}