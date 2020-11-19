package com.zhongyou.meet.mobile.base

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.zhongyou.meet.mobile.BR
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.utils.ToastUtils
import io.reactivex.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.jetbrains.anko.info

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/3 10:39 AM.
 * @
 */
class DemoViewModel(application: Application) : BaseViewModel(application) {
    val datalists = ObservableArrayList<PPT>()

    lateinit var onItemClickListener: OnItemClickListener<PPT>

    val itemBinding = ItemBinding.of<PPT>(BR.item, R.layout.main_item)
            .bindExtra(BR.listener, onItemClickListener)


    val adapter = BindingRecyclerViewAdapter<PPT>()
    /* fun getListMutableLiveData(): MutableLiveData<List<PPT>> {
         return datalists
     }*/


    fun getData() {

        onItemClickListener = object : OnItemClickListener<PPT> {
            override fun onItemClick(t: PPT) {
                ToastUtils.showToast(t.meetingName)
            }
        }

        mApiService.getAllPPT("41cca7acfe2741c8806762a9833eec16").compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
//                        super.onSubscribe(d)
                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                val jsonArray = t.getJSONObject("data").getJSONArray("pageData")
                                val parseArray = JSON.parseArray(jsonArray.toJSONString(), PPT::class.java)
                                datalists.addAll(parseArray)

                            }
                        }

                    }
                })
    }


    interface OnItemClickListener<T> {
        fun onItemClick(t: T)
    }

}