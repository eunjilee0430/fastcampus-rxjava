package com.maryang.fastrxjava.ui.repos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.base.BaseViewModelActivity
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.DataObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_github_repos.*
import android.widget.AdapterView
import org.jetbrains.anko.toast


class GithubReposActivity : BaseViewModelActivity() {

    override val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }

    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter(viewModel)
    }

    private var sorted = "[SORT]"
    private var order = "[ORDER]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.maryang.fastrxjava.R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { viewModel.searchGithubRepos() }

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sorted = sortSpinner.getItemAtPosition(position).toString()
                toast(sorted)
                viewModel.searchGithubRepos()
            }
        }

        orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                order = orderSpinner.getItemAtPosition(position).toString()
                toast(order)
                viewModel.searchGithubRepos()
            }
        }

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

    private fun subscribeSearch() {
        compositeDisposable += viewModel.searchGithubReposSubject()
            .doOnNext {
                if (it) showLoading()
            }
            .switchMap { // switchMap: 순서보장 (기존 작업 중단), 여러개 값 들어올 때 마지막값만 처리하고 싶을 때.
                if(sorted == "[SORT]" ||  order == "[ORDER]") viewModel.searchGithubReposObservable()
                else viewModel.searchSortedGithubReposObservable(sorted, order)
            }
            .subscribeWith(object : DisposableObserver<List<GithubRepo>>() {
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
            .subscribe { repo ->
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
}
