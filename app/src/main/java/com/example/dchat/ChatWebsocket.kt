package com.example.dchat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import com.google.gson.Gson

class ChatWebsocket {
    private val client = OkHttpClient()
    private var webSocket: WebSocket?=null
    private val _connectionState =
        MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionState: StateFlow<ConnectionStatus> =
        _connectionState
    private val gson= Gson()
    fun connect(
        token: String,
        onMessage: (UserMessageResponse) -> Unit
    ) {
        _connectionState.value = ConnectionStatus.CONNECTING
        val request = Request.Builder().url("wss://dchat-production-89ff.up.railway.app/ws?token=$token").build()
        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    _connectionState.value=ConnectionStatus.CONNECTED
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    _connectionState.value = ConnectionStatus.DISCONNECTED
                    webSocket.close(code, reason)
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int, reason: String
                ) {
                    _connectionState.value=ConnectionStatus.DISCONNECTED
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    val message = gson.fromJson(
                        text,
                        UserMessageResponse::class.java
                    )
                    onMessage(message)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    t.printStackTrace()
                    _connectionState.value=ConnectionStatus.FAILED
                }
            })
    }

    fun send(
        receiverId: Int,
        message: String
    ): Boolean {
        val json = JSONObject().apply {
            put("receiver_id", receiverId)
            put("content", message)
        }
        return webSocket?.send(json.toString()) ?: false
    }

    fun disConnect(){
        webSocket?.close(1000,"exit")
        webSocket = null
        client.dispatcher.executorService.shutdown()
    }
}