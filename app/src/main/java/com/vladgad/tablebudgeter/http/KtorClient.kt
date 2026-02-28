package com.vladgad.tablebudgeter.http

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import io.ktor.http.*

class KtorClient {

    companion object {
        val INSTANCE_HTTP_CLIENT: HttpClient by lazy {
            HttpClient {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    gson {
                        setPrettyPrinting()
                        disableHtmlEscaping()
                    }
                }

                // Базовая конфигурация
                defaultRequest {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }
            }
        }
    }
}


