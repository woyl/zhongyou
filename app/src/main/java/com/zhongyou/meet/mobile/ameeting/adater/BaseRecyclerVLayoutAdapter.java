package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;

import java.util.List;


/**
 * 万能的RecyclerView适配器
 * Created by 南尘 on 16-7-30.
 */
public abstract class BaseRecyclerVLayoutAdapter<T> extends DelegateAdapter.Adapter<BaseRecyclerVLayoutHolder> {

    private Context context;//上下文
    private List<T> list;//数据源
    private LayoutInflater inflater;//布局器
    private int itemLayoutId;//布局id
    private boolean isScrolling;//是否在滚动
    private OnItemClickListener listener;//点击事件监听器
    private OnItemLongClickListener longClickListener;//长按监听器
    private RecyclerView recyclerView;
    private int mItemViewType;


    //在RecyclerView提供数据的时候调用
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    /**
     * 定义一个点击事件接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }

    /**
     * 插入一项
     *
     * @param item
     * @param position
     */
    public void insert(T item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * 删除一项
     *
     * @param position 删除位置
     */
    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }


    public List<T>  getAdapterData(){
        return this.list;
    }

    public void changeAdapterData(List<T> list){
        this.list=list;
        notifyDataSetChanged();
    }

    private LayoutHelper mLayoutHelper;
    public BaseRecyclerVLayoutAdapter(Context context, List<T> list, int itemLayoutId, LayoutHelper layoutHelper) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);
        this.mLayoutHelper = layoutHelper;
        mItemViewType=0;
    }



    public void setLayoutHelper(LayoutHelper layoutHelper){
        this.mLayoutHelper=layoutHelper;
        notifyDataSetChanged();
    }


    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mLayoutHelper;
    }

    public void setNotifyDataChange(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }



    @Override
    public BaseRecyclerVLayoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId,null); //解决条目显示不全
        View view = inflater.inflate(itemLayoutId, parent, false);
        return BaseRecyclerVLayoutHolder.getRecyclerHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerVLayoutHolder holder, int position) {

        if (listener != null) {
            //            holder.itemView.setBackgroundResource(R.drawable.drall);//设置背景
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    listener.onItemClick(recyclerView, view, position);
                }
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    longClickListener.onItemLongClick(recyclerView, view, position);
                    return true;
                }
                return false;
            }
        });
        convert(holder, list.get(position), position, isScrolling);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    /**
     * 必须重写不然会出现滑动不流畅的情况
     */
    @Override
    public int getItemViewType(int position) {
        return mItemViewType;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder      ViewHolder
     * @param item        子项
     * @param position    位置
     * @param isScrolling 是否在滑动
     */
    public abstract void convert(BaseRecyclerVLayoutHolder holder, T item, int position, boolean isScrolling);
}
