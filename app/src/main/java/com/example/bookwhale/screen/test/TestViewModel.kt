package com.example.bookwhale.screen.test

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TestViewModel(): BaseViewModel() {

    val testLiveData = MutableLiveData<String>()

    override fun fetchData(): Job = viewModelScope.launch {
        testLiveData.value = "안녕하세요"
    }
}

