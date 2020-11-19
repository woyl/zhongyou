package io.agora.openlive.ui;

import android.content.Context;

import android.util.AttributeSet;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import io.agora.openlive.model.VideoStatusData;

public class AudienceRecyclerView extends RecyclerView {
    public AudienceRecyclerView(Context context) {
        super(context);
    }

    public AudienceRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudienceRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private AudienceRecyclerViewAdapter audienceRecyclerViewAdapter;

    private VideoViewEventListener mEventListener;

    public void setItemEventHandler(VideoViewEventListener listener) {
        this.mEventListener = listener;
    }

    private boolean initAdapter(int localUid, HashMap<Integer, SurfaceView> uids) {
        if (audienceRecyclerViewAdapter == null) {
            audienceRecyclerViewAdapter = new AudienceRecyclerViewAdapter(getContext(), localUid, uids, mEventListener);
            audienceRecyclerViewAdapter.setHasStableIds(true);
            return true;
        }
        return false;
    }

    public void initViewContainer(Context context, int localUid, HashMap<Integer, SurfaceView> uids) {
        boolean newCreated = initAdapter(localUid, uids);

        if (!newCreated) {
            audienceRecyclerViewAdapter.setLocalUid(localUid);
            audienceRecyclerViewAdapter.init(uids, localUid, true);
        }

        this.setAdapter(audienceRecyclerViewAdapter);

        audienceRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void insert(int id, SurfaceView surfaceView){
        audienceRecyclerViewAdapter.insertItem(id, surfaceView);
    }

    public SurfaceView getSurfaceView(int index) {
        return audienceRecyclerViewAdapter.getItem(index).mView;
    }

    public VideoStatusData getItem(int position) {
        return audienceRecyclerViewAdapter.getItem(position);
    }
}
