package com.wonchihyeon.ytt_android

import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import kotlin.random.Random

class VendingMachineClient(private val vendingMachineId: String, private val orderId: String) {
    private var ws: WebSocket? = null
    private val sessionId: String = "vending-machine-${Random.nextInt(0, 1000000)}"
    private var connected: Boolean = false

    fun connect() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("ws://13.125.128.15:8080/ws/websocket")
            .addHeader("Accept-Version", "1.1,1.0")
            .addHeader("Heart-Beat", "10000,10000")
            .build()

        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                connected = true
                println("WebSocket connected: ${response.message}")

               /* val subscribeMessage = JSONObject()
                    .put("action", "subscribe")
                    .put("topic", "/topic/orders/store/${orderId}")
                    .toString()
*/
                webSocket.send("/topic/order/store/${orderId}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Message received: $text")
                // 메시지를 처리하는 로직 추가 가능
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                println("Message received: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                println("WebSocket closing: $code / $reason")
                connected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket error: ${t.message}")
                connected = false
            }
        })

        // 클라이언트의 WebSocket 연결을 유지하기 위해 executorService를 종료하지 않음
        client.dispatcher.executorService.shutdown()
    }

    fun close() {
        ws?.close(1000, "Closing connection")
    }
}
