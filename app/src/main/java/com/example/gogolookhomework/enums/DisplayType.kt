package com.example.gogolookhomework.enums

enum class DisplayType {
    GRID,
    LIST;

    companion object {
        fun getLocalDefaultDisplayType(): DisplayType {
            return LIST
        }
    }
}