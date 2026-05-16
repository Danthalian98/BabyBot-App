package com.proyecto.babybot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proyecto.babybot.data.local.entity.ChatHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(message: ChatHistoryEntity)
    @Query("SELECT * FROM chat_history WHERE idUsuario = :uid ORDER BY id ASC ")
    suspend fun getAllMessages(uid: String): List<ChatHistoryEntity>

    // Recupera los últimos 3 mensajes del usuario actual para la "memoria"
    @Query("SELECT * FROM chat_history WHERE idUsuario = :idUsuario ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLastMessages(idUsuario: String, limit: Int): List<ChatHistoryEntity>

    // Para limpiar el historial si fuera necesario (opcional)
    @Query("DELETE FROM chat_history WHERE idUsuario = :idUsuario")
    suspend fun deleteHistory(idUsuario: String)
}