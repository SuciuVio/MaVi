package com.mavi.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mavi.databinding.ItemMessageBinding
import com.mavi.network.MessageResponse

class MessagesAdapter :
    ListAdapter<MessageResponse, MessagesAdapter.ViewHolder>(MessageDiffCallback()) {

    inner class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageResponse) {
            binding.messageText.text = message.content
            binding.senderName.text = message.sender_username
            binding.timestamp.text = message.timestamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<MessageResponse>() {
    override fun areItemsTheSame(oldItem: MessageResponse, newItem: MessageResponse) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MessageResponse, newItem: MessageResponse) =
        oldItem == newItem
}
