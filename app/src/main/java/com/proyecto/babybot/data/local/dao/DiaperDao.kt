package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proyecto.babybot.data.local.entity.DiaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaperDao {

    @Insert
    suspend fun insert(diaper: DiaperEntity)

    @Query("""
        SELECT * FROM registros_panal
        WHERE idBebe = :idBebe
        AND timestamp BETWEEN :start AND :end
        ORDER BY timestamp DESC
    """)
    fun getByDay(idBebe: String, start: Long, end: Long): Flow<List<DiaperEntity>>

    @Query("""
        SELECT COUNT(*) FROM registros_panal
        WHERE idBebe = :idBebe
        AND timestamp BETWEEN :start AND :end
    """)
    suspend fun count(idBebe: String, start: Long, end: Long): Int

    @Query("""
    SELECT * FROM registros_panal
    WHERE idBebe = :idBebe
    AND timestamp BETWEEN :start AND :end
    ORDER BY timestamp DESC
""")
    suspend fun getByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<DiaperEntity>
}