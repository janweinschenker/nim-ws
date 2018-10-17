# Kotlin Cheat Sheet

## Klassen

Klassen sind per Default `final` und `public`.

```kotlin
abstract class Car(val name: String)

class Seat(val model: String, val color: String) : Car("Seat")

val focus = Seat("Leon", "Blue")

println(focus.name()         -> prints "Seat"
println(focus.model()        -> prints "Leon"
println(focus.color()        -> prints "Blue"

val alhambra = Seat(color = "Silver", model = "Alhambra")
```

## if, when, for, while

### `if` Anweisung

```kotlin
// Traditional usage 
var max = a 
if (a < b) max = b

// With else 
var max: Int
if (a > b) {
    max = a
} else {
    max = b
}
 
// As expression 
val max = if (a > b) a else b
```

Die letzte Anweisung in einem Block ist der Rückgabewert.

```kotlin
val max = if (a > b) {
    print("Choose a")
    a
} else {
    print("Choose b")
    b
}
```


### `when` Anweisung

`when` ist Kotlins Variante des `switch`-Statements.

```kotlin
when (x) {
    in 1..10 -> print("x is in the range")
    in validNumbers -> print("x is valid")
    !in 10..20 -> print("x is outside the range")
    else -> print("none of the above")
}
```

`when` lässt sich sehr gut mit Single-Line-Expressions kombinieren. 

```kotlin
fun hasPrefix(x: Any) = when(x) {
    is String -> x.startsWith("prefix")
    else -> false
}
```





