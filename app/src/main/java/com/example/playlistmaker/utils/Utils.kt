package com.example.playlistmaker.utils

object Utils {
    fun getLastPart(path: String): String {
        return if (path.isEmpty()) {
            ""
        } else {
            path.substringAfterLast("/")
        }
    }

}