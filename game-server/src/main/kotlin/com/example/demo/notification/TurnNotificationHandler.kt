package com.example.demo.notification

import com.example.demo.common.model.Message
import com.example.demo.common.model.TurnNotification
import com.example.demo.common.model.otherPlayer
import com.example.demo.repo.GameRepository
import mu.KLogging
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/**
 * Handle notifications on the server's internal event bus. Dispatches received instances of
 * TurnNotification to the websocket.
 *
 * @see http://www.baeldung.com/spring-reactor
 */
@Service
class TurnNotificationHandler(val gameRepository: GameRepository,
                              val turnNotificationBroker: SimpMessagingTemplate) {

    companion object : KLogging()

    /**
     * Forward TurnNotification to the websocket.
     *
     * for mor info on let() and also()
     * @see https://medium.com/@elye.project/mastering-kotlin-standard-functions-run-with-let-also-and-apply-9cd334b0ef84
     */
    fun handleNotification(notification: TurnNotification) =
            gameRepository.findById(notification.gameId!!).also {
                val text: String = "${otherPlayer(notification.nextPlayer!!)} played in gameEntity ${it.get().id}. " +
                        "Now, ${it.get().remainingItems} tokens are left. " +
                        if (it.get().finished) "Game is finished. ${it.get().winner} won." else
                            "Next player is ${it.get().nextPlayer}."
                logger.info { text }
                turnNotificationBroker.convertAndSend("/topic/messages/${it.get().id}", Message(text))
            }.let {
                turnNotificationBroker.convertAndSend("/topic/turns/${it.get().id}", notification)
            }
}