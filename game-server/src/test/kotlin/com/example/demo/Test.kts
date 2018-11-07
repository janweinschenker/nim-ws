

// Functions

fun print(): Unit = println()

fun print2() = println()

fun printMore() {
    println("more")
}

fun printWord(word: String) {
    println(word)
}

fun up(word: String): String {
    return word.toUpperCase()
}

// Name parameters (with default values)

fun play(amount: Double = 2.0, cheat: Boolean) = print()

play(3.0, false)

play(amount = 4.0, cheat = true)

play(cheat = true)

class Engine(
        private var horsepower: Int,
        private var turnedOn: Boolean = false
) {
    fun discharge(amount: Double = 2.0): Unit = println("Discharged")
}

val engine = Engine(horsepower = 23)
engine.discharge()
engine.discharge(5.0)


// Streams

listOf("kotlin", "java", "scala").forEach { println(it) }

setOf("kotlin", "java", "scala")
        .filter { it.startsWith("k") }
        .map { it.toUpperCase() }
        .forEach { println(it) }


// Klassen
class Scooter {

    private var brandName: String? = null

    fun start(): Unit = println("Starting")

}
