package com.juaracoding.youtube.connection.responses;

import com.juaracoding.youtube.model.Video;

import java.io.Serializable;
import java.util.List;

public class ResponseVideos implements Serializable {
    public String nextPageToken;
    public List<Video> items;
}
