package com.juaracoding.youtube.connection.callback;

import com.juaracoding.youtube.connection.responses.ResponseVideos;

public interface CallbackVideo {

    void onComplete(ResponseVideos data);

    void onFailed();
}
