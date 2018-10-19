# Aufgabe 2

In dieser Aufgabe wird es darum gehen, folgende Komponenten zu bauen:

- die Game-Engine, also die fachliche Implementierung der Spielregeln
- einen RESTful Controller, der Funktionen der Engine über ein RESTful-Interface verefügbar macht
- Persistenz
   - Eine Entity-Klasse, um Spiele via JPA zu speichern
   - Eine Repository-Klasse, die CRUD-Operationen ermöglicht

## Kotlin Doku
[https://kotlinlang.org/docs/reference/basic-syntax.html](https://kotlinlang.org/docs/reference/basic-syntax.html)

## Vorbereitung

Führe im root `mvn clean install -Dmaven.test.skip` aus. 

## GameEntity

Öffne die Datei `GameEntity.kt`.

Die Klasse soll eine JPA-Entity sein und über folgende Properties verfügen:

- `id` vom Typ `String`  
- `initialItems` vom Typ `Int`  
- `remainingItems` vom Typ `Int`  
- `finished` vom Typ `Boolean`  
- `nextPlayer` vom Typ `Players`  
- `winner` vom Typ `Players`-Enum (nullable)

Die Klasse soll einen Konstruktor bekommen, in dem ein Wert für die Property `initialItems` gesetzt werden kann. Ergänze die Properties und überlege, ob der Konstruktorparameter jeweils ein `val` oder `var` sein kann.

## GameRepository

In der Datei `GameRepository.kt` soll ein Interface `GameRepository` angelegt werden.

Dieses Interface soll ein JPA-Repository für die Entity `GameEntity` sein.

## GameEngine

In der Datei `GameEngine.kt` soll eine Klasse `GameEngine` als Spring-Service angelegt werden.

### Datenklasse ValidationResult

In der Datei `GameEngine.kt` ist eine `data class` `ValidationResult` vorhanden, die Du erweitern musst.

`ValidationResult` soll eine Funktion `isValid()` anbieten, deren Rückgabewert den Typ Boolean hat.

Die Klasse soll ein Set (`MutableSet` von `String`) enthalten. In diesem Set sollen Fehlermeldungen als Strings
gespeichert werden, die nach einer Validierung eines Spielzugs anfallen.

`isValid()` soll den Wert `true` zurückgeben, wenn das oben genannte Set leer ist.

### Validierung von Spielzügen

Als nächstes musst Du die `validateTurn()` Funktion implementieren, die das `ValidationResult` befüllt.

Ein Spielzug ist nicht valide wenn:

* er auf einem Spiel ausgeführt werden soll, das bereits beendet ist
* ein Spieler einen Spielzug macht, der nicht an der Reihe ist
* durch einen Spielzug mehr Hölzer genommen werden sollen, als verfügbar sind
* ein Spieler weniger als ein Hölzchen nimmt
* ein Spieler mehr als drei Hölzchen nimmt

Für jede fehlgeschlagene Validierung soll eine Fehlermeldung in das Set in `ValidationResult`.

Führe den `GameEngineValidationTest` aus, um zu prüfen, ob Deine Implementierung der Spezifikation entspricht.


### Ausführen von Spielzügen

Diese Klasse `GameEngine` soll eine Funktion `play()` implementieren. Deren Parameter sollen sein:

- `game` vom Typ `GameEntity`
   - dieser Parameter wird den Zustand eines Spiels vor dem aktuellen Spielzug enthalten.
- `numberOfItems` vom Typ `Int`  
   - dieser Parameter wird angeben, wieviele Hölzer im aktuellen Spielzug antnommen werden
- `player` vom Typ `Players`  
   - dieser Parameter wird angeben, welcher Spieler gerade spielt.

Die Funktion sollte als erstes prüfen, ob der Spielzug valide ist. Danach muss der Spielzug ausgeführt werden. 

Das Ergebnis ist ein `Pair<GameEntity, ValidationResult>`, welches die Funktion zurückliefert.

### Constraints

- `play()` muss dafür sorgen, dass
  - die Anzahl der Hölzchen im Spiel um die Anzahl der gezogenen Hölzchen vermindert wird.
  - der `nextPlayer` im `game` geändert wird (wenn nötig).
  - die Property `game.finished` auf `true` gesetzt wird, sofern die Substraktion von
  `gameEntity.remainingItems` um den Wert `numberOfItems` zum Ergebnis `1` oder `0` geführt hat. In diesem Fall muss auch
  die Property `gameEntity.winner` korrekt gesetzt werden.

## GameRestController

Der `GameRestController`, der von der generierten `GameApi` erbt, soll implementiert werden.

Die Klasse soll ein Spring Rest-Controller sein.

Über Dependency-Injection sollen die Klassen `GameRepository` und `GameEngine` injziert werden.

Es müssen die drei Funktionen des Interfaces implementiert werden.

### getGames()

Diese Funktion soll alle vorhandenen Entitäten vom Typ `GameEntity` aus der Datenbank abfragen. Danach sollen
sie auf den Typ `GameDto` konvertiert werden und als `ResponseEntity<List<GameDto>>` zurückgegeben werden.

#### Konvertieren mit .apply()

```kotlin
 gameRepository // gibt GameEntity zurück in der Variable "it"
                .findAll()
                .map {
                    GameDto().apply { // Zugriff auf das neue GameDto mit "this"
                        this.id = it.id
                        this.initialItems = it.initialItems
                        // ..
                    }
                }
```

#### Konvertieren mit Extension Function

Wir werden die Umwandlung von `GameEntity` in `GameDTO` noch öfter benötigen. Lagere die Umwandlung in eine Extension Function aus. Die Funktion kann im Controller auf package level definiert werden.

#### Swagger UI

Wenn Du die Funktion implementiert hast, kannst Du die Schnittstelle über die Swagger-UI ausprobiern. 

* `cd game-server && mvn spring-boot:run`
* Öffne [http://localhost:8080/swagger-ui.html]()


### createGame()

Diese Funktion soll ein neues Spiel anlegen. Aus dem Funktionsparameter vom Typ `NewGameDto` soll eine
Instanz von `GameEntity` erzeugt und persistiert werden.

Als Rückgabewert soll die Game ID zurückgeliefert werden. Außerdem soll im `Location` Header die relative URL auf das Spiel gesetzt werden. Beispiel:

```kotlin
        // Create uri to game and return it
        val headers = HttpHeaders().apply {
            location = URI("/game/${savedGame.id}")
        }

return ResponseEntity(savedGame.id, headers, HttpStatus.CREATED)
```


### play()

Diese Funktion führt einen Spielzug durch. Als Parameter werden die Spiel-ID und
die Daten des durchzuführenden Spielzugs übergeben.

Zunächst muss anhand der Spiel-ID das existierende Spiel als `GameEntity` aus der Datenbank geladen werden.

Danach wird die `GameEntity` zusammen mit der Anzahl der genommenen Hölzer und dem Spielertyp an die
Funktion `gameEngine.play()` übergeben.

Bei einem validen Spielzug soll der neue Spielzustand persistiert und als `GameDto` zurück gegeben werden.

Bei einem nicht validen Spielzug soll sich der Spielzustand nicht ändern und stattdessen eine HTTP-Response
mit Fehlerstatuscode 400 (Bad Request) zurückgegeben werden.

Bei einem Spielzug für ein nicht existierendes Spiel (Spiel-ID in DB unbekannt) soll eine HTTP-Response
mit Fehlerstatuscode 404 (Not Found) zurückgegeben werden.


#### Werfen von Exceptions, um in REST-Controllern HTTP-Fehlercodes zurückzugeben

Du findest im `GameRestController` bereits zwei Exceptions, die mit Annotationen versehen sind. Wird eine solche Exception in diesem Controller geworfen, wird sie von Spring gefangen und Spring generiert eine entsprechende Antwort an den Client. 

```kotlin
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game does not exist.")
class GameNotFoundException : RuntimeException()
```

Wird die `GameNotFoundException` in einer Funktion geworfen, so liefert Spring einen Http Status 400 zurück.
