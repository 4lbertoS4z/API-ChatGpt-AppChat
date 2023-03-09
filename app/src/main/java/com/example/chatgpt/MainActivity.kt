package com.example.chatgpt


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatgpt.api.API
import com.example.chatgpt.model.ChatGPTMessage
import com.example.chatgpt.model.ChatGPTMessageAdapter
import com.example.chatgpt.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val chatGPTMessageList: MutableList<ChatGPTMessage> = ArrayList()
    private var chatGPTMessageAdapter: ChatGPTMessageAdapter? = null

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaTypeOrNull()!!
    private var client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.messageTextText
        binding.sendBtn


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = linearLayoutManager

        chatGPTMessageAdapter = ChatGPTMessageAdapter(chatGPTMessageList)
        binding.recyclerView.adapter = chatGPTMessageAdapter

        binding.sendBtn.setOnClickListener {
            val question = binding.messageTextText.text.toString().trim()
            addToChat(question, ChatGPTMessage.SEND_BY_ME)
            binding.messageTextText.setText("")
            callAPI(question)
        }
    }

    private fun addToChat(message: String?, sendBy: String?) {
        runOnUiThread {
            chatGPTMessageList.add(ChatGPTMessage(message, sendBy))
            chatGPTMessageAdapter?.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(chatGPTMessageAdapter?.itemCount ?: 0)
        }
    }

    private fun addResponse(response: String?) {
        chatGPTMessageList.removeAt(chatGPTMessageList.size - 1)
        addToChat(response, ChatGPTMessage.SEND_BY_BOT)
    }

    private fun callAPI(question: String?) {
        // okhttp
        chatGPTMessageList.add(ChatGPTMessage("Espera...", ChatGPTMessage.SEND_BY_BOT))
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val requestBody: RequestBody = RequestBody.create(JSON, jsonBody.toString())
        val request: Request = Request.Builder()
            .url(API.API_URL)
            .header("Authorization", "Bearer " + API.API)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Error al cargar la respuesta debido a " + e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim())
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                } else {
                    addResponse("Error al cargar la respuesta debido a " + response.body.toString())
                }
            }
        })
    }
}
