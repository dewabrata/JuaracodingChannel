package com.juaracoding.youtube.connection;

import com.juaracoding.youtube.connection.responses.ResponseInfo;
import com.juaracoding.youtube.connection.responses.ResponseSearchVideo;
import com.juaracoding.youtube.connection.responses.ResponseVideos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("playlistItems?part=snippet%2CcontentDetails")
    Call<ResponseVideos> getVideos(
            @Query("maxResults") int maxLoad,
            @Query("pageToken") String nextToken,
            @Query("playlistId") String playlistId,
            @Query("key") String apiKey
    );

    @GET("search?part=snippet&order=title&type=video")
    Call<ResponseSearchVideo> searchVideo(
            @Query("channelId") String channelId,
            @Query("q") String q,
            @Query("pageToken") String nextToken,
            @Query("maxResults") int maxLoad,
            @Query("key") String apiKey
    );

    @GET("playlists?part=snippet%2CcontentDetails")
    Call<ResponseVideos> getPlaylist(
            @Query("channelId") String channelId,
            @Query("maxResults") int maxLoad,
            @Query("pageToken") String nextToken,
            @Query("key") String apiKey
    );

    @GET("channels?part=snippet%2CbrandingSettings%2Cstatistics")
    Call<ResponseInfo> getInfo(
            @Query("id") String channelId,
            @Query("key") String apiKey
    );
}
