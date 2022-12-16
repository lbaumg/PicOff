package com.example.picoff.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.picoff.models.GoogleAccountModel
import kotlinx.coroutines.flow.map


const val DataStore_NAME = "GOOGLE_ACCOUNT_INFO"

val Context.datastore : DataStore< Preferences> by  preferencesDataStore(name = DataStore_NAME)


class GoogleAccountRepository(private val context: Context) {

    companion object{
        val IS_LOGGED_IN = booleanPreferencesKey("IS_LOGGED_IN")
        val ACCOUNT_ID = stringPreferencesKey("ACCOUNT_ID")
        val ACCOUNT_NAME = stringPreferencesKey("ACCOUNT_NAME")
        val ACCOUNT_EMAIL = stringPreferencesKey("ACCOUNT_EMAIL")
        val ACCOUNT_PHOTO_URL = stringPreferencesKey("ACCOUNT_PHOTO_URL")
    }

    suspend fun saveGoogleAccount(googleAccount: GoogleAccountModel) {
        context.datastore.edit { accounts->
            accounts[IS_LOGGED_IN] = googleAccount.isLoggedIn
            accounts[ACCOUNT_ID] = googleAccount.accountId
            accounts[ACCOUNT_NAME] = googleAccount.accountName
            accounts[ACCOUNT_EMAIL] = googleAccount.accountEmail
            accounts[ACCOUNT_PHOTO_URL] = googleAccount.accountPhotoUrl
        }
    }

    fun getGoogleAccount() = context.datastore.data.map { googleAccount ->
        GoogleAccountModel(
            isLoggedIn = googleAccount[IS_LOGGED_IN]?:false,
            accountId = googleAccount[ACCOUNT_ID]?:"",
            accountName = googleAccount[ACCOUNT_NAME]?:"",
            accountEmail = googleAccount[ACCOUNT_EMAIL]?:"",
            accountPhotoUrl = googleAccount[ACCOUNT_PHOTO_URL]?:""
        )
    }
}
