package com.proyecto.babybot.settings.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.babybot.data.local.dao.ChatDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistorySettingsViewModel @Inject constructor(
    private val chatDao: ChatDao
) : ViewModel() {

    private val _state = MutableStateFlow(HistorySettingsState())
    val state = _state.asStateFlow()

    init {
        loadHistory()
    }

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

    fun showConversation(conversation: ChatHistoryItemUi) {
        _state.update {
            it.copy(selectedConversation = conversation)
        }
    }

    fun hideConversation() {
        _state.update {
            it.copy(selectedConversation = null)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val uid = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid

            if (uid == null) {
                _state.update {
                    it.copy(isLoading = false)
                }
                return@launch
            }

            val messages = chatDao
                .getChatHistory(uid)
                .sortedBy { it.timestamp }

            val grouped = messages.groupBy {
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(it.timestamp))
            }

            val conversations = grouped.map { (date, messagesInDay) ->

                val sortedMessages = messagesInDay.sortedBy { it.timestamp }

                val uiMessages = sortedMessages.map { message ->
                    ChatMessageUi(
                        id = message.id.toString(),
                        message = message.message,
                        isUser = message.isUser,
                        time = formatTimeLabel(message.timestamp),
                        timestamp = message.timestamp
                    )
                }

                val firstUserMessage = sortedMessages
                    .firstOrNull { it.isUser }
                    ?.message
                    ?.trim()

                val title = when {
                    firstUserMessage.isNullOrBlank() -> "Conversación"
                    firstUserMessage.length > 42 -> firstUserMessage.take(42) + "..."
                    else -> firstUserMessage
                }

                val lastMessage = sortedMessages
                    .lastOrNull()
                    ?.message
                    ?.trim()
                    .orEmpty()

                ChatHistoryItemUi(
                    id = date,
                    title = title,
                    lastMessage = lastMessage,
                    date = formatDateLabel(sortedMessages.last().timestamp),
                    messageCount = sortedMessages.size,
                    timestamp = sortedMessages.last().timestamp,
                    startTimestamp = sortedMessages.first().timestamp,
                    endTimestamp = sortedMessages.last().timestamp,
                    messages = uiMessages,
                    copyText = buildCopyText(
                        date = formatDateLabel(sortedMessages.last().timestamp),
                        messages = uiMessages
                    )
                )
            }.sortedByDescending {
                it.timestamp
            }

            _state.update {
                it.copy(
                    conversations = conversations,
                    isLoading = false
                )
            }
        }
    }

    private fun buildCopyText(
        date: String,
        messages: List<ChatMessageUi>
    ): String {
        return buildString {
            appendLine("Conversación con BabyBot")
            appendLine("Fecha: $date")
            appendLine()

            messages.forEach { message ->
                val sender = if (message.isUser) {
                    "Usuario"
                } else {
                    "BabyBot"
                }

                appendLine("$sender (${message.time}):")
                appendLine(message.message)
                appendLine()
            }
        }.trim()
    }

    private fun formatDateLabel(timestamp: Long): String {
        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        return sdf.format(Date(timestamp))
    }

    private fun formatTimeLabel(timestamp: Long): String {
        val sdf = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )

        return sdf.format(Date(timestamp))
    }

    fun deleteHistory() {
        viewModelScope.launch {
            val uid = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid ?: return@launch

            chatDao.deleteHistory(uid)

            _state.update {
                it.copy(
                    conversations = emptyList(),
                    selectedConversation = null,
                    showDeleteDialog = false,
                    isHistoryDeleted = true
                )
            }
        }
    }

    fun deleteSelectedConversation() {
        viewModelScope.launch {
            val uid = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid ?: return@launch

            val selectedConversation = _state.value.selectedConversation
                ?: return@launch

            chatDao.deleteConversationByRange(
                uid = uid,
                startTimestamp = selectedConversation.startTimestamp,
                endTimestamp = selectedConversation.endTimestamp
            )

            _state.update { currentState ->
                currentState.copy(
                    selectedConversation = null,
                    conversations = currentState.conversations.filterNot {
                        it.id == selectedConversation.id
                    }
                )
            }
        }
    }
}