package com.maryang.fastrxjava.event

import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

object EventBus {
    private val bus = PublishSubject.create<Event>() // Any (Object) 받아도 되지만, 우리가 정의한 Event 라는 애만 받을 수 있게때문에 좋음.

    fun post(parameter: Event) {
        bus.onNext(parameter)
    }

    fun observe() =
        bus.toFlowable(BackpressureStrategy.BUFFER) // DataObserver와 차이점.. 어떤경우엔 얘 필요없음.
            .observeOn(AndroidSchedulers.mainThread())
}
