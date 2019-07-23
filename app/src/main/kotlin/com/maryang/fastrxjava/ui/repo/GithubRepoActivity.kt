package com.maryang.fastrxjava.ui.repo

import android.content.Context
import android.os.Bundle
import com.bumptech.glide.Glide
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.base.BaseActivity
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.event.DataObserver
import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.ui.user.UserActivity
import com.maryang.fastrxjava.util.LogoutEvent
import com.maryang.fastrxjava.util.Memory
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_github_repo.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk21.listeners.onClick
import java.util.concurrent.TimeUnit


class GithubRepoActivity : BaseActivity() {

    companion object {
        private const val KEY_REPO = "KEY_REPO"

        fun start(context: Context, repo: GithubRepo) {
            context.startActivity(
                context.intentFor<GithubRepoActivity>(
                    KEY_REPO to repo
                )
            )
        }
    }

    private val viewModel: GithubRepoViewModel by lazy {
        GithubRepoViewModel()
    }
    private lateinit var repo: GithubRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_repo)
        intent.getParcelableExtra<GithubRepo>(KEY_REPO).let {
            this.repo = it
            supportActionBar?.run {
                title = it.name
                setDisplayHomeAsUpEnabled(true)
            }
            showRepo(it)
            setOnClickListener()
        }
//        Memory.leakObservable(starCountLabel) // memory leak  테스트 가능.
//        EventBus.post(LogoutEvent()) // 에러 테스트 가능.
    }

    private fun showRepo(repo: GithubRepo) {
        Glide.with(this)
            .load(repo.owner.avatarUrl)
            .into(ownerImage)
        ownerName.text = repo.owner.userName
        starCount.text = repo.stargazersCount.toString()
        watcherCount.text = repo.watchersCount.toString()
        forksCount.text = repo.forksCount.toString()
        showStar(repo.star)
    }

    private fun setOnClickListener() {
        star.onClick { clickStar() }
        ownerImage.onClick { clickOwner() }
        ownerName.onClick { clickOwner() }
    }

    // 질문: rxbinding 을 사용하지 않고 view 중복 클릭을 방지하기 위해 throttle 을 잘 사용하는 방법이 궁금합니다
    // PublishSubject 는 View에서 사용하기에는 관심사가 분리되어 선호하지는 않음.
    val startOnClickSubject =  PublishSubject.create<Unit>()
    private fun throttleExample() {
        star.onClick { startOnClickSubject.onNext(Unit) }
        startOnClickSubject.debounce(400, TimeUnit.MILLISECONDS)
            .subscribe {
                clickStar()
            }
    }

    private fun clickStar() {
        repo.star.not().let {
            showStar(it) // 이런 기법들 많이 사용됨. UI 먼저 반영하고, ViewModel 에서 repo통해서 API
            starCount.text.toString().toInt().let { count ->
                starCount.text = (if (it) count + 1 else count - 1).toString()
            }
        }
        viewModel.onClickStar(repo)
            .subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    repo.apply {
                        star = !star
                    }.let {
                        showStar(it.star)
                        DataObserver.post(it)
                    }
                }

                override fun onError(e: Throwable) {
                    showStar(repo.star)
                    starCount.text = repo.stargazersCount.toString()
                }
            })
    }

    private fun clickOwner() {
        UserActivity.start(this, repo.owner)
    }

    private fun showStar(show: Boolean) {
        star.imageResource =
            if (show) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
    }
}
