package com.maryang.fastrxjava.ui.repos

import android.view.SearchEvent
import com.maryang.fastrxjava.base.BaseViewModel
import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.util.applySchedulersExtension
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GithubReposViewModel {
    private val repository = GithubRepository()
    private val searchSubject = PublishSubject.create<Pair<String, Boolean>>()
    var searchText = ""

    fun searchGithubRepos(search: String) {
        searchSubject.onNext(search to true)
    }

    fun searchGithubRepos() {
        searchSubject.onNext(searchText to false)
    }

    fun searchGithubReposSubject() =
        searchSubject
            .debounce(400, TimeUnit.MILLISECONDS) // 400ms -> -> 검색  -> -> -> 뷰에 보여줌.
                                                  //        다른 검색어 검색 --> -->  마지막 값만 쓰고싶으면 switchMap() 쓰면됨. (flatMap이 아닌)
                                                  // concatMap() : 순서를 보장함.
            .doOnNext { searchText = it.first }
            .map { it.second }
            .observeOn(AndroidSchedulers.mainThread())

    fun searchGithubReposObservable() =
        Single.create<List<GithubRepo>> { emitter -> // block을 넣고, param emitter
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            repository.checkStar(repo.owner.userName, repo.name) // 스타가 없으면, 실패함. (설계가 그렇게 되어있음...)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete() // onErorr 시, 스트림이 깨짐. -> 이후 전체 스트림 동작하지 않음 -> 에러가 일어나도, 잘 처리해서 스트림을 유지하고 싶음.
                                                    //
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .applySchedulersExtension()
            .toObservable()



//    val eventDisposable1 = EventBus.observe()
//        .doOnNext {
//            // doONNExt: 에서 로직이 들어가는게 좋을까? 는 모르겟므 subscribe 가2개로 나누는것도 ...?
//            when (it) {
//                is SyncFinish -> {
//                    searchGithubRepos()
//                }
//            }
//        }
//        .throttleFirst(1000, TimeUnit.MILLISECONDS)
//        .subscribe {
//            when (it) {
//                is SearchEvent -> {
//                    searchGithubRepos(it.searchText)
//                }
//            }
//        }
//}
//
//
//val eventDisposable1 = EventBus.observe()
//    .throttleFirst(1000, TimeUnit.MILLISECONDS)
//    .subscribe {
//        when (it) {
//            is SearchEvent -> {
//                searchGithubRepos(it.searchText)
//            }
//        }
//    }
//
//
//val eventDisposable2 = EventBus.observe()
//    .subscribe {
//        when (it) {
//            is SyncFiish -> {
//                searchGithubRepos()
//            }
//            is SearchEvent -> {
//                searchGithubRepos(it.searchText)
//            }
//        }
//    }

}
