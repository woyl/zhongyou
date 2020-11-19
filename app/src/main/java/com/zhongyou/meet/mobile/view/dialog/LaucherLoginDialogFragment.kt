package com.zhongyou.meet.mobile.view.dialog

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.allen.library.SuperButton
import com.kongzue.dialog.v3.FullScreenDialog
import com.woyl.lt_woyl.dialog.BaseDialogFragment
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.utils.listener.MonitorListener
import com.zhongyou.meet.mobile.view.MyWebView


class LaucherLoginDialogFragment(appCompatActivity: AppCompatActivity?) :BaseDialogFragment(true,Gravity.CENTER) {

    private var mFullScreenDialog: FullScreenDialog? = null
    private var appCompatActivity: AppCompatActivity?= null
    private var listener: MonitorListener<Boolean>?= null


    init {
        this.appCompatActivity  = appCompatActivity
    }

    fun setMonitorListener(listener: MonitorListener<Boolean>) {
        this.listener = listener
    }

    override fun initViews() {
        view = inflater.inflate(R.layout.dialog_laucher_login,null)
        val tv_1 = view.findViewById<AppCompatTextView>(R.id.tv_1)
        val sb = view.findViewById<SuperButton>(R.id.bt_agree)
        val tv_no_agree = view.findViewById<AppCompatTextView>(R.id.tv_no_agree)


        sb.setOnClickListener {
            listener?.OnMonitor(true)
            dismiss()
        }

        tv_no_agree.setOnClickListener {
            listener?.OnMonitor(false)
            dismiss()
        }

        val content = "您可以通过阅读完整的《用户协议》和《隐私政策》来了解详细信息."
        val spannableStringBuilder = SpannableStringBuilder(content)
        val sizeSpan = AbsoluteSizeSpan(40)
        spannableStringBuilder.setSpan(sizeSpan,0,content.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.c3A8CE8))
        val colorSpan = object :UnderlineSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#3A8CE8")
            }
        }

        val colorSpan2 = object :UnderlineSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#3A8CE8")
            }
        }

        val clickableSpan = object :ClickableSpan() {
            override fun onClick(widget: View) {
                mFullScreenDialog = FullScreenDialog.show(appCompatActivity!!, R.layout.activity_web_view, FullScreenDialog.OnBindView { dialog, rootView ->
                    rootView.findViewById<View>(R.id.titleRela).visibility = View.GONE
                    val webView: MyWebView = rootView.findViewById(R.id.web_view)
                    webView.loadUrl(Constant.AGREEMENTURL)
                }).setOkButton("关闭")
                        .setOnBackClickListener {
                            mFullScreenDialog?.doDismiss()
                            true
                        }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        val clickableSpan2 = object :ClickableSpan() {
            override fun onClick(widget: View) {
                mFullScreenDialog = FullScreenDialog.show(appCompatActivity!!, R.layout.activity_web_view, FullScreenDialog.OnBindView { dialog, rootView ->
                    rootView.findViewById<View>(R.id.titleRela).visibility = View.GONE
                    val webView: MyWebView = rootView.findViewById(R.id.web_view)
                    webView.loadUrl(Constant.AGREEMENTURL_PRIVATE)
                }).setOkButton("关闭")
                        .setOnBackClickListener {
                            mFullScreenDialog?.doDismiss()
                            true
                        }
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }



        val privacyBeginIndex = content.indexOf("《")
        val privacyEndIndex = content.indexOf("》") + 1
        val protocolBeginIndex = content.lastIndexOf("《")
        val protocolEndIndex = content.lastIndexOf("》") + 1

        spannableStringBuilder.setSpan(clickableSpan,privacyBeginIndex,privacyEndIndex,Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(clickableSpan2,protocolBeginIndex,protocolEndIndex,Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        tv_1.movementMethod = LinkMovementMethod.getInstance()

        spannableStringBuilder.setSpan(colorSpan,privacyBeginIndex,privacyEndIndex,Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(colorSpan2,protocolBeginIndex,protocolEndIndex,Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

        tv_1.text = spannableStringBuilder
    }
}