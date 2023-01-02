package com.example.gogolookhomework.contenproviders

import android.content.SearchRecentSuggestionsProvider

class SearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY =
            "com.example.gogolookhomework.contenproviders.SearchSuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}