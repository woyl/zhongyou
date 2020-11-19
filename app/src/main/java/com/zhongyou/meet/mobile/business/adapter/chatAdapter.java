package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.ViewPagerActivity;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.zhongyou.meet.mobile.view.CropSquareTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {

    private Context context;
    private List<ChatMesData.PageDataEntity> data;
    private onClickCallBack callBack;

    public chatAdapter(Context context, List<ChatMesData.PageDataEntity> data, onClickCallBack tempCallBack){
        this.context = context;
        this.data = data;
        this.callBack = tempCallBack;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(context).inflate(R.layout.item_left,parent,false);
            return new ViewHolder(view);
        }else if(viewType == 1){
            View view = LayoutInflater.from(context).inflate(R.layout.item_right,parent,false);
            return new ViewHolder(view);
        }else if(viewType == 2){
            View view = LayoutInflater.from(context).inflate(R.layout.item_center_big,parent,false);
            return new ViewHolder(view);
        }
        return null;

    }


    @Override
    public int getItemViewType(int position) {

        if(data.get(position).getMsgType() == 1 || data.get(position).getMsgType() == 2){
            return 2;
        }else {

            if (null==data.get(position).getUserId()){
                return 0;
            }

            if(!data.get(position).getUserId().equals(Preferences.getUserId())){
                return 0;
            }else {
                return 1;
            }
        }

//        return super.getItemViewType(position);
    }
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((View)msg.obj).setVisibility(View.GONE);

        }
    };

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(getItemViewType(position)==2){
            if(data.get(position).getMsgType()==1){
                if(data.get(position).getUserId().equals(Preferences.getUserId())){
                    ((TextView)holder.tvCenter).setText("你撤回了一条消息");
                    Log.v("onbindviewholder999","=="+(System.currentTimeMillis()-data.get(position).getReplyTimestamp()));
                    if(System.currentTimeMillis()-data.get(position).getReplyTimestamp()<20000){
                        ((TextView)holder.tvEdit).setVisibility(View.VISIBLE);
                        Message msg = new Message();
                        msg.obj = holder.tvEdit;
                        handler.sendMessageDelayed(msg,20000);
                        ((TextView)holder.tvEdit).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                callBack.onEditCallBack(data.get(position).getContent());
                            }
                        });
                    }else {
                        ((TextView)holder.tvEdit).setVisibility(View.GONE);
                    }

                }else {
                    ((TextView)holder.tvCenter).setText(data.get(position).getUserName()+"  撤回了一条消息");
                    ((TextView)holder.tvEdit).setVisibility(View.GONE);
                }
            }else {
                ((TextView)holder.tvCenter).setText(data.get(position).getReplyTime());
                ((TextView)holder.tvEdit).setVisibility(View.GONE);
            }

            return;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onClickCallBackFuc();
            }
        });

        if (null==data.get(position).getUserName()){
            ((TextView)holder.name).setText("");
        }else {
            ((TextView)holder.name).setText(data.get(position).getUserName());
        }



        if (null==data.get(position).getUserLogo()){
            Picasso.with(context).load(R.drawable.ico_face).into(holder.imgHead);
        }else {
//            ImageHelper.loadImageDpId(data.get(position).getUserLogo(), R.dimen.my_px_66, R.dimen.my_px_66, holder.imgHead);
            Glide.with(context)
                    .load(data.get(position).getUserLogo())
                    .error(R.drawable.ico_face)
                    .placeholder(R.drawable.ico_face)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgHead);
        }


        if(getItemViewType(position)==0){
            holder.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callBack.onLongContent(view,null,data.get(position).getContent());
                    return false;
                }
            });
            holder.imgHead.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callBack.onLongImgHead(data.get(position).getUserName(),data.get(position).getUserId());
                    return false;
                }
            });
        }else {
            holder.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callBack.onLongContent(view,data.get(position).getId(),data.get(position).getContent());
                    return false;
                }
            });
            holder.imgHead.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
            if(data.get(position).getLocalState()==0){
                holder.sendBar.setVisibility(View.GONE);
                holder.tvState.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.GONE);
            }else if(data.get(position).getLocalState() == 1){
                holder.sendBar.setVisibility(View.VISIBLE);
                holder.tvState.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.GONE);
            }else if(data.get(position).getLocalState() == 2){
                holder.sendBar.setVisibility(View.GONE);
                holder.tvState.setVisibility(View.VISIBLE);
                holder.tvError.setVisibility(View.VISIBLE);
                holder.tvState.setOnClickListener(view -> callBack.onReSend(data.get(position).getContent(),data.get(position).getType()));
            }
        }

        if(data.get(position).getType() == 1){
            String url = "";
            Picasso.with(context).load(data.get(position).getContent())
                     .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_STORE)
                    .error(R.drawable.load_error)
                    .placeholder(R.drawable.loading)
                    .transform(new CropSquareTransformation()).into(holder.imgPic, new Callback() {
                @Override
                public void onSuccess() {

                    holder.imgPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int pos = 0;
                            ArrayList<String> mList = new ArrayList<>();
                            for(int i=0; i<data.size(); i++){
                                if(data.get(i).getType()==1){
                                    mList.add(data.get(i).getContent());
                                    if(data.get(i).getContent().equals(data.get(position).getContent())){
                                        pos = mList.size()-1;
                                    }
                                }
                            }
                            context.startActivity(new Intent(context,ViewPagerActivity.class).putExtra("imglist",mList)
                                    .putExtra("pos",pos));
                        }
                    });

                    holder.imgPic.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            callBack.onLongContent(view,data.get(position).getId(),null);
                            return true;
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
            ((TextView)holder.tvContent).setVisibility(View.GONE);
            ((ImageView)holder.imgArrow).setVisibility(View.GONE);
            ((ImageView)holder.imgPic).setVisibility(View.VISIBLE);



        }else {
            ((TextView)holder.tvContent).setText(data.get(position).getContent());
            ((TextView)holder.tvContent).setVisibility(View.VISIBLE);
            ((ImageView)holder.imgArrow).setVisibility(View.VISIBLE);
            ((ImageView)holder.imgPic).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView tvContent;
        private CircleImageView imgHead;
        private ImageView imgPic;
        private ImageView imgArrow;
        private ProgressBar sendBar;
        private TextView tvState, tvError;

        private TextView tvCenter;
        private TextView tvEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            imgHead = (CircleImageView)itemView.findViewById(R.id.mIvHead);
            imgPic = (ImageView) itemView.findViewById(R.id.img_pic);
            imgArrow = (ImageView)itemView.findViewById(R.id.img_arrow);
            sendBar = (ProgressBar)itemView.findViewById(R.id.send_bar);
            tvState = (TextView)itemView.findViewById(R.id.send_sate);
            tvError = (TextView)itemView.findViewById(R.id.send_err);

            tvCenter = itemView.findViewById(R.id.tv_center);
            tvEdit = itemView.findViewById(R.id.tv_edit);

        }
    }

    public interface onClickCallBack{
        void onClickCallBackFuc();
        void onLongImgHead(String name, String userId);
        void onLongContent(View view, String id,String content);
        void onEditCallBack(String content);
        void onReSend(String content,int type);
//        void onClickImage(Info info);
    }
}
