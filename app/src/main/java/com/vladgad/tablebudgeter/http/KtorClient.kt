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
        @Volatile
        private var INSTANCE: HttpClient? = null
        fun getInstanceClientSheets(): HttpClient {
            return INSTANCE?: synchronized(this){
                val client = HttpClient {
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
                INSTANCE = client
                client
            }
        }
    }


}


