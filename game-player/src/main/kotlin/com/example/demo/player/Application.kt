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
open class Application


fun main(args: Array<String>) {
    runApplication<Application>(*args)
