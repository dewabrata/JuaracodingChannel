package com.juaracoding.youtube.room.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "watched")
public class EntityWatched {

    @PrimaryKey
    @NonNull
    private String videoId = "";

    @ColumnInfo
    private long time = 0;

    public EntityWatched(String videoId, long time) {
        this.videoId = videoId;
        this.time = time;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
