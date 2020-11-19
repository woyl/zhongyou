package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongyou.meet.mobile.R;

import java.util.List;

public class inputAdapter extends RecyclerView.Adapter<inputAdapter.ViewHolder> {

    private Context context;
    private List<String> data;
    private onItemClickInt itemClick;

    public inputAdapter(Context context, List<String> data, onItemClickInt temItemClick){
        this.context = context;
        this.data = data;
        this.itemClick = temItemClick;


    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_input,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(position == 0){
            holder.name.setText("相册");
            holder.imgBg.setBackground(context.getResources().getDrawable(R.mipmap.input_photo));
        }else {
            holder.name.setText("拍照");
            holder.imgBg.setBackground(context.getResources().getDrawable(R.mipmap.input_take));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("这里是点击每一行item的响应事件",""+position+item);
                itemClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView imgBg;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            imgBg = itemView.findViewById(R.id.img_photo);

        }
    }

    public interface  onItemClickInt{
        void onItemClick(int pos);
    }
}

