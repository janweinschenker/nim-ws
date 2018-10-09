package com.example.demo

import com.example.demo.notification.TurnNotificationConsumer
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service
import reactor.bus.EventBus
import reactor.bus.selector.Selectors

/**
 * Run on application startup and configure our notificationConsumer to handle events of type newTurn.
 */
@Service
class AppStartupRunner(val eventBus: EventBus, val notificationConsumer: TurnNotificationConsumer) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        eventBus.on(Selectors.regex("newTurn \\d+"), notificationConsumer)
    }

}