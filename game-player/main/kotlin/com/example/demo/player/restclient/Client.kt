package com.example.demo.player.restclient

import com.example.demo.common.model.Game
import com.example.demo.common.model.Players
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service


/**
 * This service offers a couple of convenience methods to submit RESTful requests.
 */
@Service
class Client(templateBuilder: RestTemplateBuilder,
             @Value("\${nimdojo.config.gameServer:}") val gameServer: String,
             @Value("\${nimdojo.config.player:}") val playerName: String) {

    private val restTemplate = templateBuilder
            .requestFactory(HttpComponentsClientHttpRequestFactory::class.java)
            .rootUri(gameServer)
            .build()!!

    fun getGames(uri: String): Array<Game>? {
        fun asyncGetForObject() = async(CommonPool) {
            restTemplate.getForObject(uri, Array<Game>::class.java)
        }
        return runBlocking { asyncGetForObject().await() }
    }

    fun postGame(uri: String): List<String?> {
        fun asyncPostForObject() = async(CommonPool) {
            restTemplate.postForObject(uri, null, String::class.java)
        }

        val strings = listOf(asyncPostForObject())
        return runBlocking { strings.map { it.await() } }
    }

    fun patchGame(gameId: Long, playerTakes: Int): List<String?> {
        fun asyncPatchForObject() = async(CommonPool) {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON_UTF8;
            headers.add("X-Player", Players.valueOf(playerName).name)
            val entity = HttpEntity("parameters", headers)
            val exchange = restTemplate.exchange("/games/${gameId}/?player_takes=$playerTakes", HttpMethod.PATCH, entity, String::class.java)
            exchange.body
        }

        val strings = listOf(asyncPatchForObject())
        return runBlocking { strings.map { it.await() } }
    }


}