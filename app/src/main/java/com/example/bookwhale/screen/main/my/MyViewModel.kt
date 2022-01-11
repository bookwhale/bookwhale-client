package com.example.bookwhale.screen.main.my

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.my.ProfileModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyViewModel(
    private val myRepository: MyRepository
): BaseViewModel() {

    val profileStateLiveData = MutableLiveData<MyState>(MyState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        val response = myRepository.getMyInfo()

        if (response.status == NetworkResult.Status.SUCCESS) {
            profileStateLiveData.value = MyState.Success(
                ProfileModel(
                    nickName = response.data!!.nickName,
                    profileImage = response.data!!.profileImage
                )
            )
            Log.e("profileImage: ", response.data.profileImage.toString())
        } else {
            profileStateLiveData.value = MyState.Error(
                code = response.code
            )
        }
    }

}