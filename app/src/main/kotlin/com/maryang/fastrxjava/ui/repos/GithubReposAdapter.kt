package com.maryang.fastrxjava.ui.repos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maryang.fastrxjava.R
import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.ui.repo.GithubRepoActivity
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.android.synthetic.main.item_github_repo.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk21.listeners.onClick

class GithubReposAdapter(val viewModel : GithubReposViewModel) : RecyclerView.Adapter<GithubReposAdapter.RepoViewHolder>() {

    var items: List<GithubRepo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder =
        RepoViewHolder(
                LayoutInflater.from(parent.context)
                .inflate(R.layout.item_github_repo, parent, false)
        )

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        items[position].let { repo ->
            with(holder.itemView) {

                fun showStar() {
                    if (repo.star) repoStar.imageResource = R.drawable.baseline_star_24
                    else repoStar.imageResource = R.drawable.baseline_star_border_24
                }

                repoName.text = repo.name
                repoDescription.text = repo.description
                showStar()
                onClick { GithubRepoActivity.start(context, repo) }

                repoStar.onClick {
                    viewModel.onClickStar(repo)
                        .subscribe(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                repo.apply {
                                    star = !star
                                }.let {
                                    showStar()
                                }
                            }

                            override fun onError(e: Throwable) {
                                showStar()
                            }
                        })
                }

            }

        }


    }

    override fun getItemCount(): Int = items.size

    class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
