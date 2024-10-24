package com.chirag_redij.waltz.network.api

import com.chirag_redij.waltz.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

interface KtorClient {

    companion object {

        val apiService by lazy { create() }

        private fun create(): HttpClient {
            val client = HttpClient(Android) {

                if (!BuildConfig.BUILD_TYPE.equals("release", true)) {
                    install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                Timber.tag("KTOR Response").d(message)
                            }
                        }
                        level = LogLevel.ALL
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            ignoreUnknownKeys = true
                            isLenient = true
                            prettyPrint = true
                        }
                    )
                }

                install(HttpTimeout) {
                    requestTimeoutMillis = 15000L
                    connectTimeoutMillis = 15000L
                    socketTimeoutMillis = 15000L
                }

                install(ResponseObserver) {
                    onResponse { response ->
                        Timber.tag("HTTP status:").d("${response.status.value}")
                    }
                }

                // Apply to all requests
                defaultRequest {
                    headers {
                        append("Authorization",BuildConfig.authorizationKey)
                    }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }

            }
            return client
        }

    }

}