package com.chirag_redij.waltz.network.api

object Webservices {

    private const val BASE_URL = "https://api.pexels.com"

    private const val IMAGES_API = "$BASE_URL/v1"
    private const val VIDEOS_API = "$BASE_URL/videos"

    const val IMAGE_FEED_CURATED  = "$IMAGES_API/curated"
    const val IMAGE_FEED_COLLECTION  = "$IMAGES_API/collections/5jamfc8"
    const val FEED_IMAGE_COUNT = 20;

    fun getSpecificImage(imageId : Int) = "$IMAGES_API/photos/$imageId"

}