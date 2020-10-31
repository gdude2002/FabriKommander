package me.gserv.fabrikommander.data.spec

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,

    val homes: MutableList<Home> = mutableListOf(),
)
