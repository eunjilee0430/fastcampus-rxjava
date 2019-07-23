package com.maryang.fastrxjava.util

import android.util.Log
import android.widget.TextView
import com.maryang.fastrxjava.base.BaseApplication
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


object Memory {
    // observer 객체가 TextView 객체를 들고있다.
    // Activity 또는 View 또는 Fragment 등등의 View가 TextView를 들고있다.
    // Activity -> TextView
    // Observer -> TextView
    // Activity가 종료되면, TextView도 종료되어야하는데,
    // Observer 가 TextView를 들고있고, TextView를 바라보는 Activity도 안죽는다.
    // cf) GC 는 참조하는애가 없으면 동작함.
    fun leakObservable(text: TextView) {
        Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { x ->
                Log.d(BaseApplication.TAG, "leakObservable $x")
                text.text = x.toString()
            }
    }
}
