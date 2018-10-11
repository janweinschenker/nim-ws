# Aufgabe 5 - Computerspieler

In dieser Aufgabe wird es darum gehen, einen Client zu bauen, der eigenständig mit einer simplen AI gegen einen menschlichen 
Spieler spielen kann. Er wird dabei das RESTful-Interface des Servers nutzen.

Dieser Client wird sich zusätzlich auf den Websockets des Servers registrieren, um darüber Spiel-Updates zu empfangen. 


## Websocket Client

Wir implementieren einen Websocket-Client, der die empfangenen Nachrichten vom Server als Log-Message ausgibt.

Im Project `com.example.demo.player.websocket` legen wir die Klasse `MessageSessionHandler` an. Diese lassen wir von
`org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter` erben. Weiterhin definieren wir im Constructor
eine Variable vom Typ `Long`, die wir `gameId` nennen.

Nun müssen wir folgende vier Funktionen implementieren:

```kotlin
   companion object : KLogging()

   /**
     * called directly after the session has been established.
     */
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        session.subscribe("/topic/messages/$gameId", this)
    }

    /**
     * called in case an exception is thrown.
     */
    override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
        exception.printStackTrace()
    }

    /**
     * Get the type of the payload object.
     */
    override fun getPayloadType(headers: StompHeaders): Type {
        return Message::class.java
    }

    /**
     * handle a frame received by the server.
     */
    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        val message: Message = payload as Message
        logger.info { message.text }
    }
``` 

Damit der Websocket-Client funktioniert, muss er in der Klasse `com.example.demo.player.Application` noch 
entsprechend konfiguriert werden.

Dazu ist es notwendig, eine Instanz des `WebSocketStompClient` zu erzeugen. Dazu fügen wir der Application-Klasse eine
Funktion mit dem Typ `WebSocketStompClient` hinzu und annotieren sie zusätzlich als `@Bean`

```kotlin
   /**
     * Create a websocket client and register with the game server websocket.
     */
    @Bean
    open fun stompClient(): WebSocketStompClient {
        val stompClient = WebSocketStompClient(StandardWebSocketClient())
        stompClient.messageConverter = MappingJackson2MessageConverter()
        stompClient.taskScheduler = ConcurrentTaskScheduler()
        return stompClient
    }
```

Der Websocket-Client muss sich nun nur noch auf dem Spielserver registrieren. 

```kotlin
stompClient.connect(msgUrl, MessageSessionHandler(gameId.toLong()))
```

Tipp: Dies lässt sich z.B. mithilfe eines `org.springframework.boot.CommandLineRunner` erledigen,
der ähnlich wie der `WebSocketStompClient`, als `@Bean` in der Klasse `Application` instanziiert werden kann.

Innerhalb des `CommandLineRunner` erfolgt dann der Aufruf von `stompClient.connect()`.