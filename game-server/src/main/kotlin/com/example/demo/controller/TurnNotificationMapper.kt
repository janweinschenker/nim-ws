package com.example.demo.controller

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