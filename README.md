# Aufgabe 2

In dieser Aufgabe wird es darum gehen, folgende Komponenten zu bauen:

- die Game-Engine, also die fachliche Implementierung der Spielregeln
- einen RESTful Controller, der Funktionen der Engine über ein RESTful-Interface verefügbar macht
- Persistenz
   - Eine Entity-Klasse, um Spiele via JPA zu speichern
   - Eine Repository-Klasse, die CRUD-Operationen ermöglicht
   
## GameEntity

In der Datei `GameEntity.kt` soll eine Klasse `GameEntity` angelegt werden.

Die Klasse soll eine JPA-Entity sein und über folgende Properties verfügen:

- `id` vom Typ `Long`  
- `initialItems` vom Typ `Int`  
- `remainingItems` vom Typ `Int`  
- `finished` vom Typ `Boolean`  
- `nextPlayer` vom Typ `Players`  
- `winner` vom Typ `Players` (nullable)

Die Klasse soll einen Konstruktor bekommen, in dem ein Wert für die Property `initialItems` gesetzt werden
kann.

## GameRepository

In der Datei `GameRepository.kt` soll ein Interface `GameRepository` angelegt werden.

Dieses Interface soll ein JPA-Repository für die Entity `GameEntity` sein.

## GameEngine

In der Datei `GameEngine.kt` soll eine Klasse `GameEngine` als Spring-Service angelegt werden.

In der Datei `GameEngine.kt` soll weiterhin eine Datenklasse `ValidationResult` angelegt werden.

`ValidationResult` soll eine Funktion `isValid()` anbieten, deren Rückgabewert den Typ Boolean hat.

Diese Klasse `GameEngine` soll genau eine Funktion `play()` implementieren. Deren Parameter sollen sein:

- `game` vom Typ `GameEntity`
   - dieser Parameter wird den Zustand eines Spiels vor dem aktuellen Spielzug enthalten.
- `numberOfItems` vom Typ `Int`  
   - dieser Parameter wird angeben, wieviele Hölzer im aktuellen Spielzug antnommen werden
- `player` vom Typ `Players`  
   - dieser Parameter wird angeben, welcher Spieler gerade spielt.
   
Der Rückgabewert von `pair()` ist `Pair<GameEntity, ValidationResult>`

### Constraints

- `play()` muss dafür sorgen, dass 
  - die Property `gameEntity.remainingItems` um den Wert `numberOfItems` subtrahiert wird.
  - die Property `game.nextPlayer` beim Verlassen der Funktion den Wert des nächsten Spielers hat. Dieser Wert muss 
  ungleich dem Wert `game.nextPlayer` zum Aufrufzeitpunkt der Methode sein.
  - die Property `game.finished` auf `true` gesetzt wird, sofern die Substraktion von
  `gameEntity.remainingItems` um den Wert `numberOfItems` zum Ergebnis `1` geführt hat. In diesem Fall muss auch
  die Property `gameEntity.winner` korrekt gesetzt werden.

## GameRestController

In der Datei `GameRestController.kt` soll eine Klasse angelegt werden, die vom 
Interface `org.example.demo.rest.api.GameApi` erbt und alle davon abgeleiteten
Methoden implementieren

Die Klasse soll ein Spring Rest-Controller sein.

Über Dependency-Injection solle die Klassen `GameRepository` und `GameEngine` injziert werden.