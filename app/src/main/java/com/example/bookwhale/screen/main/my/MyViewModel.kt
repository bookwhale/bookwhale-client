package com.example.bookwhale.screen.main.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.my.LogOutDTO
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import com.example.bookwhale.model.auth.ProfileModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MyViewModel(
    private val myRepository: MyRepository
): BaseViewModel() {

    val profileStateLiveData = MutableLiveData<MyState>(MyState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        profileStateLiveData.value = MyState.Loading
        val response = myRepository.getMyInfo()

        if (response.status == NetworkResult.Status.SUCCESS) {
            profileStateLiveData.value = MyState.Success(
                ProfileModel(
                    nickName = response.data!!.nickName,
                    profileImage = response.data!!.profileImage
                )
            )
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

    fun updateNickName(nickName: String) = viewModelScope.launch {
        profileStateLiveData.value = MyState.Loading
        val response = myRepository.updateMyNickName(NickNameRequestDTO(nickName))

        if (response.status == NetworkResult.Status.SUCCESS) {
            fetchData()
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

    fun updateProfileImage(image: MultipartBody.Part) = viewModelScope.launch {
        profileStateLiveData.value = MyState.Loading

        val response = myRepository.updateProfileImage(image)

        if (response.status == NetworkResult.Status.SUCCESS) {
            fetchData()
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

    fun logOut() = viewModelScope.launch {
        profileStateLiveData.value = MyState.Loading

        val logOutDTO = LogOutDTO(
            apiToken = myPreferenceManager.getAccessToken()!!,
            refreshToken = myPreferenceManager.getRefreshToken()!!
        )

        val response = myRepository.logOut(logOutDTO)

        if(response.status == NetworkResult.Status.SUCCESS) {
            profileStateLiveData.value = MyState.LogOutSuccess
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

    fun withDraw() = viewModelScope.launch {
        profileStateLiveData.value = MyState.Loading

        val logOutDTO = LogOutDTO(
            apiToken = myPreferenceManager.getAccessToken()!!,
            refreshToken = myPreferenceManager.getRefreshToken()!!
        )

        val response = myRepository.withDraw(logOutDTO)

        if(response.status == NetworkResult.Status.SUCCESS) {
            profileStateLiveData.value = MyState.WithDrawSuccess
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

    fun deleteSavedToken() {
        myPreferenceManager.removeAccessToken()
        myPreferenceManager.removeRefreshToken()
    }

}