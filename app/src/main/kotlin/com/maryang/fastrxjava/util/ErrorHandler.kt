package com.maryang.fastrxjava.util

import com.maryang.fastrxjava.BuildConfig
import com.maryang.fastrxjava.event.EventBus
import retrofit2.HttpException

// 어떤 에러든 여기를 탄다.
object ErrorHandler {
    fun handle(t: Throwable) {
        if(BuildConfig.DEBUG) {
            t.printStackTrace()
        }
        if(t is HttpException) {
            if(t.code() == 401) {
                EventBus.post(LogoutEvent())  // 전역적인 처리가 많은 케이스는 많지 않을 것.
            }
        }
    }
}