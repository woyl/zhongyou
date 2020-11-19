package com.zhongyou.meet.mobile.business;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhongyou.family.photolib.Info;
import com.zhongyou.family.photolib.PhotoView;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by liuheng on 2015/8/19.
 */
public class ViewPagerActivity extends Activity {

    private ViewPager mPager;
    private Info info;
    private ArrayList<String> mListPhotp;

//    private int[] imgsId = new int[]{R.mipmap.baby_default_avatar,R.mipmap.baby_default_avatar};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        if(getIntent().getStringExtra("orientation")!=null){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mListPhotp = getIntent().getStringArrayListExtra("imglist");

        info = getIntent().getParcelableExtra("info");
        mPager = (ViewPager) findViewById(R.id.pager);
//        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));

        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mListPhotp.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view= LayoutInflater.from(ViewPagerActivity.this).inflate(R.layout.item_brower_photo,null);
                PhotoView pview = view.findViewById(R.id.photo);
                pview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                pview.enable();
                pview.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                view.setImageResource(imgsId[position]);
                Picasso.with(BaseApplication.getInstance()).load(mListPhotp.get(position)).into(pview);
                if(position == 0){
//                    pview.animaFrom((Info) getIntent().getParcelableExtra("info"));
                }
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        mPager.setCurrentItem(getIntent().getIntExtra("pos",0));
    }
}
