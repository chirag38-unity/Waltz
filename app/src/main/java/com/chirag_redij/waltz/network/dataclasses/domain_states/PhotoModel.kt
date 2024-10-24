package com.chirag_redij.waltz.network.dataclasses.domain_states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PhotoModel(
    val alt: String,
    @SerialName("avg_color")
    val avgColor: String,
    val id: Int,
    val liked: Boolean,
    val photographer: String,
    @SerialName("photographer_id")
    val photographerId: Int,
    @SerialName("photographer_url")
    val photographerUrl: String,
    val src: Src,
    val url: String,
    val height: Int,
    val width: Int
) : Parcelable