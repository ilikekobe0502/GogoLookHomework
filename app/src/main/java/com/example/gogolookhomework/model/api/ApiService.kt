package com.example.gogolookhomework.model.api

import com.example.gogolookhomework.model.api.response.ImageResponse
import com.example.gogolookhomework.model.api.response.SearchResultDisplayType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    @GET("displayType")
    suspend fun getDefaultDisplayType(): Response<SearchResultDisplayType>

    @GET(".")
    suspend fun searchImages(@QueryMap map: HashMap<String, String>): Response<ImageResponse>
}