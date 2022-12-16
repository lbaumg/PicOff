package com.example.picoff.models

data class GoogleAccountModel (
    val isLoggedIn: Boolean,
    val accountId: String,
    val accountName: String,
    val accountEmail: String,
    val accountPhotoUrl: String
)
