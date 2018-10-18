package com.example.demo.engine

import com.example.demo.common.model.Players
import com.example.demo.common.model.otherPlayer
import com.example.demo.entity.GameEntity
import com.google.common.annotations.VisibleForTesting
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameEngine {

    companion object : KLogging()

    fun play(game: GameEntity, numberOfItems: Int, player: Players): Pair<GameEntity, ValidationResult> {

        val validationResult = validateTurn(game, numberOfItems, player)

        // Early exit at invalid game
        if (!validationResult.isValid()) {
            logger.debug { "Invalid turn for game ${game.id}: ${validationResult.violations.joinToString()}" }
            return Pair(game, validationResult)
        }

        // Actually make the turn
        game.remainingItems -= numberOfItems
        game.nextPlayer = otherPlayer(player)

        // Compute winner if applicable
        val winner: Optional<Players> = when (game.remainingItems) {
            0 -> Optional.of(otherPlayer(player))
            1 -> Optional.of(player)
            else -> Optional.empty()
        }

        // Updating game state
        if (winner.isPresent) {
            game.winner = winner.get()
            game.finished = true
            logger.debug { "Player $player won the game ${game.id}" }
        }

        return Pair(game, validationResult)
    }

    @VisibleForTesting
    internal fun validateTurn(game: GameEntity, itemsToBeTaken: Int, playerMakingAMove: Players): ValidationResult {
        val validationResult = ValidationResult()

        if (game.finished) validationResult.violations.add("Game finished. No more turns allowed.")
        if (game.nextPlayer != playerMakingAMove) validationResult.violations.add("Next player is ${game.nextPlayer}.")
        if (itemsToBeTaken > game.remainingItems) validationResult.violations.add("Unable to take $itemsToBeTaken when only ${game.remainingItems} remain.")
        if (itemsToBeTaken < 1) validationResult.violations.add("You should at least take 1 item.")
        if (itemsToBeTaken > 3) validationResult.violations.add("You should not take more than 3 items.")

        return validationResult
    }

}

data class ValidationResult(
        val violations: MutableSet<String> = mutableSetOf()
) {

    fun isValid(): Boolean = violations.isEmpty()

}