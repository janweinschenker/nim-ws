# Aufgabe 4

In dieser aufgabe wird es das Ziel sein, drei weitere technische Neuerungen zum Spielserver hinzuzufügen:

1. Für die interne Ablaufsteuerung der Servers soll ein Eventbus eingefügt werden.
1. Um Statusänderungen von Spielen an zukünftige Client-Anwendungen zu propagieren, soll ein Websocket implementiert werden.
1. Für das Konvertieren von Entity-Klassen in DTOs soll die MapStruct-Library eingesetzt werden.

## Eventbus

Wir verwenden den Eventbus von _Project reactor_. Dieser wird folgendermaßen in unsere
Anwendung integriert:

### Spring Configuration

1. Wir legen das Package `com.example.demo.config` an.
1. Darin legen wir eine offene Configuration-Klasse, z.B. `EventbusConfig`. Diese Klasse annotieren wir mit
`org.springframework.context.annotation.Configuration`.
1. Weiterhin erzeugen wir in dieser Klasse zwei Spring-Beans, die vom Eventbus benötigt werden:
    ```kotlin
    @Bean
    open fun env(): Environment = Environment.initializeIfEmpty().assignErrorJournal()

    @Bean
    open fun createEventBus(env: Environment): EventBus = EventBus.create(env, Environment.THREAD_POOL)
    ```

### TurnNotificationHandler
Die Ablaufsteuerung soll auf dem Auslösen, Versenden und Verarbeiten von Ereignissen basieren. 

Ereignisse sind Instanzen der Klasse `TurnNotification`

Für das Verarbeiten von Ereignissen erstellen wir eine Spring-Service-Klasse `TurnNotificationHandler`,
die nur eine einzige Funktion implementieren muss. Diese könnte Zum Beispiel `handleNotification()` heißen und
benötigt genau einen Parameter:

- `notification` vom Typ `TurnNotification`

Eine Instanz vom Typ TurnNotification enthält die ID eines Spiels. Ein Aufruf der Funktion `handleNotification()`
wird zukünftig durch eine Änderung des Spielzustands ausgelöst. Wir wissen innerhalb dieser Funktion also,
dass es für eine bestimmte Spiel-ID eine Änderung gegeben hat.

Bis auf weiteres beschränken wir uns an dieser Stelle darauf, den Inhalt der `TurnNotification` als Log-Ausgabe 
auf der Konsole auszugeben.

### TurnNotificationConsumer

Der `TurnNotificationHandler` wird nicht direkt durch den Eventbus aufgerufen. Damit das passiert bauen wir nun 
eine weitere eine Spring-Service-Klasse mit dem Namen `TurnNotificationConsumer`. Diese muss von der Klasse
`reactor.fn.Consumer` erben, genauer gesagt von `reactor.fn.Consumer<Event<TurnNotification>>`.

Über die Dependency-Injection von Spring lassen wir uns die Instanz des `TurnNotificationHandler`injizieren. 

Wir sind gezwungen, die Funktion `fun accept(event: Event<TurnNotification>?)` zu implementieren.

Aus dem Parameter `event` erhalten wir über Zugriff auf das Feld `event.data` eine Instanz der
Klasse `TurnNotification`. Achtung: kann `null` sein &mdash; es bietet sich an, einen `null`-sicheren Zugriff
im Kotlin-Stil zu verwenden.

Diese Instanz der `TurnNotification` kann nun an den injizierten `TurnNotificationHandler`übergeben werden.

### AppStartupRunner
Zuletzt müssen wir dafür sorgen, dass der Eventbus die Ereignisse an den `TurnNotificationConsumer` weiterleitet.

Diese Konfiguration erfolgt einmal zur Startzeit unserer Anwendung. Dazu erstellen wir eine weitere Spring-Service-Klasse,
die diesemal von `org.springframework.boot.ApplicationRunner` erben muss.

Weiterhin injizieren wir eine Instanz von `reactor.bus.EventBus` und eine des `TurnNotificationConsumer`.

Wir implementieren die Funktion `run(args: ApplicationArguments?)` und fügen dort eine Zeile ein,
die den Eventbus anweist, bestimmte Events an unseren Consumer weiterzuleiten.

```kotlin
eventBus.on(Selectors.regex("newTurn \\d+"), notificationConsumer)
```

In unserem Fall zeichnen sich Ereignisse aus, dass sie zu einem bestimmten Thema (_Topic_) gehören. Ein Topic wird über
einen einfachen String oder einen regulären Ausdruck identifiziert.

Hier sorgen wir dafür, dass ein Ereignis mit dem Topic `"newTurn {gameId}"` zur weiteren Bearbeitung an unseren
`TurnNotificationConsumer` weitergegeben wird.

### Erweiterung des GameRestController
Im `GameRestController` injizieren wir nun eine Instanz von `reactor.bus.EventBus`.

In der Funktion `play()` gibt es eine Stelle, an der wir einen Spielzug als gültig validiert haben.

Dort lösen wir nun ein neues Ereignis aus:

```kotlin
val turnNotification = toTurnNotification(gameEntity)
eventBus.notify("newTurn ${savedGame.id}", Event.wrap(turnNotification))
```  

