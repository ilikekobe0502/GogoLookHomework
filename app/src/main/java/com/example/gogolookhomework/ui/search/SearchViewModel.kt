package com.example.gogolookhomework.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gogolookhomework.enums.DisplayType
import com.example.gogolookhomework.misc.extensions.getViewModelScope
import com.example.gogolookhomework.misc.provider.DispatcherProvider
import com.example.gogolookhomework.model.api.ApiResult
import com.example.gogolookhomework.model.api.response.Image
import com.example.gogolookhomework.model.repository.SearchRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val apiRepository: SearchRepository
) : ViewModel() {
    private val _searchResult = MutableLiveData<ApiResult<List<Image>>>()
    val searchResult: LiveData<ApiResult<List<Image>>> = _searchResult

    private val _defaultDisplayTypeResult = MutableLiveData<ApiResult<DisplayType>>()
    val defaultDisplayTypeResult: LiveData<ApiResult<DisplayType>> = _defaultDisplayTypeResult

    init {
        getDefaultDisplayType()
        searchImage("")
    }

    fun searchImage(queryString: String) {
        _searchResult.postValue(ApiResult.loading())

        getViewModelScope().launch(DispatcherProvider.IO) {
            apiRepository.searchImages(queryString)
                .map { response ->
                    response?.let {
                        return@map it.hits
                    } ?: kotlin.run {
                        throw Exception("Empty Result")
                    }
                }
                .catch {
                    _searchResult.postValue(ApiResult.error(it))
                }.collect {
                    _searchResult.postValue(ApiResult.success(it))
                }
        }
    }

    private fun getDefaultDisplayType() {
        getViewModelScope().launch(DispatcherProvider.IO) {
            apiRepository.getDefaultDisplayType()
                .catch {
                    _defaultDisplayTypeResult.postValue(ApiResult.error(it))
                }.collect {
                    _defaultDisplayTypeResult.postValue(ApiResult.success(it.displayType))
                }
        }
    }
}