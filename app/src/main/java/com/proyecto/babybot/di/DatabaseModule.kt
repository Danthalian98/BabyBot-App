package com.proyecto.babybot.di

import android.content.Context
import androidx.room.Room
import com.proyecto.babybot.data.local.dao.ActiveSessionDao
import com.proyecto.babybot.data.local.database.BabyBotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.proyecto.babybot.data.local.dao.ChatDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BabyBotDatabase {
        return Room.databaseBuilder(
            context,
            BabyBotDatabase::class.java,
            "babybot_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBabyDao(db: BabyBotDatabase) = db.babyDao()

    @Provides
    fun provideMealDao(db: BabyBotDatabase) = db.mealDao()

    @Provides
    fun provideDiaperDao(db: BabyBotDatabase) = db.diaperDao()

    @Provides
    fun provideSleepDao(db: BabyBotDatabase) = db.sleepDao()

    @Provides
    fun provideActiveSessionDao(db: BabyBotDatabase): ActiveSessionDao {
        return db.activeSessionDao()
    }

    @Provides
    fun provideChatDao(db: BabyBotDatabase): ChatDao {
        return db.chatDao()
    }
}