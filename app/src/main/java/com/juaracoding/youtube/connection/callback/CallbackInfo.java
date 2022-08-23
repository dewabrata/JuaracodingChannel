package com.juaracoding.youtube.connection.callback;

import com.juaracoding.youtube.connection.responses.ResponseInfo;

public interface CallbackInfo {

    void onComplete(ResponseInfo data);

    void onFailed();
}
