package com.juaracoding.youtube.connection;

import android.util.Log;

import com.juaracoding.youtube.connection.callback.CallbackInfo;
import com.juaracoding.youtube.connection.callback.CallbackSearchVideo;
import com.juaracoding.youtube.connection.callback.CallbackVideo;
import com.juaracoding.youtube.connection.responses.ResponseInfo;
import com.juaracoding.youtube.connection.responses.ResponseSearchVideo;
import com.juaracoding.youtube.connection.responses.ResponseVideos;
import com.juaracoding.youtube.data.Constant;
import com.juaracoding.youtube.data.RemoteConfig;

import retrofit2.Call;
import retrofit2.Callback;

public class Request extends RestBuilder {

    // get list video for home
    public void getVideos(String nextToken, String playlistId, final CallbackVideo listener) {
        Call<ResponseVideos> callback = request.getVideos(
                Constant.MAX_LOAD_VIDEO,
                nextToken,
                playlistId,
                Constant.YOUTUBE_API_KEY
        );
        callback.enqueue(new Callback<ResponseVideos>() {
            @Override
            public void onResponse(Call<ResponseVideos> call, retrofit2.Response<ResponseVideos> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<ResponseVideos> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                listener.onFailed();
            }
        });
    }

    // get list of playlist
    public void getPlaylist(String nextToken, final CallbackVideo listener) {
        Call<ResponseVideos> callback = request.getPlaylist(
                RemoteConfig.channel_id,
                Constant.MAX_LOAD_VIDEO,
                nextToken,
                Constant.YOUTUBE_API_KEY
        );
        callback.enqueue(new Callback<ResponseVideos>() {
            @Override
            public void onResponse(Call<ResponseVideos> call, retrofit2.Response<ResponseVideos> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<ResponseVideos> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                listener.onFailed();
            }
        });
    }

    // get list video for search video
    public void searchVideo(String q, String nextToken, final CallbackSearchVideo listener) {
        Call<ResponseSearchVideo> callback = request.searchVideo(
                RemoteConfig.channel_id,
                q, nextToken,
                Constant.MAX_LOAD_VIDEO,
                Constant.YOUTUBE_API_KEY
        );
        callback.enqueue(new Callback<ResponseSearchVideo>() {
            @Override
            public void onResponse(Call<ResponseSearchVideo> call, retrofit2.Response<ResponseSearchVideo> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<ResponseSearchVideo> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                listener.onFailed();
            }
        });
    }


    // get channel info data
    public void getInfo(final CallbackInfo listener) {
        Call<ResponseInfo> callback = request.getInfo(
                RemoteConfig.channel_id,
                Constant.YOUTUBE_API_KEY
        );
        callback.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, retrofit2.Response<ResponseInfo> response) {
                listener.onComplete(response.body());
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                listener.onFailed();
            }
        });
    }

}
