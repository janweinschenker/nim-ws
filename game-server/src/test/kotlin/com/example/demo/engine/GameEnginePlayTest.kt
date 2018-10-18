package com.example.demo.engine

import com.example.demo.common.model.Players
import com.example.demo.entity.GameEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameEnginePlayTest {

    private val gameEngine = GameEngine()

    @Test
    fun `should switch next player to computer`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.HUMAN)
        // when
        val result = gameEngine.play(game, 1, Players.HUMAN)
        // then
        assertThat(result.second.isValid()).isTrue()
        assertThat(result.first.nextPlayer).isEqualTo(Players.COMPUTER)
    }

    @Test
    fun `should switch next player to human`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.play(game, 1, Players.COMPUTER)
        // then
        assertThat(result.second.isValid()).isTrue()
        assertThat(result.first.nextPlayer).isEqualTo(Players.HUMAN)
    }

    @Test
    fun `should decrease remaining items`() {
        // given
        val game = GameEntity(initialItems = 13, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.play(game, 1, Players.COMPUTER)
        // then
        assertThat(result.second.isValid()).isTrue()
        assertThat(result.first.remainingItems).isEqualTo(12)
    }

    @Test
    fun `should finish game when taking next to last item`() {
        // given
        val game = GameEntity(initialItems = 2, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.play(game, 1, Players.COMPUTER)
        // then
        assertThat(result.second.isValid()).isTrue()
        assertThat(result.first.finished).isTrue()
        assertThat(result.first.winner).isEqualTo(Players.COMPUTER)
    }

    @Test
    fun `should finish game when taking last item`() {
        // given
        val game = GameEntity(initialItems = 2, nextPlayer = Players.COMPUTER)
        // when
        val result = gameEngine.play(game, 2, Players.COMPUTER)
        // then
        assertThat(result.second.isValid()).isTrue()
        assertThat(result.first.finished).isTrue()
        assertThat(result.first.winner).isEqualTo(Players.HUMAN)
    }

}