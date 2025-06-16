// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api_client

interface ApiClient {
    suspend fun getToken(assuranceType: AssuranceType, claimType: ClaimType, userID: String, resource: String?): String
}
