package com.zhongyou.meet.mobile.ameeting.whiteboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PageControlView extends CardView {

    @BindView(R.id.tv_page)
    protected TextView tv_page;

    private PageControlListener listener;

    public PageControlView(@NonNull Context context) {
        this(context, null);
    }

    public PageControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_page_control, this);
        ButterKnife.bind(this);
    }

    public void setPageIndex(int index, int count) {
        tv_page.setText(String.format(Locale.getDefault(), "%d/%d", index + 1, count));
    }

    @OnClick({R.id.iv_start, R.id.iv_previous, R.id.iv_next, R.id.iv_end,R.id.iv_add})
    public void onClick(View view) {
        if (listener == null) return;
        switch (view.getId()) {
            case R.id.iv_start:
                listener.toStart();
                break;
            case R.id.iv_previous:
                listener.toPrevious();
                break;
            case R.id.iv_next:
                listener.toNext();
                break;
            case R.id.iv_end:
                listener.toEnd();
                break;
            case R.id.iv_add:
                listener.toAdd();
                break;
        }
    }

    public void setListener(PageControlListener listener) {
        this.listener = listener;
    }

    public interface PageControlListener {
        void toStart();

        void toPrevious();

        void toNext();

        void toEnd();
        void toAdd();
    }

}
