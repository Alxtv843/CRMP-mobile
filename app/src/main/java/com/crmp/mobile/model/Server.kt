package com.crmp.mobile.model

import java.util.UUID

data class Server(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val host: String,
    val port: Int = 7777,
    val isFavorite: Boolean = false,
) {
    fun endpoint(): String = "$host:$port"

    companion object {
        fun samples(): List<Server> = listOf(
            Server(name = "Пример CRMP #1", host = "127.0.0.1", port = 7777, isFavorite = true),
            Server(name = "Пример SA-MP", host = "play.example.com", port = 7777, isFavorite = false),
        )
    }
}
