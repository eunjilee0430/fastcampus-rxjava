package com.maryang.fastrxjava.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.util.ErrorHandler
import com.maryang.fastrxjava.util.LogoutEvent
import io.reactivex.plugins.RxJavaPlugins
import org.jetbrains.anko.toast

class BaseApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
        const val TAG = "FastRxJava2"
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        Stetho.initializeWithDefaults(this)
        setErrorHandler()
    }


    // 글로벌한 default 에러 핸들러.
    // onError() 가 없거나, onError()에서 또 Exceptieon이 나면 들어옴.
    private fun setErrorHandler() {
        RxJavaPlugins.setErrorHandler {

            ErrorHandler.handle(it)

        }

        EventBus.observe()
            .subscribe {
                when(it) {
                    is LogoutEvent -> {
                        // do logout : 다 날리고, 로그인 화면으로 이동 등.
                        toast("에러났음")
                    }
                }
            }
    }
}
