package com.proyecto.babybot.settings.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HistorySettingsViewModel @Inject constructor(
    // Después aquí puedes inyectar tu ChatRepository o ChatDao
) : ViewModel() {

    private val _state = MutableStateFlow(
        HistorySettingsState(
            conversations = listOf(
                ChatHistoryItemUi(
                    id = "1",
                    title = "Consulta sobre alimentación",
                    lastMessage = "Puedes ofrecer pequeñas porciones según la edad del bebé...",
                    date = "Hoy",
                    messageCount = 8
                ),
                ChatHistoryItemUi(
                    id = "2",
                    title = "Duda sobre sueño",
                    lastMessage = "Para mejorar la rutina nocturna, intenta mantener horarios constantes...",
                    date = "Ayer",
                    messageCount = 5
                )
            )
        )
    )

    val state = _state.asStateFlow()

    fun showDeleteDialog() {
        _state.update {
            it.copy(showDeleteDialog = true)
        }
    }

    fun hideDeleteDialog() {
        _state.update {
            it.copy(showDeleteDialog = false)
        }
    }

    fun deleteHistory() {
        // Después aquí borras desde Room
        _state.update {
            it.copy(
                conversations = emptyList(),
                showDeleteDialog = false,
                isHistoryDeleted = true
            )
        }
    }
}