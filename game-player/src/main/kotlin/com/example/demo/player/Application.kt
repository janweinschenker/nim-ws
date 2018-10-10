package com.example.demo.player

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class Application


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}