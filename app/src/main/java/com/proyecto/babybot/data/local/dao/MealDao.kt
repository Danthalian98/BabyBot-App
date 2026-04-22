package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.proyecto.babybot.data.local.entity.MealEntity

@Dao
interface MealDao {

    @Insert
    suspend fun insert(meal: MealEntity)

    @Query("""
        SELECT * FROM registros_comida
        WHERE idBebe = :idBebe
        AND timestamp BETWEEN :start AND :end
        ORDER BY timestamp DESC
    """)
    fun getMealsByDay(
        idBebe: String,
        start: Long,
        end: Long
    ): Flow<List<MealEntity>>

    @Query("""
        SELECT COUNT(*) FROM registros_comida
        WHERE idBebe = :idBebe
        AND timestamp BETWEEN :start AND :end
    """)
    suspend fun countMeals(
        idBebe: String,
        start: Long,
        end: Long
    ): Int

    @Query("""
    SELECT * FROM registros_comida
    WHERE idBebe = :idBebe
    AND timestamp BETWEEN :start AND :end
    ORDER BY timestamp DESC
""")
    suspend fun getMealsByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<MealEntity>
}