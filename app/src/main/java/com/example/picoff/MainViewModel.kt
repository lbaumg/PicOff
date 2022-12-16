package com.example.picoff

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picoff.models.GoogleAccountModel
import com.example.picoff.repository.GoogleAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val googleAccountRepository: GoogleAccountRepository) : ViewModel() {

    var loadedData = MutableLiveData<Boolean>(false)

    val isLoggedIn = MutableLiveData<Boolean>(false)
    val accountName = MutableLiveData<String?>(null)
    val accountEmail = MutableLiveData<String?>(null)
    val accountId = MutableLiveData<String?>(null)
    val accountPhotoUrl = MutableLiveData<Uri?>(null)


    fun updateAccount(id: String?, displayName: String?, email: String?, photoUrl: Uri?, loggedIn : Boolean = true) {
        accountId.value = id
        accountName.value = displayName
        accountEmail.value = email
        accountPhotoUrl.value = photoUrl
        isLoggedIn.value = loggedIn

        saveGoogleAccountData()
    }

    private fun saveGoogleAccountData() {
        viewModelScope.launch(Dispatchers.IO) {
            googleAccountRepository.saveGoogleAccount(
                GoogleAccountModel(
                    isLoggedIn = isLoggedIn.value!!,
                    accountId = accountId.value ?: "",
                    accountName = accountName.value ?: "",
                    accountEmail = accountEmail.value ?: "",
                    accountPhotoUrl = accountPhotoUrl.value.toString()
                )
            )
        }
    }

    fun retrieveGoogleAccountData() {
        viewModelScope.launch(Dispatchers.IO) {
            googleAccountRepository.getGoogleAccount().collect {
                isLoggedIn.postValue(it.isLoggedIn)
                accountId.postValue(if (it.accountId != "") it.accountId else null)
                accountName.postValue(if (it.accountName != "") it.accountName else null)
                accountEmail.postValue(if (it.accountEmail != "") it.accountEmail else null)
                accountPhotoUrl.postValue(if (it.accountPhotoUrl != "") Uri.parse(it.accountPhotoUrl) else null)
                loadedData.postValue(true)
            }
        }
    }
}
