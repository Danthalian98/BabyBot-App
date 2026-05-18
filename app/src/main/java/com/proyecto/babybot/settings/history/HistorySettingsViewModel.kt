package com.proyecto.babybot.settings.history

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModel
import com.proyecto.babybot.data.local.dao.ChatDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HistorySettingsViewModel @Inject constructor(
    private val chatDao: ChatDao
) : ViewModel() {

    private val _state = MutableStateFlow(
        HistorySettingsState(

        )
    )

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

            val messages = chatDao.getChatHistory(uid)

            val grouped = messages.groupBy {

                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(it.timestamp))
            }

            val conversations = grouped.map { (date, messagesInDay) ->

                ChatHistoryItemUi(
                    id = date,

                    title = messagesInDay
                        .firstOrNull { it.isUser }
                        ?.message
                        ?.take(40)
                        ?: "Conversación",

                    lastMessage = messagesInDay
                        .lastOrNull()
                        ?.message
                        ?: "",

                    date = formatDateLabel(messagesInDay.last().timestamp),

                    messageCount = messagesInDay.size,

                    timestamp = messagesInDay.last().timestamp
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

    private fun formatDateLabel(timestamp: Long): String {

        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
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
                    showDeleteDialog = false,
                    isHistoryDeleted = true
                )
            }
        }
    }
}