package com.zhongyou.meet.mobile.business;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.adapter.ImageBrowerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageBrowerActivity extends BasicActivity {

    private RecyclerView mRecycler;
    private ImageBrowerAdapter adapter;
    private List<String> imgList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_brower);
        initView();
        imgList = getIntent().getStringArrayListExtra("imglist");
        adapter = new ImageBrowerAdapter(this,imgList,getIntent().getParcelableExtra("info"));
        mRecycler.setAdapter(adapter);
    }
    private void initView(){
        mRecycler = (RecyclerView)this.findViewById(R.id.recycler);
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecycler.setLayoutManager(gridlayoutManager);

    }

    @Override
    public String getStatisticsTag() {
        return "浏览图片";
    }
}
