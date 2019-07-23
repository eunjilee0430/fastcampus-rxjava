package com.maryang.fastrxjava.ui.repos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.base.BaseActivity
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.DataObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_github_repos.*

class GithubReposActivity : BaseActivity() {


    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.maryang.fastrxjava.R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { viewModel.searchGithubRepos() }

        searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                viewModel.searchGithubRepos(text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        subscribeSearch()
        subscribeDataObserver()
    }

//    private var searchDisposable: Disposable? = null
//    private var dataDisposable: Disposable? = null
//    private val comm = CompositeDisposable() // CompositeDisposable 만들고, 한번에 dispose()

    private fun subscribeSearch() {
        compositeDisposable += viewModel.searchGithubReposSubject()
            .doOnNext {
                if (it) showLoading()
            }
            .switchMap { viewModel.searchGithubReposObservable() }
            .subscribeWith(object : DisposableObserver<List<GithubRepo>>() { // observer 를 넣으면 void   ->   subscribeWith 를 사용하자.
                override fun onNext(t: List<GithubRepo>) {
                    hideLoading()
                    adapter.items = t
                }

                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                }
            })
    }

    private fun subscribeDataObserver() {
        compositeDisposable += DataObserver.observe()
            .filter { it is GithubRepo }
            .subscribe { repo ->                            // consumer 를 넣으면 Disposable
                adapter.items.find {
                    it.id == repo.id
                }?.apply {
                    star = star.not()
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
//        searchDisposable?.dispose()
//        dataDisposable?.dispose()

//        comm.dispose() // BaseActivity에서 수행함.
    }
}
