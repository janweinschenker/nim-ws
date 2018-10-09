package com.example.demo.common.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
open class TurnNotification(@JsonProperty("id") var id: Long? = -1,
                            @JsonProperty("game_id") var gameId: Long? = -1,
                            @JsonProperty("remaining_items") var remainingItems: Int? = -1,
                            @JsonProperty("finished") var finished: Boolean = false,
                            @JsonProperty("next_player") var nextPlayer: Players? = null,
                            @JsonProperty("winner") var winner: Players? = null)
