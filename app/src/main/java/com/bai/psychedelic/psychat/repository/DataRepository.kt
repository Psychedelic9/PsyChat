package com.bai.psychedelic.psychat.repository

import com.bai.psychedelic.psychat.data.entity.ResultEntity


class DataRepository{
    fun login(phone:String?, password:String?, success:(ResultEntity)->Unit,
              failed:((Throwable) -> Unit)? = null){
//        addDisposable(webService.login(phone,password)
//            .asyncSchedulers()
//            .subscribeOn(Schedulers.io())
//            .netSubscribe(success,failed)
//        )
    }



}