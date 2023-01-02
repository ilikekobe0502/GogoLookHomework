package com.example.gogolookhomework.model.repository

import com.example.gogolookhomework.enums.DisplayType
import com.example.gogolookhomework.model.api.ApiService
import com.example.gogolookhomework.model.api.response.ImageResponse
import com.example.gogolookhomework.model.api.response.SearchResultDisplayType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException


class SearchRepositoryImpl(private val apiService: ApiService) : SearchRepository {
    override suspend fun getDefaultDisplayType(): Flow<SearchResultDisplayType> {
        return flowOf(apiService.getDefaultDisplayType())
            .map { result ->
                if (!result.isSuccessful) throw HttpException(result)
                return@map result.body() ?: SearchResultDisplayType(DisplayType.getLocalDefaultDisplayType())
            }
    }

    override suspend fun searchImages(query: String): Flow<ImageResponse?> {
        val queryMap: HashMap<String, String> = HashMap()
        queryMap["q"] = query
        return flowOf(apiService.searchImages(queryMap))
            .map { result ->
                if (!result.isSuccessful) throw HttpException(result)
                return@map result.body()
            }
    }
}