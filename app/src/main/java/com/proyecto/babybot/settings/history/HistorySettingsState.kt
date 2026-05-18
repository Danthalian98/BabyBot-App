package com.proyecto.babybot.settings.history

data class HistorySettingsState(
    val isLoading: Boolean = false,
    val conversations: List<ChatHistoryItemUi> = emptyList(),
    val selectedConversation: ChatHistoryItemUi? = null,
    val showDeleteDialog: Boolean = false,
    val isHistoryDeleted: Boolean = false
)

data class ChatHistoryItemUi(
    val id: String,
    val title: String,
    val lastMessage: String,
    val date: String,
    val messageCount: Int,
    val timestamp: Long,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val messages: List<ChatMessageUi>,
    val copyText: String
)

data class ChatMessageUi(
    val id: String,
    val message: String,
    val isUser: Boolean,
    val time: String,
    val timestamp: Long
)