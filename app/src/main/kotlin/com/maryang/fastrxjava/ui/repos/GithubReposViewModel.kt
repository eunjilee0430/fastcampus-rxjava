package com.maryang.fastrxjava.ui.repos

import com.maryang.fastrxjava.base.BaseViewModel
import com.maryang.fastrxjava.data.repository.GithubRepository
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.event.SearchEvent
import com.maryang.fastrxjava.util.applySchedulersExtension
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GithubReposViewModel : BaseViewModel() {
    private val repository = GithubRepository()
    private val searchSubject = PublishSubject.create<Pair<String, Boolean>>()
    var searchText = ""

    val eventDisposable = EventBus.observe()
        .throttleFirst(100, TimeUnit.MILLISECONDS)
        .subscribe {
            when (it) {
                is SearchEvent -> {
                    searchGithubRepos(it.searchText)
                }
            }
        }

    fun searchGithubRepos(search: String) {
        searchSubject.onNext(search to true)
    }

    fun searchGithubRepos() {
        searchSubject.onNext(searchText to false)
    }

    fun searchGithubReposSubject() =
        searchSubject
            .debounce(400, TimeUnit.MILLISECONDS)
            .doOnNext { searchText = it.first }
            .map { it.second }
            .observeOn(AndroidSchedulers.mainThread())

    fun searchGithubReposObservable() =
        Single.create<List<GithubRepo>> { emitter ->
            repository.searchGithubRepos(searchText)
                .subscribe({
                    Completable.merge(
                        it.map { repo ->
                            repository.checkStar(repo.owner.userName, repo.name)
                                .doOnComplete { repo.star = true }
                                .onErrorComplete()
                        }
                    ).subscribe {
                        emitter.onSuccess(it)
                    }
                }, {})
        }
            .applySchedulersExtension()
            .toObservable()


    // githubRepoViewModel과 동일한 메소드인데, 중복해서 가지고 있지 않을 수 있는 좋은 방법?
    fun onClickStar(repo: GithubRepo) =
        (if (repo.star)
            repository.unstar(repo.owner.userName, repo.name)
        else
            repository.star(repo.owner.userName, repo.name))
            .observeOn(AndroidSchedulers.mainThread())

}
