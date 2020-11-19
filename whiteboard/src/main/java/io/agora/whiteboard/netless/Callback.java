package io.agora.whiteboard.netless;

public interface Callback<T> {

    void onSuccess(T res);

    void onFailure(Throwable throwable);

}
