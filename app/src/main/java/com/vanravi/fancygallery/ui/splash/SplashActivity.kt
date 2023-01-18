package com.vanravi.fancygallery.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi
import com.vanravi.fancygallery.R
import com.vanravi.fancygallery.base.BaseActivity
import com.vanravi.fancygallery.databinding.ActivitySplashBinding
import com.vanravi.fancygallery.ui.main.MainActivity

@ExperimentalPagingApi
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    private val splashViewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun getVM(): SplashViewModel = splashViewModel



    override fun bindVM(binding: ActivitySplashBinding, vm: SplashViewModel) {
        vm.initSplashScreen()
        val observer = Observer<Any> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        splashViewModel.liveData.observe(this, observer)
    }

    override fun setBinding(): ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
}