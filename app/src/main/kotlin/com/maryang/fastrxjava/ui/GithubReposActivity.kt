package com.maryang.fastrxjava.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_github_repos.*


class GithubReposActivity : AppCompatActivity() {

    private val viewModel: GithubReposViewModel by lazy {
        GithubReposViewModel()
    }
    private val adapter: GithubReposAdapter by lazy {
        GithubReposAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_repos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener { load() }

        load(true)
    }

    private fun load(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()

        viewModel.getGithubRepos().subscribe(object: DisposableSingleObserver<List<GithubRepo>>() {
            override fun onSuccess(t: List<GithubRepo>) {
                hideLoading()
                adapter.items = t
            }

            override fun onError(e: Throwable) {
                hideLoading()
            }

        })

    }

    private fun load2() {
        // Observable이 이벤트를 발행하면
        viewModel.getUser()
            .subscribe(
                // Observer가 무언가 를 수행한다.
                object: DisposableMaybeObserver<User>() { // Disposable == Subscription(Rx1), 구독해제 (ex. User가 뒤로가기 버튼 클릭)
                override fun onSuccess(t: User) {
                    // null이 아니라 제대로 왔을 떄 불리는 곳
                    hideLoading()

                }

                override fun onComplete() {
                    // null이 왔을 떄 불리는 곳 (onComplete)
                    hideLoading()
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                }

        })
    }

    private fun load3() {
        viewModel.updateUser().subscribe(object: DisposableCompletableObserver() {
            override fun onComplete() {
                hideLoading()
            }
            override fun onError(e: Throwable) {
                hideLoading()
            }
        })
    }


    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }



    // Back버튼을 누르면, 액티비티는 종료된다.
    // 액티비티에 있는 Observable을 구독하는 Observer는 Activity의 Context를 참조한다. ( View건듬 )
    // Activity는 종료가 되어야하는데, Dispose되지 않은 Observer에 Context가 잡혀있어, 사라지지 않음.  (GC가 안잡아감 -> 메모리 누수)
    
    // 강의 내용 코드.
    private fun lectionContents() {

        // map = element들을 순회하면서 변경한다.
        val numbers = listOf(1,2,3)
        numbers.map{
            it + 3
        }
        numbers.flatMap {
            listOf(4,5,6)
        }

        viewModel.getGithubRepos().toMaybe() // Single -> Maybe: onComplete로 내려올 일 전혀 없음.
            .flatMap { viewModel.getUser() }.subscribe()

        viewModel.getGithubRepos()
            .flatMap { viewModel.getUser().toSingle() }.subscribe() // Maybe -> Single: Exception의 가능성이 있음.
                                                                    // But 절대 null로 안내려온다면 이렇게 써도됨.

        viewModel.getGithubRepos() // Schedulers.io()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                // 보통 이 위치에서 UI 작업은 권장되지 않음.
            }
            .observeOn(Schedulers.newThread())
            .flatMap { viewModel.getUser().toSingle() } // newThread()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


        viewModel.getGithubRepos().toMaybe()
            .doOnSuccess {
                // getGithubRepos 종료되면 로그 불림
            }
            // getUser()불름
            .flatMap { viewModel.getUser() }
            .doOnSuccess {
                // getUser() 종료되면 로그 불림
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }
}
