# Aufgabe 1 &mdash; Hallo Welt

```kotlin
package com.example.demo

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
    println("Hallo Welt")
}
```

1. Erzeuge die obige Klasse im richtigen Package
2. Starte die App: `mvn spring-boot:run`
3. Du solltest "Hallo Welt" auf der Konsole sehen

   
Kotlin Doku: [https://kotlinlang.org/docs/reference/basic-syntax.html](https://kotlinlang.org/docs/reference/basic-syntax.html)