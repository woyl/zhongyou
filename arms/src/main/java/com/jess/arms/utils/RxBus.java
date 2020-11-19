package com.jess.arms.utils;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by ruoyun on 16/9/24.
 * 事件总线
 */

public class RxBus {

    private static volatile RxBus mInstance;

    private RxBus() {
    }

    public static RxBus getDefault() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    private final Subject<Object, Object> _bus = new SerializedSubject<Object, Object>(PublishSubject.create());

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }


    public static void sendMessage(Object o) {
        RxBus.getDefault().send(o);
    }

    public static Subscription handleMessage(Action1 function) {
        return RxBus.getDefault().toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(function);
    }



}
