package de.holisticon.course.kotlin


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.lang.System.exit

/**
 * Startup our Application.
 */
@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
    println("Hallo Welt")
    exit(0)
}
