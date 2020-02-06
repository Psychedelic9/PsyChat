package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel:ViewModel(){

    val splashText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    fun setSplashText(str:String?){
        splashText.value = str
    }

}