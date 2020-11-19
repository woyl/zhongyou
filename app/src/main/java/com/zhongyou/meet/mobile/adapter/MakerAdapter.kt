package com.zhongyou.meet.mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MakerAdapter(fragmentActivity: FragmentActivity,fragments:MutableList<Fragment>) : FragmentStateAdapter(fragmentActivity) {

    private var mFragment  = mutableListOf<Fragment>()

    init {
        mFragment = fragments
    }

    override fun getItemCount(): Int {
        return mFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragment[position]
    }


}