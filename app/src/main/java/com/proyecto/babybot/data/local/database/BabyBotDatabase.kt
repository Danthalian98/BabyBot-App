package com.proyecto.babybot.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.proyecto.babybot.data.local.converter.Converters
import com.proyecto.babybot.data.local.dao.ActiveSessionDao
import com.proyecto.babybot.data.local.dao.BabyDao
import com.proyecto.babybot.data.local.dao.DiaperDao
import com.proyecto.babybot.data.local.dao.MealDao
import com.proyecto.babybot.data.local.dao.SleepDao
import com.proyecto.babybot.data.local.entity.ActiveSessionEntity
import com.proyecto.babybot.data.local.entity.BabyEntity
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity

@Database(
    entities = [
        BabyEntity::class,
        MealEntity::class,
        DiaperEntity::class,
        SleepEntity::class,
        ActiveSessionEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BabyBotDatabase : RoomDatabase() {

    abstract fun babyDao(): BabyDao
    abstract fun mealDao(): MealDao
    abstract fun diaperDao(): DiaperDao
    abstract fun sleepDao(): SleepDao
    abstract fun activeSessionDao(): ActiveSessionDao
}