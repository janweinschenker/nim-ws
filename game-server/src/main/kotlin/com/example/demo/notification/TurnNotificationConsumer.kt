package com.example.demo.notification

import com.example.demo.common.model.TurnNotification
import mu.KLogging
import org.springframework.stereotype.Service
import reactor.bus.Event
import reactor.fn.Consumer

/**
 * Consume events of type Event<TurnNotification> and forward them to the TurnNotificationHandler.
 */
@Service
class TurnNotificationConsumer(val turnNotificationHandler: TurnNotificationHandler) : Consumer<Event<TurnNotification>> {

    companion object : KLogging()

    override fun accept(event: Event<TurnNotification>?) {
        event?.data?.let {
            try {
                turnNotificationHandler.handleNotification(it)
            } catch (e: InterruptedException) {
                logger.error { "InterruptedException occured: $e" }
            }
        }

    }

}