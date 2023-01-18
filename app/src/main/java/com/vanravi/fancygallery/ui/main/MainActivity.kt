package com.vanravi.fancygallery.ui.main

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanravi.fancygallery.base.BaseActivity
import com.vanravi.fancygallery.data.model.ImageListItem
import com.vanravi.fancygallery.databinding.ActivityMainBinding
import com.vanravi.fancygallery.databinding.ItemImageBinding
import com.vanravi.fancygallery.ui.detail.FullScreenActivity
import com.vanravi.fancygallery.ui.main.adapter.ImageAdapter
import com.vanravi.fancygallery.ui.main.adapter.PagingLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalPagingApi
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    ImageAdapter.ImageItemClickListener {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var imageAdapter: ImageAdapter

    override fun getVM(): MainViewModel = mainViewModel

    override fun bindVM(binding: ActivityMainBinding, vm: MainViewModel) =
        with(binding) {
            val rLayoutManager = LinearLayoutManager(this@MainActivity)
            with(imageAdapter) {
                rvImages.apply {
                    postponeEnterTransition()
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                    layoutManager = rLayoutManager
                }
                rvImages.adapter = withLoadStateHeaderAndFooter(
                    header = PagingLoadStateAdapter(this),
                    footer = PagingLoadStateAdapter(this)
                )

                swipeRefreshLayout.setOnRefreshListener { refresh() }
                imageClickListener = this@MainActivity

                with(vm) {
                    launchOnLifecycleScope {
                        imageResponse.collectLatest { submitData(it) }
                    }
                    launchOnLifecycleScope {
                        loadStateFlow.collectLatest {
                            swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                        }
                    }
                }

                launchOnLifecycleScope {
                    imageAdapter.loadStateFlow.collect { loadState ->
                        // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                        val errorState = loadState.source.append as? LoadState.Error
                            ?: loadState.source.prepend as? LoadState.Error
                            ?: loadState.append as? LoadState.Error
                            ?: loadState.prepend as? LoadState.Error
                            ?: loadState.source.refresh as? LoadState.Error
                        errorState?.let {
                            Toast.makeText(
                                this@MainActivity,
                                "\uD83D\uDE28 Wooops ${it.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onImageClicked(binding: ItemImageBinding, item: ImageListItem) {
        //Navigate to detail
        val intent = Intent(this@MainActivity, FullScreenActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }
}