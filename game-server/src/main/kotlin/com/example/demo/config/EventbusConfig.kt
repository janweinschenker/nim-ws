package com.example.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.Environment
import reactor.bus.EventBus

/**
 * Create environment and eventbus beans. This must happen before AppStartupRunner is initialized.
 */
@Configuration
open class EventbusConfig {
    @Bean
    open fun env(): Environment = Environment.initializeIfEmpty().assignErrorJournal()

    @Bean
    open fun createEventBus(env: Environment): EventBus = EventBus.create(env, Environment.THREAD_POOL)
}