package com.example.demo.engine

import com.example.demo.common.model.Players
import com.example.demo.entity.GameEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class GameEntityEngineTest {

    val engine = GameEngine()

    @Test
    fun `test human player wins`() {
        // Given
        val gameEntity: GameEntity = createGame()
        // When
        val result = engine.play(gameEntity, 1, Players.HUMAN)
        // Then
        assertThat(result.first.finished).isTrue()
        assertThat(result.first.winner).isEqualTo(Players.HUMAN)
    }

    @Test
    fun `test human played a turn`() {
        // Given
        val gameEntity: GameEntity = createGame(8)
        // When
        val result = engine.play(gameEntity, 2, Players.HUMAN)
        // Then
        assertThat(result.first.remainingItems).isEqualTo(6)
        assertThat(gameEntity.nextPlayer).isEqualTo(Players.COMPUTER)
        assertThat(gameEntity.winner).isNull()
    }

    @Test
    fun `test human player lost`() {
        // Given
        val gameEntity: GameEntity = createGame(3)
        val resultHuman = engine.play(gameEntity, 1, Players.HUMAN)
        assertThat(resultHuman.first.finished).isFalse()
        // When
        val finalResult = engine.play(gameEntity, 1, Players.COMPUTER)
        // Then
        assertThat(finalResult.first.finished).isTrue()
        assertThat(finalResult.first.winner).isEqualTo(Players.COMPUTER)
    }

    @Test
    fun `test game is already finished`() {
        // Given
        val gameEntity: GameEntity = createGame()
        gameEntity.finished = true
        // When
        val result = engine.play(gameEntity, 1, Players.HUMAN)
        // Then
        assertThat(result.second.isValid()).isFalse()
    }

    private fun createGame(count: Int = 2): GameEntity = GameEntity(initialItems = count)
}