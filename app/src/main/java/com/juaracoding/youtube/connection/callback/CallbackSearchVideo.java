package com.juaracoding.youtube.connection.callback;

import com.juaracoding.youtube.connection.responses.ResponseSearchVideo;

public interface CallbackSearchVideo {

    void onComplete(ResponseSearchVideo data);

    void onFailed();

}
