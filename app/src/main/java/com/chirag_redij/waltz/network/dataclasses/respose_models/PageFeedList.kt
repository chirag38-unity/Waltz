package com.chirag_redij.waltz.network.dataclasses.respose_models

import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageFeedList(
    val page : Int,
    val per_page : Int,
//    @SerialName("media")
    val photos : List<PhotoModel> = emptyList(),
    val total_results : Int
)
