package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.babybot.data.local.entity.BabyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(baby: BabyEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBaby(baby: BabyEntity)
    @Query("SELECT * FROM bebes WHERE idUsuario = :idUsuario LIMIT 1")
    suspend fun getBaby(idUsuario: String): BabyEntity?
}