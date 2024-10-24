package com.chirag_redij.waltz.network.api

import com.chirag_redij.waltz.network.api.Webservices.FEED_IMAGE_COUNT
import com.chirag_redij.waltz.network.dataclasses.domain_states.PhotoModel
import com.chirag_redij.waltz.network.dataclasses.respose_models.PageFeedList
import com.chirag_redij.waltz.network.dataclasses.util.NetworkError
import com.chirag_redij.waltz.network.dataclasses.util.Result
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

object APIRepository {

    suspend fun getPhotosFeed(pageNumber : Int) : Result<PageFeedList,NetworkError> {

        return withContext(Dispatchers.IO) {
            val response = try {
                KtorClient.apiService.get {
                    url(Webservices.IMAGE_FEED_CURATED)
                    parameter("page", pageNumber)
                    parameter("per_page", FEED_IMAGE_COUNT)
                    parameter("sort", "desc")
                }
            } catch (e : UnresolvedAddressException) {
                return@withContext Result.Error(NetworkError.NO_INTERNET)
            } catch (e : SerializationException) {
                return@withContext Result.Error(NetworkError.SERIALIZATION)
            } catch(e: Exception) {
                coroutineContext.ensureActive()
                return@withContext Result.Error(NetworkError.UNKNOWN)
            }

            return@withContext when(response.status.value) {
                in 200..299 -> {
                    Result.Success(response.body<PageFeedList>())
                }
                401 -> Result.Error(NetworkError.UNAUTHORIZED)
                408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
                409 -> Result.Error(NetworkError.CONFLICT)
                413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
                500 -> Result.Error(NetworkError.SERVER_ERROR)
                else -> {
                    Result.Error(NetworkError.UNKNOWN)
                }
            }
        }

    }

    suspend fun getSpecificPhoto(photoId : Int) : Result<PhotoModel, NetworkError> {

        return withContext(Dispatchers.IO) {
            val response = try {
                KtorClient.apiService.get {
                    url(Webservices.getSpecificImage(photoId))
                }
            } catch (e : UnresolvedAddressException) {
                return@withContext Result.Error(NetworkError.NO_INTERNET)
            } catch (e : SerializationException) {
                return@withContext Result.Error(NetworkError.SERIALIZATION)
            } catch(e: Exception) {
                coroutineContext.ensureActive()
                return@withContext Result.Error(NetworkError.UNKNOWN)
            }

            return@withContext when(response.status.value) {
                in 200..299 -> {
                    Result.Success(response.body<PhotoModel>())
                }
                401 -> Result.Error(NetworkError.UNAUTHORIZED)
                408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
                409 -> Result.Error(NetworkError.CONFLICT)
                413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
                500 -> Result.Error(NetworkError.SERVER_ERROR)
                else -> {
                    Result.Error(NetworkError.UNKNOWN)
                }
            }
        }

    }

}