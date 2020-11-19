package com.zhongyou.meet.mobile.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import kotlinx.android.synthetic.main.activity_about_agreement.*
import kotlinx.android.synthetic.main.base_tool_bar_k.*

class AboutAgreementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_agreement)

        title_k.text = "关于我们"
        back.setOnClickListener {
            finish()
        }

        user_agreement.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java).putExtra("url", Constant.AGREEMENTURL))
        }

        privacy_agreement.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java).putExtra("url", Constant.AGREEMENTURL_PRIVATE))
        }
    }
}