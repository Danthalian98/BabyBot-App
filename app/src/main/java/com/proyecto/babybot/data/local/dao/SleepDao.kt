package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proyecto.babybot.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Insert
    suspend fun insert(sleep: SleepEntity)

    @Query("""
        SELECT * FROM registros_sueno
        WHERE idBebe = :idBebe
        AND inicio BETWEEN :start AND :end
        ORDER BY inicio DESC
    """)
    fun getByDay(idBebe: String, start: Long, end: Long): Flow<List<SleepEntity>>

    @Query("""
    SELECT * FROM registros_sueno
    WHERE idBebe = :idBebe
    AND inicio BETWEEN :start AND :end
    ORDER BY inicio DESC
""")
    suspend fun getByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<SleepEntity>
}