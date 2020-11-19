package io.agora.openlive.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Audience;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by whatisjava on 17-11-22.
 */

public class AudienceAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Audience> audiences;
    private OnAudienceButtonClickListener listener;

    public AudienceAdapter(Context context, ArrayList<Audience> audiences, OnAudienceButtonClickListener listener) {
        this.context = context;
        this.audiences = audiences;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return audiences != null ? audiences.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return audiences != null ? audiences.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_audience, null);
            viewHolder = new ViewHolder();
            viewHolder.audienceLayout = convertView.findViewById(R.id.stop_audience);
            viewHolder.nameText = convertView.findViewById(R.id.audience_name);
            viewHolder.talkButton = convertView.findViewById(R.id.talk);
            viewHolder.checkButton = convertView.findViewById(R.id.check);
            viewHolder.handsupImage = convertView.findViewById(R.id.handsup);
            viewHolder.clientTypeImage = convertView.findViewById(R.id.clientType);
            viewHolder.callingText = convertView.findViewById(R.id.calling);
            viewHolder.auditStatusImage = convertView.findViewById(R.id.auditStatus);
            viewHolder.postTypeNameText = convertView.findViewById(R.id.postTypeName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Audience audience = audiences.get(position);
        viewHolder.nameText.setText((position + 1) + ". " + audience.getUname());

        if (audience.getAuditStatus() == 1) {
            viewHolder.auditStatusImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.auditStatusImage.setVisibility(View.INVISIBLE);
        }

        if (String.valueOf(audience.getUid()).startsWith("1")) {
            viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
            viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_tv);
        } else if(String.valueOf(audience.getUid()).startsWith("2")) {
            viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
            viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_mobile);
        } else if(String.valueOf(audience.getUid()).startsWith("3")) {
            viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
            viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_tv);
        } else if(String.valueOf(audience.getUid()).startsWith("4")) {
            viewHolder.clientTypeImage.setVisibility(View.VISIBLE);
            viewHolder.clientTypeImage.setImageResource(R.drawable.ic_lable_mobile);
        } else {
            viewHolder.clientTypeImage.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(audience.getPostTypeName())) {
            viewHolder.postTypeNameText.setText(audience.getPostTypeName());
            viewHolder.postTypeNameText.setVisibility(View.VISIBLE);
        } else {
            viewHolder.postTypeNameText.setVisibility(View.GONE);
        }

        if (audience.isHandsUp()) {
            viewHolder.handsupImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.handsupImage.setVisibility(View.INVISIBLE);
        }

        if (audience.getCallStatus() == 2) {
            viewHolder.callingText.setVisibility(View.VISIBLE);
            viewHolder.callingText.setText("连麦中...");
            viewHolder.talkButton.setVisibility(View.GONE);
        } else if (audience.getCallStatus() == 1) {
            viewHolder.callingText.setVisibility(View.VISIBLE);
            viewHolder.callingText.setText("正在连麦...");
            viewHolder.talkButton.setVisibility(View.GONE);
        } else {
            viewHolder.callingText.setVisibility(View.GONE);
            viewHolder.talkButton.setVisibility(View.VISIBLE);
            viewHolder.talkButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTalkButtonClick(audience);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout audienceLayout;
        ImageView handsupImage, auditStatusImage, clientTypeImage;
        TextView nameText, callingText, postTypeNameText;
        Button talkButton, checkButton;
    }

    public void setData(ArrayList<Audience> audiences){
        this.audiences = audiences;
        Collections.sort(this.audiences, (o1, o2) -> {
            if(o1.isHandsUp() && !o2.isHandsUp()){
                return -1;
            } else if(!o1.isHandsUp() && o2.isHandsUp()){
                return 1;
            } else {
                return 0;
            }
        });
        notifyDataSetChanged();
    }

    public interface OnAudienceButtonClickListener {

        public void onTalkButtonClick(Audience audience);

    }

}
