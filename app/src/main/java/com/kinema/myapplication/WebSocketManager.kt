package com.kinema.myapplication

import org.java_websocket.client.WebSocketClient

object WebSocketManager {
    var client: WebSocketClient? = null
    var nombre: String = ""
    var correo: String = ""
    var respuestaCompra: ((String) -> Unit)? = null
    var respuestaEntrades: ((String) -> Unit)? = null
}