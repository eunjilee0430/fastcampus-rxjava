package com.maryang.fastrxjava.observer

import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.util.ErrorHandler
import com.maryang.fastrxjava.util.LogoutEvent
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.HttpException

abstract  class DefaultSingleObserver<T> : DisposableSingleObserver<T>() {

    override fun onError(e: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        ErrorHandler.handle(e)

         if(e is HttpException) {

             // api에서 401은 토큰이 만료된 애.
             if(e.code() == 401) {
                 EventBus.post(LogoutEvent()) // 특이 케이스에 보내게됨.
             }
         }

    }

}