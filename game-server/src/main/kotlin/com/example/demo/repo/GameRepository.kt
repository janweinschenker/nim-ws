package com.example.demo.repo

import com.example.demo.entity.GameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Simple repo for CRUD-operations on the GameEntity entity.
 */
@Repository
interface GameRepository : JpaRepository<GameEntity, Long>