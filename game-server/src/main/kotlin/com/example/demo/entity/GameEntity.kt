package com.example.demo.entity

import java.util.*
import javax.persistence.Id

// FIXME Add the correct annotations to this class and make
data class GameEntity(
        @Id val id: String = Random().nextLong().toString()
)