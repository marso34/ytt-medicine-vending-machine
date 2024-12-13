package com.wonchihyeon.ytt_android

import android.util.Log
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class MyWebSocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        Log.d("WebSocket", "WebSocket connected: ${response.message}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Message received: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("WebSocket", "Messa" +
                "ge received: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d("WebSocket", "WebSocket closing: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        Log.e("WebSocket", "Error: ${t.message}")
    }
}
