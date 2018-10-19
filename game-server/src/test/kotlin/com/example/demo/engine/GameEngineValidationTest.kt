package com.example.demo.engine

import com.example.demo.entity.Players
import com.example.demo.entity.GameEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameEngineValidationTest {

    private val gameEngine = GameEngine()

    @Test
    fun `human should be able to make turn`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.HUMAN)
        // when
        val result = gameEngine.validateTurn(game, 1, Players.HUMAN)
        // then
        assertThat(result.isValid()).isTrue()
    }

    @Test
    fun `computer should be able to make turn`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.validateTurn(game, 1, Players.COMPUTER)
        // then
        assertThat(result.isValid()).isTrue()
    }

    @Test
    fun `validation should fail if player takes more items than remaining`() {
        // given
        val game = GameEntity(initialItems = 2, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.validateTurn(game, 3, Players.COMPUTER)
        // then
        assertThat(result.isValid()).isFalse()
        assertThat(result.violations.size).isEqualTo(1)
        assertThat(result.violations.first()).contains("Unable to take")
    }

    @Test
    fun `validation should fail if player takes not enough items`() {
        // given
        val game = GameEntity(initialItems = 2, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.validateTurn(game, 0, Players.COMPUTER)
        // then
        assertThat(result.isValid()).isFalse()
        assertThat(result.violations.size).isEqualTo(1)
        assertThat(result.violations.first()).contains("You should at least take")
    }

    @Test
    fun `validation should fail if player takes too many items`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.validateTurn(game, 4, Players.COMPUTER)
        // then
        assertThat(result.isValid()).isFalse()
        assertThat(result.violations.size).isEqualTo(1)
        assertThat(result.violations.first()).contains("You should not take more than")
    }

    @Test
    fun `validation should fail if wrong player makes turn`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.HUMAN)
        // when
        val result = gameEngine.validateTurn(game, 1, Players.COMPUTER)
        // then
        assertThat(result.isValid()).isFalse()
        assertThat(result.violations.size).isEqualTo(1)
        assertThat(result.violations.first()).contains("Next player is")
    }

    @Test
    fun `validation should fail if game is finished`() {
        // given
        val game = GameEntity(
                initialItems = 13,
                nextPlayer = Players.HUMAN,
                winner = Players.HUMAN,
                finished = true)
        // when
        val result = gameEngine.validateTurn(game, 1, Players.HUMAN)
        // then
        assertThat(result.isValid()).isFalse()
        assertThat(result.violations.size).isEqualTo(1)
        assertThat(result.violations.first()).contains("Game finished")
    }
}