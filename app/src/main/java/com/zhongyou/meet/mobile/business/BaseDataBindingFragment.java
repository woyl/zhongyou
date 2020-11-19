package com.zhongyou.meet.mobile.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**DataBinding的activity基类
 * Created by wufan on 2017/2/20.
 */

public abstract class BaseDataBindingFragment<X extends ViewDataBinding>  extends BaseFragment{
    protected X mBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, initContentView(), container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSavedInstanceState(savedInstanceState);
        initView();
        initAdapter();
        initListener();
        requestData();
    }

    /**
     * 恢复操作
     *
     * @param savedInstanceState
     */
    protected void initSavedInstanceState(Bundle savedInstanceState) {
    }

    /**
     * 初始化布局
     */
    protected abstract int initContentView();


    /**
     * 获得初始化数据
     */
    protected void initExtraIntent() {

    }

    /**
     * 初始化请求地址
     */
    protected void initUrl() {
    }

    /**
     * 初始化控件
     */
    protected void initView() {

    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    /**
     * 初始化Adapter
     */
    protected void initAdapter() {
    }

    /**
     * 请求数据
     */
    protected void requestData() {

    }



}
