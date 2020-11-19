package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxBus
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.di.component.DaggerMyNewComponent
import com.zhongyou.meet.mobile.di.module.MyNewModule
import com.zhongyou.meet.mobile.event.LikeEvent
import com.zhongyou.meet.mobile.event.UnlockEvent
import com.zhongyou.meet.mobile.mvp.contract.MyNewContract
import com.zhongyou.meet.mobile.mvp.presenter.MyNewPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.*
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.utils.StatusBarUtils
import kotlinx.android.synthetic.main.fragment_my_new.*


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/02/2020 11:31
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class MyNewFragment : BaseFragment<MyNewPresenter>(), MyNewContract.View{

    companion object {
        fun newInstance(): MyNewFragment {
            return MyNewFragment()
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerMyNewComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myNewModule(MyNewModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_my_new, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (StatusBarUtils.hasNotchInScreen(activity)) {
            setStatusHeight(StatusBarUtils.getNotchSizeAtHuawei(mContext))
        }
        refreshLayout?.setOnRefreshListener {
            mPresenter?.getUnReadMessage()
        }

        RxBus.handleMessage { o ->
            if (o is LikeEvent) {
                if (o.isLike) {
                    mPresenter?.getUnReadMessage()
                }
            } else if (o is UnlockEvent) {
                val isUnlock = o as UnlockEvent
                if (isUnlock.isLock) {
                    refreshLayout?.autoRefresh()
                }
            }
        }
        setOnclick()
    }

    private fun setStatusHeight(height:Int) {
        val layoutParams = constraint_head.layoutParams as ConstraintLayout.LayoutParams
		layoutParams.setMargins(0,StatusBarUtils.getStatusBarHeight(mContext)+height,0,0)
        constraint_head.layoutParams = layoutParams
    }

    private fun setOnclick() {
        my_course.setOnClickListener {
            launchActivity(Intent(context, MyCourseActivity::class.java))
        }

        my_class_room.setOnClickListener {
            launchActivity(Intent(context, MyClassRoomActivity::class.java))
        }

        constraint_my_message.setOnClickListener {
            launchActivity(Intent(context, MessageDetailActivity::class.java))
        }

        system_set.setOnClickListener {
            launchActivity(Intent(context, SystemSettingActivity::class.java))
        }

        my_contact_us.setOnClickListener {
            launchActivity(Intent(context, ContactUsActivity::class.java))
        }
        constraint_head.setOnClickListener {
            launchActivity(Intent(context, AccountSettingActivity::class.java))
        }
    }

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     *fun setData(data:Any) {
     *   if(data is Message){
     *       when (data.what) {
     *           0-> {
     *               //根据what 做你想做的事情
     *           }
     *           else -> {
     *           }
     *       }
     *   }
     *}
     *
     *
     *
     *
     *
     * // call setData(Object):
     * val data = Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    override fun setData(data: Any?) {

    }

    override fun showRedDot(isShow: Boolean, count: Int) {
        refreshLayout?.finishRefresh()
        red_dotone?.visibility = if (isShow) View.VISIBLE else View.GONE
        if (count > 99) {
            red_dotone?.text = "99+"
        } else {
            red_dotone?.text = "$count"
        }

    }

    override fun showLoading() {

    }

    override fun hideLoading() {
        refreshLayout?.finishRefresh()
        mPresenter?.getUnReadMessage()
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "")) && mContext != null) {
            //head
            Glide.with(this).load(MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "")).circleCrop().placeholder(R.drawable.defaule_banner).error(R.drawable.defaule_banner).into(img_head)
            tv_name.text = MMKV.defaultMMKV().getString(MMKVHelper.USERNICKNAME, "")
            tv_user_type.text = MMKV.defaultMMKV().getString(MMKVHelper.POSTTYPENAME, "")
        }
        mPresenter?.getUnReadMessage()
    }
}
