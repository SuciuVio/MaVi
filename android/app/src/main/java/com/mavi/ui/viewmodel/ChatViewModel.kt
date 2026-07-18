package com.mavi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavi.data.repository.ChatRepository
import com.mavi.data.local.MessageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadConversation(userId: Int, otherUserId: Int) = viewModelScope.launch {
        chatRepository.getConversation(userId, otherUserId).collect { msgs ->
            _messages.value = msgs
        }
        fetchMessages(otherUserId)
    }

    fun sendMessage(receiverId: Int, content: String) = viewModelScope.launch {
        _uiState.value = ChatUiState.Loading
        val result = chatRepository.sendMessage(receiverId, content)
        _uiState.value = if (result.isSuccess) {
            ChatUiState.Success("Message sent")
        } else {
            ChatUiState.Error(result.exceptionOrNull()?.message ?: "Failed to send")
        }
    }

    private fun fetchMessages(otherUserId: Int) = viewModelScope.launch {
        chatRepository.fetchMessages(otherUserId)
    }

    fun markAsRead(messageId: Int) = viewModelScope.launch {
        chatRepository.markAsRead(messageId)
    }

    fun deleteMessage(messageId: Int) = viewModelScope.launch {
        chatRepository.deleteMessage(messageId)
    }
}

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val message: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}
