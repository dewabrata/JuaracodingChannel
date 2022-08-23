package com.juaracoding.youtube.room.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.juaracoding.youtube.model.ContentDetails;
import com.juaracoding.youtube.model.Snippet;
import com.juaracoding.youtube.model.Thumbnail;
import com.juaracoding.youtube.model.TypeThumbnail;
import com.juaracoding.youtube.model.Video;

import java.io.Serializable;

@Entity(tableName = "favorite")
public class EntityFavorite implements Serializable {

    @PrimaryKey
    @NonNull
    private String videoId = "";
    private String title;
    private String description;
    private String thumbnail;
    private long savedTime = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getSavedTime() {
        return savedTime;
    }

    public void setSavedTime(long savedTime) {
        this.savedTime = savedTime;
    }

    public static Video getVideo(EntityFavorite obj) {

        Video video = new Video();
        video.id = obj.videoId;
        video.snippet = new Snippet();
        video.snippet.thumbnails = new Thumbnail();
        video.snippet.thumbnails.high = new TypeThumbnail();
        video.contentDetails = new ContentDetails();

        video.snippet.title = obj.title;
        video.snippet.description = obj.description;
        video.contentDetails.videoId = obj.videoId;
        video.snippet.thumbnails.high.url = obj.thumbnail;

        return video;
    }

    public static EntityFavorite getEntity(Video obj) {
        EntityFavorite fav = new EntityFavorite();
        fav.setVideoId(obj.contentDetails.videoId);
        fav.setTitle(obj.snippet.title);
        fav.setDescription(obj.snippet.description);
        String thumb = obj.snippet.thumbnails != null ? obj.snippet.thumbnails.high.url : "";
        fav.setThumbnail(thumb);
        fav.setSavedTime(System.currentTimeMillis());
        return fav;
    }
}
