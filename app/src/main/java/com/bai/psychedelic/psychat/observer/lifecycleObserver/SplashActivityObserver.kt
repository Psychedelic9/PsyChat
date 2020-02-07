package com.bai.psychedelic.psychat.observer.lifecycleObserver

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class SplashActivityObserver constructor(context: Context): LifecycleObserver{
    private val mContext:Context = context

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setListener(){

    }
}