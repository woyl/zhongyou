package com.zhongyou.meet.mobile.base

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 1:11 PM.
 * @
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application), AnkoLogger {


    val disposableLists = ArrayList<Disposable>()


    fun addDisposable(disposable: Disposable) {
        disposableLists.add(disposable)
    }

    protected val mApiService: ApiService by lazy {
        HttpsRequest.provideClientApi()
    }

    open fun launchActivity(intent: Intent) {
        Preconditions.checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    public override fun onCleared() {
        super.onCleared()
        for (disposable in disposableLists) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
    }

}