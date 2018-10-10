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

Ein Spielzug sollte nun dazu führen, dass im `TurnNotificationHandler` die Methode `handleNotification()` aufgerufen wird. 

## Websocket

### Klasse WebSocketConfig

Über ein Websocket propagiert der Spielserver Statusinformationen. Für die Bereitstellung des Websockets
benötigen wir eine offene Configuration-Klasse, z.B. mit dem Namen `WebSocketConfig`. Diese Klasse mit von 
`org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer` und mit folgenden
Annotationen versehen werden:

- `org.springframework.context.annotation.Configuration`
- `org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker`

Wir müssen folgende zwei Methoden überschreiben:

```kotlin
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/turns", "/messages")
    }
```

Damit richten wir zwei Websocket-Topics ein:
- `/topic/turns`
  - Über dieses Topic werden wir Instanzen der Klasse `TurnNotification` versenden.
- `/topic/messages`
  - Über dieses Topic werden wir textuelle Nachrichten vom Typ `String` versenden.
  
  
### Klasse TurnNotificationHandler

In die Klasse `TurnNotificationHandler` injizieren wir eine Instanz von `org.springframework.messaging.simp.SimpMessagingTemplate`.

Diese Instanz können wir dazu nutzen, Daten über den Websocket zu versenden:

- Über das Message-Topic:
  ```kotlin
  simpMessagingTemplate.convertAndSend("/topic/messages/${gameEntity.id}", com.example.demo.common.model.Message(text))
  ```
- Über das Turns-Topic:
  ```kotlin
  val notification: TurnNotification = ... ; 
  simpMessagingTemplate.convertAndSend("/topic/turns/${gameEntity.id}", notification))
  ```
  
Weiterhin ließe sich in die Klasse `TurnNotificationHandler` eine Instanz der Klasse `GameRepository` injizieren, um weitere
Spieldaten aus der Datenbank zu beziehen.

## MapStruct

Wir beenden Aufgabe 2 mit der Einführung eines Tools zur Konvertierung von Value-Objects.

In diesem Projekt hantieren wir mit Instanzen der Klasse `GameEntity` und unterschiedlichen, davon
abgeleiteten Klassen.

Mit MapStruct ist es möglich Mappings von der Klasse `GameEntity` beispielsweise nach `TurnNotification` zu definieren.

Dazu fügen wir folgende Klasse in das Server-Projekt ein.

```kotlin
import com.example.demo.common.model.TurnNotification
import com.example.demo.entity.GameEntity
import mu.KLogging
import org.mapstruct.*
import javax.annotation.PostConstruct

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
abstract class TurnNotificationMapper {

    companion object : KLogging()

    @Mappings(
            Mapping(source = "id", target = "id"),
            Mapping(source = "id", target = "gameId"),
            Mapping(source = "remainingItems", target = "remainingItems"),
            Mapping(source = "finished", target = "finished"),
            Mapping(source = "nextPlayer", target = "nextPlayer"),
            Mapping(source = "winner", target = "winner")
    )
    abstract fun gameEntityToTurnNotification(gameEntity: GameEntity): TurnNotification

    @InheritInverseConfiguration
    abstract fun turnNotificationToGameEntity(turnNotification: TurnNotification): GameEntity

    @PostConstruct
    fun startUp() {
        logger.info { "TurnNotificationMapper started ..." }
    }
}
```

Diese Klasse `TurnNotificationMapper` lässt sich nun über Dependency-Injection in alle anderen zum Spring-Kontext
gehörenden Klassen nutzen.

Beispielsweise im `GameRestController`, beim Auslösen eines Spielzug-Ereignisses 

  ```kotlin
  val notification: TurnNotification = turnNotificationMapper.gameEntityToTurnNotification(gameEntity) ; 
  simpMessagingTemplate.convertAndSend("/topic/turns/${gameEntity.id}", notification))
  ```


