package com.example.demo.controller

import com.example.demo.engine.GameEngine
import mu.KLogging
import org.example.demo.rest.api.GameApi
import org.example.demo.rest.model.GameDto
import org.example.demo.rest.model.NewGameDto
import org.example.demo.rest.model.TurnDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


/**
 * Our game controller
 */
@RestController
class GameRestController(
        private val gameEngine: GameEngine) : GameApi {

    companion object : KLogging()


    /**
     * Returns a list of all created games.
     */
    override fun getGames(): ResponseEntity<List<GameDto>> {
        // FIXME
        return ResponseEntity.ok(emptyList())
    }


    /**
     * POST mapping for creating a new game.
     *
     * The 400 (Bad Request) status code indicates that the server cannot or
     * will not process the request due to something that is perceived to be
     * a client error (e.g., malformed request syntax, invalid request
     * message framing, or deceptive request routing).
     *
     * Returns the link to the newly created game.
     */
    override fun createGame(@Valid @RequestBody newGame: NewGameDto): ResponseEntity<String> {
        // FIXME
        return ResponseEntity("", null, HttpStatus.CREATED)
    }

    override fun play(@PathVariable("id") id: String, @Valid @RequestBody turn: TurnDto): ResponseEntity<GameDto> {
        // FIXME
        return ResponseEntity.ok(GameDto())
    }

}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game does not exist.")
class GameNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This move is not valid.")
class IllegalMoveException(override val message: String?) : RuntimeException(message)

