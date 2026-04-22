package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.babybot.data.local.entity.ActiveSessionEntity

@Dao
interface ActiveSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: ActiveSessionEntity)

    @Query("""
        SELECT * FROM sesiones_activas
        WHERE idBebe = :idBebe
        ORDER BY createdAt DESC
    """)
    suspend fun getByBaby(idBebe: String): List<ActiveSessionEntity>

    @Query("""
        SELECT * FROM sesiones_activas
        WHERE idBebe = :idBebe AND sessionType = :sessionType
        LIMIT 1
    """)
    suspend fun getByType(
        idBebe: String,
        sessionType: String
    ): ActiveSessionEntity?

    @Query("""
        DELETE FROM sesiones_activas
        WHERE idBebe = :idBebe AND sessionType = :sessionType
    """)
    suspend fun deleteByType(
        idBebe: String,
        sessionType: String
    )

    @Query("DELETE FROM sesiones_activas")
    suspend fun clearAll()
}