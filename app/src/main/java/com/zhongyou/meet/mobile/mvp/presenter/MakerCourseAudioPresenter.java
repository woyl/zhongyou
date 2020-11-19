package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxBus;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.CommonUtils;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.core.MusicManager;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.entiy.MoreAudio;
import com.zhongyou.meet.mobile.event.AdapaterRefushEvent;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MoreDetailsActivity;
import com.zhongyou.meet.mobile.receiver.MoreMakerCourseAudioReciver;
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener;
import com.zhongyou.meet.mobile.view.CompletedProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/08/2020 13:43
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MakerCourseAudioPresenter extends BasePresenter<MakerCourseAudioContract.Model, MakerCourseAudioContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private int clickPosition = -1;
//    private MusicInfoReceiver receiver;

    private MoreMakerCourseAudioReciver moreMakerCourseAudioReciver;

    private static PlayService.MusicBinder musicBinder;
    List<MoreAudio> moreAudios = new ArrayList<>();

    private BaseRecyclerViewAdapter<MoreAudio> mAdapter;
    private boolean isPlayAll = false;
    private static PlayService.MusicBinder musicBinderAll;
    private int oldPosition = -1;

    @Inject
    public MakerCourseAudioPresenter(MakerCourseAudioContract.Model model, MakerCourseAudioContract.View rootView) {
        super(model, rootView);
    }


    public int getCurrentPosition() {
        return clickPosition;
    }

    public void setCurrentPosition(int poi) {
        clickPosition = poi;
    }

    public void setOldPosition(int position) {
        oldPosition = position;
        mAdapter.notifyItemChanged(position);
    }

    public BaseRecyclerViewAdapter<MoreAudio> initAudioRecyclerAdapter(PlayService.MusicBinder musicBinder1, final Animation mOperatingAnim, MakerCourseAudioActivity audioActivity, MakerCourseAudioPresenter makerCourseAudioPresenter) {

        musicBinder = musicBinder1;
        if (musicBinder.isPlaying()) {
            clickPosition = musicBinder.getPosition();
        }

        mAdapter = new BaseRecyclerViewAdapter<MoreAudio>(mApplication, mRootView.getData(), R.layout.item_maker_course_audio_more) {

            @Override
            public void convert(BaseRecyclerHolder holder, MoreAudio item, int position, boolean isScrolling) {

                TextView recommendDate = holder.getView(R.id.recommendDate);
                View topView = holder.getView(R.id.view_1);
                View tv_point = holder.getView(R.id.tv_point);
                AppCompatImageView img_play_m_all = holder.getView(R.id.img_play_m_all);
                TextView textViewName =holder.getView(R.id.className);

                Timber.e("1111  " + position + "   item.getDate()---->%s ---%s", item.getDate(), item.getName());
                if (position == 0) {
                    topView.setVisibility(View.GONE);
                    img_play_m_all.setVisibility(View.VISIBLE);
                    recommendDate.setText(item.getDate());
                    recommendDate.setVisibility(View.VISIBLE);
                    tv_point.setVisibility(View.VISIBLE);
                } else {
                    topView.setVisibility(View.VISIBLE);
                    img_play_m_all.setVisibility(View.GONE);
                }

                if (position > 0) {
                    if (!item.getDate().equals(mRootView.getData().get(position - 1).getDate())) {
                        recommendDate.setText(item.getDate());
                        recommendDate.setVisibility(View.VISIBLE);
                        tv_point.setVisibility(View.VISIBLE);
                    } else {
                        recommendDate.setVisibility(View.GONE);
                        tv_point.setVisibility(View.GONE);
                    }
                }


//				BubbleSeekBar seekBar = holder.getView(R.id.seekBar);
//				seekBar.getConfigBuilder().max(item.getTotalTime()).progress((int) item.getCurrentDuration()).build();
//				if (item.getType() == 0) {
//					seekBar.setVisibility(View.GONE);
//				} else if (seekBar.getVisibility() != View.VISIBLE) {
//					seekBar.setVisibility(View.GONE);
//				}

//				if (position == mRootView.getData().size() - 1) {
//					holder.getView(R.id.line).setVisibility(View.GONE);
//				}

//				if (item.isExpanded()) {
//					if (holder.getView(R.id.descriptionImage).getVisibility() != View.VISIBLE) {
//						holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
//						Glide.with(mApplication).load(item.getComentURL()).skipMemoryCache(false).into((ImageView) holder.getView(R.id.descriptionImage));
//					}
//				} else {
//					if (holder.getView(R.id.descriptionImage).getVisibility() != View.GONE) {
//						holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
//					}
//				}


                CompletedProgressView completedProgressView = holder.getView(R.id.circle_progress);
                completedProgressView.setTag(position);
                if (item.getTotalTime() != 0 && item.getCurrentDuration() != 0) {
                    int pro = (int) (item.getCurrentDuration() / (item.getTotalTime() / 100));
                    completedProgressView.setProgress(pro);
                } else {
                    completedProgressView.setProgress(0);
                }

				if (musicBinder.isPlaying() && MusicManager.Companion.getInstance().getPosition() == position) {
                    textViewName.setTextColor(ContextCompat.getColor(mApplication,R.color.c3a8ae6));
                } else {
                    textViewName.setTextColor(ContextCompat.getColor(mApplication,R.color.black));
				}

                if (oldPosition == position) {
                    mRootView.getData().get(position).setType(0);
                    completedProgressView.setProgress(0);
                }

                if (mRootView.getData().get(position).getType() == 1 && musicBinder.isPlaying()) {
//					holder.setImageResource(R.id.audioState, R.drawable.icon_audio_pause);
                    holder.setImageResource(R.id.img_play_button, R.mipmap.play_music);
                } else if (mRootView.getData().get(position).getType() == 3) {
//					holder.setImageResource(R.id.audioState, R.drawable.icon_audio_loading);

//					mOperatingAnim = AnimationUtils.loadAnimation(mApplication, R.anim.tip);
//					LinearInterpolator lin = new LinearInterpolator();
//					mOperatingAnim.setInterpolator(lin);
//					holder.getView(R.id.audioState).startAnimation(mOperatingAnim);

                } else {
//					holder.setImageResource(R.id.audioState, R.drawable.audio);
                    holder.setImageResource(R.id.img_play_button, R.mipmap.pause_music);
                }

                if (isPlayAll) {
                    holder.setImageResource(R.id.img_play_m_all, R.mipmap.playing_m);
                } else {
                    holder.setImageResource(R.id.img_play_m_all, R.mipmap.pause_m);
                }

                if (!musicBinder.getMoreDetails()) {
                    isPlayAll = false;
                    holder.setImageResource(R.id.img_play_m_all, R.mipmap.pause_m);
                }


                holder.setText(R.id.className, item.getName());

                //holder.setText(R.id.teacherName, "主讲人:" + item.getAnchorName());
                //屏蔽 主讲人 文本
                holder.setText(R.id.teacherName, item.getAnchorName());

                holder.setText(R.id.classDate, item.getBelongTime() + "  |  ");

                holder.setText(R.id.classTime, CommonUtils.getFormattedTime(item.getCurrentDuration()) + "/" + item.getTotalDate());

                holder.getView(R.id.fl_aniu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oldPosition = -1;
                        musicBinder.setOldPosition(oldPosition);
                        isPlayAll = false;
                        musicBinder.setMore(false);
                        musicBinder.setMoreDetails(false);
                        playItemVideo(item, position);
                        notifyDataSetChanged();
                        if (makerCourseAudioPresenter != null) {
                            makerCourseAudioPresenter.click(item.getId());
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickPosition = -1;
                        audioActivity.startActivity(new Intent(audioActivity, MoreDetailsActivity.class)
                                .putExtra("name", item.getName())
                                .putExtra("belongTime", item.getBelongTime())
                                .putExtra("author", item.getAnchorName())
                                .putExtra("url", item.getComentURL())
                                .putExtra("video", item.getVioceURL())
                                .putExtra("position", position));

                    }
                });
                holder.getView(R.id.img_play_m_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oldPosition = -1;
                        musicBinder.setOldPosition(oldPosition);
                        musicBinder.setMore(true);
                        musicBinder.setMoreDetails(true);
                        if (musicBinder.isPlaying()) {
                            isPlayAll = false;
                            musicBinder.pause();
                            if (musicBinder.getPosition() < mRootView.getData().size()) {
                                mRootView.getData().get(musicBinder.getPosition()).setType(2);
                            }
                        } else {
                            isPlayAll = true;
                            if (mRootView.getData() != null && mRootView.getData().size() > 0) {
                                int currPosition = getCurrentPosition() != -1 ? getCurrentPosition() : 0;
                                playItemVideo(item, currPosition);
                            }
                        }

//						playItemVideo(item,position);
                        notifyDataSetChanged();
                    }
                });
            }
        };

        return mAdapter;
    }

    private void playItemVideo(MoreAudio item, int position) {
        if (null == item.getVioceURL()) {
            return;
        }
        if (clickPosition == -1) {
            playMusic(mRootView.getData(), position, item.getVioceURL(), 1);
        } else {
            if (position == clickPosition) {
                if (mRootView.getData().get(position).getType() == 0) {
                    playMusic(mRootView.getData(), position, item.getVioceURL(), 1);
                } else if (mRootView.getData().get(position).getType() == 1) {
                    musicBinder.pause();
                    mRootView.getData().get(position).setType(2);
                } else if (mRootView.getData().get(position).getType() == 2) {
                    musicBinder.start();
                    mRootView.getData().get(position).setType(1);
                }
            } else {
                if (clickPosition < mRootView.getData().size()) {
                    mRootView.getData().get(clickPosition).setType(0);
                    mRootView.getData().get(clickPosition).setCurrentDuration(0);
                    playMusic(mRootView.getData(), position, item.getVioceURL(), 1);
                }
            }

        }

//		if (item.getType() != 1 && item.getType() != 3) {
//			item.setExpanded(false);
//			holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
//		} else {
//			item.setExpanded(true);
//			holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
//		}

    }

    public void playMusic(List<MoreAudio> musicData, int position, String url, int type) {
        if (musicBinderAll.isPlaying()) {
            musicBinderAll.pause();
        }
        musicData.get(position).setType(type);
        musicBinder.playMusic(url);
        if (clickPosition != -1) {
            musicData.get(clickPosition).setExpanded(false);
        }
        clickPosition = position;
        MusicManager.Companion.getInstance().setPosition(position);
        RxBus.sendMessage(new AdapaterRefushEvent(true));
    }

    public void click(String id) {
        if (mModel != null) {
            mModel.clickCourse(id).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxSchedulersHelper.io_main())
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {

                        }
                    });
        }
    }

    public void getMoreAudio(int page) {
        if (mModel != null) {
            mModel.getMoreAudio(page).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxSchedulersHelper.io_main())
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            addDispose(d);
                        }

                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {

                                if (page == 1) {
                                    moreAudios.clear();
                                }
                                JSONObject json = jsonObject.getJSONObject("data");
                                if (json.containsKey("changeLessonByDayList")) {
                                    int totalPage = json.getJSONObject("changeLessonByDayList").getInteger("totalPage");
                                    mRootView.setTotalPage(totalPage);
                                    JSONArray data = json.getJSONObject("changeLessonByDayList").getJSONArray("list");

                                    for (int i = 0; i < data.size(); i++) {
                                        List<MoreAudio> list = JSONArray.parseArray(data.getJSONObject(i).getJSONArray("list").toJSONString(), MoreAudio.class);
                                        for (MoreAudio moreAudio : list) {
                                            moreAudio.setDate(data.getJSONObject(i).getString("date"));
                                        }
                                        moreAudios.addAll(list);
                                    }


									/*for (int i = 0; i < data.size(); i++) {
										List<MoreAudio> list = JSONArray.parseArray(data.getJSONObject(i).getJSONArray("list").toJSONString(), MoreAudio.class);
										MoreAudio moreAudio = new MoreAudio();
										if (i == 0) {
											moreAudio.setDate(data.getJSONObject(i).getString("date"));
											list.add(0, moreAudio);
										}
										if (moreAudios.size() > 0 && moreAudios.get(moreAudios.size() - 1).getDate() != null && list.get(0).getDate() != null) {
											Timber.e("list.get(0).getDate()---->%s", list.get(0).getDate());
											if (moreAudios.get(moreAudios.size() - 1).getDate().equals(list.get(0).getDate())) {
												Timber.e("moreAudios.get(moreAudios.size() - 1).getDate()---->%s", moreAudios.get(moreAudios.size() - 1).getDate());
												list.remove(moreAudio);
											}
										}
										moreAudios.addAll(list);
									}*/
                                    if (mRootView != null) {
                                        mRootView.getDataComplete(moreAudios);
                                        mRootView.getHeaderImageResult(json.getString("url"));
                                    }
                                }

                            } else {
                                mRootView.getDataComplete(null);
                                mRootView.showMessage(jsonObject.getString("errmsg"));
                            }
                        }
                    });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        clickPosition = -1;
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
        musicBinder.setMore(true);

    }


    public void registerAudioReceiver() {
        moreMakerCourseAudioReciver = new MoreMakerCourseAudioReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
        filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS);
        filter.addAction(GlobalConsts.ACTION_STATR_MUSIC);
        filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC);
        filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC);
        filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC);
        filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC);
        filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC);
        mApplication.registerReceiver(moreMakerCourseAudioReciver, filter);

        moreMakerCourseAudioReciver.setMonitorListener(new MonitorTwoListener<Context, Intent>() {
            @Override
            public void OnMonitor(Context context, Intent intent) {
                setReceiver(context,intent);
            }
        });

    }

    private void setReceiver(Context context, Intent intent) {
        String action = intent.getAction();
        Timber.e(action);
        if (action == null || clickPosition == -1) {
            return;
        }
        switch (action) {
            case GlobalConsts.ACTION_UPDATE_PROGRESS:
                int current = intent.getIntExtra("current", 0);
                int total = intent.getIntExtra("total", 0);
				/*mAudioList.get(clickPosition).setCurrentDuration(current);
				mAdapter.notifyItemRangeChanged(clickPosition, 1);*/

//					mRootView.upDateAudioProgress(clickPosition, current, total);

                if (musicBinder.getPosition() < mRootView.getData().size()) {
                    mRootView.getData().get(musicBinder.getPosition()).setCurrentDuration(current);
                    mRootView.getData().get(musicBinder.getPosition()).setTotalTime(total);
                    mRootView.getData().get(musicBinder.getPosition()).setType(1);
                    musicBinder.setPosition(musicBinder.getPosition());
                    mAdapter.notifyItemChanged(musicBinder.getPosition());
                }


                break;
            case GlobalConsts.ACTION_LOCAL_MUSIC:
                break;
            case GlobalConsts.ACTION_PAUSE_MUSIC:
                //音乐暂停播放
                if (musicBinder.getPosition() < mRootView.getData().size()) {
                    mRootView.getData().get(musicBinder.getPosition()).setType(2);
                    mAdapter.notifyItemChanged(musicBinder.getPosition());
                }
                break;
            case GlobalConsts.ACTION_STATR_MUSIC:
                //只有点暂停和播放按钮时
                if (clickPosition < mRootView.getData().size()) {
                    mRootView.getData().get(clickPosition).setType(1);
                    musicBinder.setPosition(clickPosition);
                    mAdapter.notifyItemChanged(clickPosition);
                }

                break;
            case GlobalConsts.ACTION_ONLINE_MUSIC:
                break;
            case GlobalConsts.ACTION_NEXT_MUSIC:
                break;
            case GlobalConsts.ACTION_MUSIC_STARTED:
                //音乐开始播放
				/*mAudioList.get(clickPosition).setType(1);
				mOperatingAnim.cancel();
				mAdapter.notifyDataSetChanged();*/
//					mRootView.startMusic(clickPosition);
                if (clickPosition < mRootView.getData().size()) {
                    mRootView.getData().get(clickPosition).setType(1);
                    musicBinder.setPosition(clickPosition);
                    mAdapter.notifyItemChanged(clickPosition);
                }

                break;
            case GlobalConsts.ACTION_COMPLETE_MUSIC:
                //播放完成
				/*mAudioList.get(clickPosition).setType(0);
				mAudioList.get(clickPosition).setCurrentDuration(0);
				mAdapter.notifyDataSetChanged();*/

                if (isPlayAll && musicBinder.getMoreDetails()) {
                    int curr = clickPosition + 1;
                    if (curr < mRootView.getData().size()) {
                        mRootView.getData().get(clickPosition).setType(0);
                        mAdapter.notifyItemChanged(clickPosition);
                        clickPosition = curr;
                        mRootView.getData().get(curr).setType(1);
                        musicBinder.playMusic(mRootView.getData().get(curr).getVioceURL());
                        mAdapter.notifyItemChanged(curr);
                    }
                } else {
                    if (musicBinder.getMoreDetails()) {
                        int curr = musicBinder.getPosition();
                        if (curr < mRootView.getData().size()) {
                            if (curr == 0) {
                                mRootView.getData().get(curr).setType(0);
                                mAdapter.notifyItemChanged(curr);
                                int pos = curr + 1;
                                musicBinder.setPosition(pos);
                                clickPosition = pos;
                                mRootView.getData().get(pos).setType(1);
                                musicBinder.playMusic(mRootView.getData().get(pos).getVioceURL());
                                mAdapter.notifyItemChanged(pos);
                            } else {
                                if (MusicManager.Companion.getInstance().getMoreAudios().size() > 0) {
                                    mRootView.getData().get(curr - 1).setType(0);
                                    mAdapter.notifyItemChanged(curr - 1);
                                    musicBinder.setPosition(curr);
                                    clickPosition = curr;
                                    mRootView.getData().get(curr).setType(1);
                                    musicBinder.playMusic(mRootView.getData().get(curr).getVioceURL());
                                    mAdapter.notifyItemChanged(curr);
                                } else {
                                    mRootView.getData().get(curr).setType(0);
                                    mAdapter.notifyItemChanged(curr);
                                    int pos = curr + 1;
                                    musicBinder.setPosition(pos);
                                    clickPosition = pos;
                                    mRootView.getData().get(pos).setType(1);
                                    musicBinder.playMusic(mRootView.getData().get(pos).getVioceURL());
                                    mAdapter.notifyItemChanged(pos);
                                }

                            }
                        }
                    } else {
                        musicBinder.pause();
                        if (clickPosition < mRootView.getData().size()) {
                            mRootView.getData().get(clickPosition).setType(0);
                            mRootView.getData().get(clickPosition).setCurrentDuration(0);
                            mAdapter.notifyItemChanged(clickPosition);
                        }
                    }
//						mRootView.completeMusic(clickPosition);
                }
                break;
        }
    }


    public void unregisterReceiver() {
        try {
            if (moreMakerCourseAudioReciver != null && mApplication != null) {
                mApplication.unregisterReceiver(moreMakerCourseAudioReciver);
                moreMakerCourseAudioReciver = null;
            }
//            if (musicBinder != null && clickPosition != -1) {
//                musicBinder.pause();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMusicBinder(PlayService.MusicBinder binder) {
        musicBinderAll = binder;
//		musicBinder = binder;
    }


}
