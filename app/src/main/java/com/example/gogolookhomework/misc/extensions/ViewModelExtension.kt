package com.example.gogolookhomework.misc.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gogolookhomework.misc.provider.TestCoroutineScope

fun ViewModel.getViewModelScope() =
    TestCoroutineScope ?: this.viewModelScope