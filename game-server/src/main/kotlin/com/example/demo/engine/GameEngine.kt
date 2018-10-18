package com.example.demo.engine

import com.example.demo.common.model.Players
import com.example.demo.entity.GameEntity
import com.google.common.annotations.VisibleForTesting
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class GameEngine {

    companion object : KLogging()

    @VisibleForTesting
    internal fun validateTurn(game: GameEntity, itemsToBeTaken: Int, playerMakingAMove: Players): ValidationResult {
        logger.info { "Validating turn in ${game.id} for $playerMakingAMove" }

        // FIXME Implement this function first.
        // FIXME Execute the tests in GameEngineValidationTest to check your code.

        return ValidationResult()
    }

    fun play(game: GameEntity, numberOfItems: Int, player: Players): Pair<GameEntity, ValidationResult> {
        logger.info { "$player wants to take $numberOfItems items in ${game.id}." }

        // FIXME After validating a turn, make the turn and set the game status accordingly.
        // FIXME Execute the tests in GameEnginePlayTest to check your code.

        return Pair(GameEntity(), ValidationResult())
    }

}

// FIXME ValidationResult needs to hold a suitable collection of violations
data class ValidationResult(val removeMe: Any? = null)