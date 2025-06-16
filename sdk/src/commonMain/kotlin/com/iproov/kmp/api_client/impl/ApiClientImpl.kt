// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api_client.impl

import com.iproov.kmp.api_client.ApiClient
import com.iproov.kmp.api_client.AssuranceType
import com.iproov.kmp.api_client.ClaimType
import com.iproov.kmp.util.saferUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClientImpl(
    private val baseUrl: String,
    private val apiKey: String,
    private val secret: String,
) : ApiClient {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    override suspend fun getToken(
        assuranceType: AssuranceType,
        claimType: ClaimType,
        userID: String,
        resource: String?
    ): String {
        try {
            val values = mutableMapOf(
                "api_key" to apiKey,
                "secret" to secret,
                "resource" to (resource ?: "com.iproov.kmp.api_client"),
                "client" to "kmp",
                "user_id" to userID,
                "assurance_type" to assuranceType.name.lowercase(),
            )

            val httpResponse = client.post("${baseUrl.saferUrl}claim/${claimType.name.lowercase()}/token") {
                contentType(Json)
                setBody(values)
            }

            if (httpResponse.status.isSuccess()) {
                val response: ResponseData = httpResponse.body()
                return response.token
            }

            throw Exception("Error: ${httpResponse.bodyAsText()}")
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error: ${e.message}")
        }
    }
}

@Serializable
data class ResponseData(val token: String)
