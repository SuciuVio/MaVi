package com.mavi.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mavi.databinding.FragmentChatDetailBinding
import com.mavi.ui.adapters.MessagesAdapter
import com.mavi.ui.viewmodel.ChatViewModel

class ChatDetailFragment : Fragment() {

    private lateinit var binding: FragmentChatDetailBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messagesAdapter: MessagesAdapter
    private var otherUserId: Int = 0
    private var userName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            otherUserId = it.getInt("user_id", 0)
            userName = it.getString("username", "User")
        }

        binding.toolbarTitle.text = userName
        setupRecyclerView()
        setupObservers()
        setupListeners()
        chatViewModel.getMessages(otherUserId)
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messagesAdapter
        }
    }

    private fun setupListeners() {
        binding.sendButton.setOnClickListener {
            val content = binding.messageInput.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            chatViewModel.sendMessage(otherUserId, content)
            binding.messageInput.text?.clear()
        }
    }

    private fun setupObservers() {
        chatViewModel.messages.observe(viewLifecycleOwner) { result ->
            result.onSuccess { messages ->
                messagesAdapter.submitList(messages)
                if (messages.isNotEmpty()) {
                    binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
            result.onFailure { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.sendMessageResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                chatViewModel.getMessages(otherUserId)
            }
            result.onFailure { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
