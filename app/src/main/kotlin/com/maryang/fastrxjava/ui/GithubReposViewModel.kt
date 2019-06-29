package com.maryang.fastrxjava.ui

import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.data.source.DefaultCallback
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.User
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response

class GithubReposViewModel {
    private val repository = GithubRepository()

    // ASIS: 람다 등으로 직접 명령을 받아서 얘를 해를 넣어줬어.
    // TOBE: 나는 몰라. 누가 받아서 하겠지 (의존성을 한번 끊어줌)
    //          받아서 뭐해야지~ 라는 생각을 끊자!!!
    fun getGithubRepos() : Single<List<GithubRepo>> =     // Single에는 enqueue가 없음.
        repository.getGithubRepos()
            .subscribeOn(Schedulers.io()) //
            .doOnSuccess { // Schedulers.io()

            }
            .observeOn(AndroidSchedulers.mainThread())


    fun getUser() : Maybe<User> =
        repository.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateUser() : Completable =
            repository.updateUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
}