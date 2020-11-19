/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyou.meet.mobile.config;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.ChairManActivity;
import com.zhongyou.meet.mobile.ameeting.MakerChairManActivity;
import com.zhongyou.meet.mobile.ameeting.MakerMeetingAudienceActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseVideoDetailActivity;

import java.util.Objects;

import io.agora.openlive.ui.MeetingAudienceActivity;
import io.agora.openlive.ui.MeetingInitActivity;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link Application.ActivityLifecycleCallbacks} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:14
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {


	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		Timber.i("------%s - onActivityCreated------", activity);
		if (activity != null) {
			if (activity instanceof ChairManActivity
					|| activity instanceof MeetingAudienceActivity
					|| activity instanceof MeetingInitActivity
					|| activity instanceof MakerChairManActivity
					|| activity instanceof MakerMeetingAudienceActivity
					|| activity instanceof MakerCourseVideoDetailActivity) {
				return;
			}

			ImmersionBar.with(activity).statusBarColor(R.color.white)
					.statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
					.flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
					.navigationBarColor(R.color.white) //导航栏颜色，不写默认黑色
					.navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
					.fitsSystemWindows(true)
					.init();
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		Timber.i("------%s - onActivityStarted------", activity);
		if (!activity.getIntent().getBooleanExtra("isInitToolbar", false)) {
			//由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
			//而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
			activity.getIntent().putExtra("isInitToolbar", true);
			//这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
			if (activity.findViewById(R.id.toolbar) != null) {
				if (activity instanceof AppCompatActivity) {
					((AppCompatActivity) activity).setSupportActionBar(activity.findViewById(R.id.toolbar));
					Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).setDisplayShowTitleEnabled(false);
				} else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						activity.setActionBar(activity.findViewById(R.id.toolbar));
						Objects.requireNonNull(activity.getActionBar()).setDisplayShowTitleEnabled(false);
					}
				}
			}
			if (activity.findViewById(R.id.toolbar_title) != null) {
				((TextView) activity.findViewById(R.id.toolbar_title)).setText(activity.getTitle());
			}
			if (activity.findViewById(R.id.toolbar_back) != null) {
				activity.findViewById(R.id.toolbar_back).setOnClickListener(v -> activity.onBackPressed());
			}
		}

		//如果页面上有下拉刷新和加载更多 直接设置header 并且刷新的id 要为refreshLayout
		if (activity.findViewById(R.id.refreshLayout) != null) {
			if (activity.findViewById(R.id.refreshLayout) instanceof SmartRefreshLayout) {
				SmartRefreshLayout refreshLayout = activity.findViewById(R.id.refreshLayout);
				refreshLayout.setRefreshHeader(new MaterialHeader(activity));
			}
		}
	}


	@Override
	public void onActivityResumed(Activity activity) {
		Timber.i("------%s - onActivityResumed------", activity);


	}

	@Override
	public void onActivityPaused(Activity activity) {
		Timber.e("------%s - onActivityPaused------", activity);
//		if (mActivity == activity) {
//			activityIsStop = true;
//		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		Timber.e("------%s - onActivityStopped------", activity);


	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		Timber.i("------%s - onActivitySaveInstanceState------", activity);
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		Timber.i("------%s - onActivityDestroyed------", activity);
		//横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作


	}
}