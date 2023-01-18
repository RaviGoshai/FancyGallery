package com.vanravi.fancygallery.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanravi.fancygallery.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel :  BaseViewModel() {
    var liveData: MutableLiveData<Any> = MutableLiveData()

    fun initSplashScreen() {
        viewModelScope.launch {
            delay(5000)
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        liveData.value = "finish"
    }
}