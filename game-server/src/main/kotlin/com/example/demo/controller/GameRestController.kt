package com.example.demo.controller

import com.example.demo.common.model.Players
import com.example.demo.engine.GameEngine
import com.example.demo.entity.GameEntity
import com.example.demo.repo.GameRepository
import mu.KLogging
import org.example.demo.rest.api.GameApi
import org.example.demo.rest.model.GameDto
import org.example.demo.rest.model.NewGameDto
import org.example.demo.rest.model.PlayerDto
import org.example.demo.rest.model.TurnDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid


/**
 * Our game controller
 */
@RestController
class GameRestController(
        private val gameRepository: GameRepository,
        private val gameEngine: GameEngine) : GameApi {

    companion object : KLogging()


    /**
     * Returns a list of all created games.
     */
    override fun getGames(): ResponseEntity<List<GameDto>> {
        // Find all games and map them to dtos
        val games = gameRepository
                .findAll()
                .map {
                    GameDto().apply {
                        this.id = it.id
                        this.initialItems = it.initialItems
                        this.remainingItems = it.remainingItems
                        this.nextPlayer = PlayerDto.fromValue(it.nextPlayer.name)
                        this.setFinished(it.finished)
                    }
                }
        return ResponseEntity.ok(games)
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

        // Create entity
        val game = GameEntity(
                initialItems = newGame.initialItems,
                remainingItems = newGame.initialItems
        )

        // Save game
        val savedGame = gameRepository.save(game)

        // Create uri to game and return it
        val headers = HttpHeaders().apply {
            location = URI("/game/${savedGame.id}")
        }

        return ResponseEntity(savedGame.id, headers, HttpStatus.CREATED)
    }

    override fun play(@PathVariable("id") id: String, @Valid @RequestBody turn: TurnDto): ResponseEntity<GameDto> {
        // Find game or throw Exception
        val game = gameRepository.findById(id).orElseThrow { GameNotFoundException() }

        // Make move
        val (playedGame, validation) = gameEngine.play(game, turn.takenItems, Players.valueOf(turn.player.name))

        if (validation.isValid()) {
            val savedGame = gameRepository.save(playedGame).apply { makeComputerMove(this) }
            val gameDto = GameDto().apply {
                this.id = savedGame.id
                this.initialItems = savedGame.initialItems
                this.remainingItems = savedGame.remainingItems
                this.nextPlayer = PlayerDto.fromValue(savedGame.nextPlayer.name)
                this.setFinished(savedGame.finished)
                this.winner(PlayerDto.fromValue(savedGame.winner?.name ?: "doesNotExist"))
            }
            return ResponseEntity.ok(gameDto)
        } else {
            throw IllegalMoveException(validation.violations.joinToString(separator = "\n"))
        }
    }

    private fun makeComputerMove(game: GameEntity): GameEntity {
        if (game.finished) return game
        val result = this.gameEngine.play(game, 1, Players.COMPUTER)
        return gameRepository.save(result.first)
    }

}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game does not exist.")
class GameNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class IllegalMoveException(override val message: String?) : RuntimeException(message)

