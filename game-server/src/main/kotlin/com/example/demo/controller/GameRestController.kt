package com.example.demo.controller

import com.example.demo.common.model.Players
import com.example.demo.engine.GameEngine
import com.example.demo.entity.GameEntity
import com.example.demo.repo.GameRepository
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
                        this.nextPlayer = GameDto.NextPlayerEnum.valueOf(it.nextPlayer.name)
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
    override fun createGame(@Valid @RequestBody newGame: NewGameDto): ResponseEntity<Void> {

        // Create entity
        val game = GameEntity(
                initialItems = newGame.initialItems,
                remainingItems = newGame.initialItems
        )

        // Save game
        val savedGame = gameRepository.save(game)

        // Create uri to game and return it
        val relativeGameUri = URI("/game/${savedGame.id}")
        return ResponseEntity
                .created(relativeGameUri)
                .build()
    }

    /**
     * PATCH mapping for playing the game.
     *
     * Example: PATCH /games/0?player_takes=1
     *
     * @param gameId the id of the game in which the playerType participates
     * @param playerTakes the number of tokens the playerType take in their move
     * @param playerType type of playerType. Value must be one of Players#values()
     *
     * @return a string documenting the state of the game.
     */

    override fun play(@PathVariable("id") id: Long, @Valid @RequestBody turn: TurnDto): ResponseEntity<GameDto> {
        // Find game or throw Exception
        val game = gameRepository.findById(id).orElseThrow { GameNotFoundException() }

        // Make move
        val (playedGame, validation) = gameEngine.play(game, turn.takenItems, Players.HUMAN)

        if (validation.isValid()) {
            val savedGame = gameRepository.save(playedGame)
            val gameDto = GameDto().apply {
                this.id = savedGame.id
                this.initialItems = savedGame.initialItems
                this.remainingItems = savedGame.remainingItems
                this.nextPlayer = GameDto.NextPlayerEnum.fromValue(savedGame.nextPlayer.name)
                this.setFinished(savedGame.finished)
                this.winner(GameDto.WinnerEnum.fromValue(savedGame.winner?.name ?: "doesNotExist"))
            }
            return ResponseEntity.ok(gameDto)
        } else {
            throw IllegalMoveException(validation.violations.joinToString(separator = "\n"))
        }
    }

}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game does not exist.")
class GameNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This move is not valid.")
class IllegalMoveException(override val message: String?) : RuntimeException(message)

