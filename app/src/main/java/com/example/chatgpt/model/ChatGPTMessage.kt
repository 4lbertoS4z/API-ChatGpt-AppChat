package com.example.chatgpt.model

class ChatGPTMessage(val message: String?, val sentBy: String?) {
    companion object {
        const val SEND_BY_ME = "me"
        const val SEND_BY_BOT = "bot"
    }
}