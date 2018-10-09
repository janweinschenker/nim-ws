package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * Startup our Application.
 */
@EnableSwagger2
@SpringBootApplication
open class NimServer

fun main(args: Array<String>) {
    runApplication<NimServer>(*args)
}
