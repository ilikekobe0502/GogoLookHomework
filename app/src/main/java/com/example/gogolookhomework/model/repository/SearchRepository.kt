package com.example.gogolookhomework.model.repository

import com.example.gogolookhomework.model.api.response.ImageResponse
import com.example.gogolookhomework.model.api.response.SearchResultDisplayType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getDefaultDisplayType():Flow<SearchResultDisplayType>
    suspend fun searchImages(query: String): Flow<ImageResponse?>
}