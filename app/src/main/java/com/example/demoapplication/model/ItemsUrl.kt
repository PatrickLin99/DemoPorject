package com.example.demoapplication.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemsUrl(
    @SerialName("items") val item: List<Url>
)

@Serializable
data class Url(
    @SerialName("url") val item: String
)


