package com.maryang.fastrxjava.util

import android.util.Log
import com.maryang.fastrxjava.base.BaseApplication
import com.maryang.fastrxjava.observer.DefaultSingleObserver
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver

object Error {

    fun error() {
        Observable.error<Unit>(IllegalStateException())
            .subscribe({}, {
                Log.d(BaseApplication.TAG, "error $it")
            })
    }

    fun error2(){
        Single.error<Unit>(IllegalStateException()) // error가 나면 Unit 도 안나오겠지만, 일단 Single은 1개를 반환해야하므로.
            .subscribe(object: DefaultSingleObserver<Unit>(){ // 에러처리시는 얘를 추천함! 왜냐하면, 에러처리를 더 안전하고 적은 코드로 할 수 있음. (DefaultSingleObserver)
                override fun onSuccess(t:Unit){
                }
                override fun onError(e:Throwable){
                    super.onError(e) // 얘 무조건 호출.
                    // 에러를 받는다.
                }
            });


        Single.error<Unit>(java.lang.IllegalStateException())
            .subscribe({
                // onSuccess
            }, {
                // onError
                ErrorHandler.handle(it)
            })
    }




   fun errorExample() {
       Single.error<Boolean>(IllegalStateException())
           .onErrorResumeNext { // 에러가 일어라면, 스트림 망가뜨리지 말고, 이값으로 변경하여 계쏙 진행해라~~. -> onSuccess()
               Single.just(true)
           }
           .onErrorReturn { // onErrorResumeNext와 동일.
                true
           }

//       Completable.error(IllegalStateException())
//           .onErrorComplete {
//               // onComplete() 로 보냄.
//           }
//           . subscribe()
   }
}

// method가 하나밖에 없는 인터페이스.

// 컨슈머는 람다 자체를
