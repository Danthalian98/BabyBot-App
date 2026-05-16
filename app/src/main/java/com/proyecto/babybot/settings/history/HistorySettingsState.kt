package com.proyecto.babybot.settings.history

data class HistorySettingsState(
    val isLoading: Boolean = false,
    val conversations: List<ChatHistoryItemUi> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val isHistoryDeleted: Boolean = false
)

data class ChatHistoryItemUi(
    val id: String,
    val title: String,
    val lastMessage: String,
    val date: String,
    val messageCount: Int
)