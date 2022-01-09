package com.example.bookwhale.screen.main.my

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.main.my.MyRepository
import com.example.bookwhale.model.main.my.ProfileModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyViewModel(
    private val myRepository: MyRepository
): BaseViewModel() {

    val profileInfo = MutableLiveData<ProfileModel>()

    init {
        Log.e("myViewModel","myViewModel")
    }

    override fun fetchData(): Job = viewModelScope.launch {
        val result = myRepository.getProfile()

        profileInfo.value = ProfileModel(
            profileName = result.profileName,
            profileImage = result.profileImage
        )

    }

}