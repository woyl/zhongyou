package com.zhongyou.meet.mobile.core;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.entiy.MoreAudio;
import com.zhongyou.meet.mobile.event.AdapaterRefushEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * 播放音乐相关Service服务
 */
public class PlayService extends Service {
	private static MediaPlayer mediaPlayer;
	private static boolean isLoop = true;
	private Intent mIntent;
	private UpdateProgressThread mUpdateProgressThread;

//	private List<MoreAudio> moreAudioList;
//	private int position,posi;
//	private boolean isMore = true;
//	private boolean isMoreDetails = true;
//	private int oldPosition = -1;
//	private boolean isOtherPause = true;

	/**
	 * 创建Service时执行1次
	 */
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		//注册mediaPlayer的监听
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				//准备完成  开始播放
				mediaPlayer.start();
				//发送广播 -> 音乐已经开始播放
				mIntent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
				//启动更新音乐进度的线程
				sendOrderedBroadcast(mIntent,null);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {

				if (MusicManager.Companion.getInstance().isMoreMusicDatas() && MusicManager.Companion.getInstance().isMoreDetails()) {
					if (MusicManager.Companion.getInstance().getMoreAudios().size() > 0) {
						MusicManager.Companion.getInstance().setPositionNew(MusicManager.Companion.getInstance().getPosition() + 1);
						if (MusicManager.Companion.getInstance().getPositionNew() < MusicManager.Companion.getInstance().getMoreAudios().size()) {
							mediaPlayer.reset();
							try {
								mediaPlayer.setDataSource(MusicManager.Companion.getInstance().getMoreAudios().get(MusicManager.Companion.getInstance().getPositionNew()).getVioceURL());
							} catch (IOException e) {
								e.printStackTrace();
							}
							mediaPlayer.prepareAsync();
							MusicManager.Companion.getInstance().setPosition(MusicManager.Companion.getInstance().getPositionNew());
							RxBus.sendMessage(new AdapaterRefushEvent(true));
						}
					}
				}

				//next() 通知Activity播完了
				Timber.e("setOnCompletionListener   onCompletion");
				mIntent = new Intent(GlobalConsts.ACTION_COMPLETE_MUSIC);
				sendOrderedBroadcast(mIntent,null);


			}
		});
		mUpdateProgressThread = new UpdateProgressThread();
		mUpdateProgressThread.start();

		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Timber.e("what:==%d,extra==%d", what, extra);
				return true;
			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}


	@Override
	public void onDestroy() {
		mediaPlayer.release();  //释放资源
		isLoop = false;
		super.onDestroy();
	}

	public  class MusicBinder extends Binder {

		/**
		 * 播放或暂停
		 */
		/**
		 * 播放或暂停
		 */
	/*	public void startOrPause() {
			if (mediaPlayer.isPlaying()) {
				Intent intent = new Intent(GlobalConsts.ACTION_PAUSE_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.pause();
			} else {
				Intent intent = new Intent(GlobalConsts.ACTION_STATR_MUSIC);
				sendBroadcast(intent);
				mediaPlayer.start();
			}
		}*/

		public void resetMediaPlayer(){
			if (mediaPlayer!=null){
				mediaPlayer.reset();
			}
		}

		/**
		 * 暂停播放
		 */
		public void pause() {
			if (mediaPlayer != null) {
				Intent intent = new Intent(GlobalConsts.ACTION_PAUSE_MUSIC);
				sendOrderedBroadcast(intent,null);
				mediaPlayer.pause();
			}
		}

		/**
		 * 开始播放
		 */
		public void start() {
			if (mediaPlayer != null) {
				Intent intent = new Intent(GlobalConsts.ACTION_STATR_MUSIC);
				sendOrderedBroadcast(intent,null);
				mediaPlayer.start();
			}
		}

//		public List<MoreAudio> getLists() {
//			return moreAudioList;
//		}

//		public void setGlobalPlayback(ArrayList<MoreAudio> audios,int posi) {
//			moreAudioList = audios;
//			position = posi;

//			try {
//				if (moreAudioList.size() > 0) {
//					if (!mediaPlayer.isPlaying()) {
//						mediaPlayer.reset();
//						mediaPlayer.setDataSource(audios.get(posi).getVioceURL());
//						mediaPlayer.prepareAsync();
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				Intent intent = new Intent(GlobalConsts.ACTION_COMPLETE_MUSIC);
//				intent.putExtra("isPlaying", mediaPlayer.isPlaying());
//				sendOrderedBroadcast(intent,null);
//			}
//		}

		public void setMore(boolean isMores) {
			MusicManager.Companion.getInstance().setMoreMusicDatas(isMores);
		}


		public void setMoreDetails(boolean isMore) {
			MusicManager.Companion.getInstance().setMoreDetails(isMore);
		}

		public boolean getMoreDetails(){
			return MusicManager.Companion.getInstance().isMoreDetails();
		}

		public void setPosition(int posi) {
			MusicManager.Companion.getInstance().setPosition(posi);
		}

		public int getPosition(){
			return MusicManager.Companion.getInstance().getPosition();
		}

		public void setOldPosition(int position) {
			MusicManager.Companion.getInstance().setOldMusiPosition(position);
		}

		public int getOldPosition(){
			return MusicManager.Companion.getInstance().getOldMusiPosition();
		}

		public void setIsOtherPause(boolean isPause) {
			MusicManager.Companion.getInstance().setOtherPause(isPause);
		}

		public boolean isOtherPause(){
			return MusicManager.Companion.getInstance().isOtherPause();
		}

		/**
		 * 定位到某个位置 继续播放、暂停
		 *
		 * @param position
		 */
		public void seekTo(int position) {
			mediaPlayer.seekTo(position);
		}

		/**
		 * 供客户端调用的接口方法
		 * 当连接与正在播放的连接相同的时候  就直接播放
		 *
		 * @param url
		 */
		public void playMusic(String url) {

			Timber.e("url===="+url + "--------->");

			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				e.printStackTrace();
				Intent intent = new Intent(GlobalConsts.ACTION_COMPLETE_MUSIC);
				intent.putExtra("isPlaying", mediaPlayer.isPlaying());
//				sendBroadcast(intent);
				sendOrderedBroadcast(intent,null);
			}
		}

		public boolean isPlaying() {
			if (mediaPlayer != null) {
				return mediaPlayer.isPlaying();
			}
			return false;
		}

		public long getCurrentDutation() {
			if (mediaPlayer != null) {
				return  mediaPlayer.getDuration();
			}
			return -1;
		}
	}


	/**
	 * 更新进度
	 * 每1S发送一次广播
	 */
	class UpdateProgressThread extends Thread {
		public void run() {
			while (isLoop) {
				try {
					Thread.sleep(1000);
					if (mediaPlayer.isPlaying()) {
						//发送广播
						Intent intent = new Intent(GlobalConsts.ACTION_UPDATE_PROGRESS);
						int current = mediaPlayer.getCurrentPosition();
						int total = mediaPlayer.getDuration();
						intent.putExtra("current", current);
						intent.putExtra("total", total);
						intent.putExtra("isPlaying", mediaPlayer.isPlaying());
						intent.putExtra("position",MusicManager.Companion.getInstance().getPosition());
						sendOrderedBroadcast(intent,null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


}



