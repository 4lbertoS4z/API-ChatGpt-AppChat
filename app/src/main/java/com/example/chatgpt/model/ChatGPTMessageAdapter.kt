package com.example.chatgpt.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgpt.R
import com.google.android.material.card.MaterialCardView


class ChatGPTMessageAdapter(private val chatGPTMessageList: List<ChatGPTMessage>) : RecyclerView.Adapter<ChatGPTMessageAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = chatGPTMessageList[position]
        if (message.sentBy == ChatGPTMessage.SEND_BY_ME) {
            holder.left_chat_view.visibility = View.GONE
            holder.right_chat_view.visibility = View.VISIBLE
            holder.right_chat_text_view.text = message.message
        } else {
            holder.right_chat_view.visibility = View.GONE
            holder.left_chat_view.visibility = View.VISIBLE
            holder.left_chat_text_view.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return chatGPTMessageList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val left_chat_view: MaterialCardView = itemView.findViewById(R.id.left_chat_view)
        val right_chat_view: MaterialCardView = itemView.findViewById(R.id.right_chat_view)
        val left_chat_text_view: TextView = itemView.findViewById(R.id.left_chat_text_view)
        val right_chat_text_view: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }
}
