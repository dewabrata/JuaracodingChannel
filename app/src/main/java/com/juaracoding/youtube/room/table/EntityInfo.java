package com.juaracoding.youtube.room.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.juaracoding.youtube.model.Info;

@Entity(tableName = "info")
public class EntityInfo {

    @PrimaryKey
    public long infoId = 1;
    public String title = "";
    public String description = "";
    public String thumbUrl = "";
    public String bannerUrl = "";
    public long videoCount = 0;

    public EntityInfo() {
    }

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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public long getInfoId() {
        return infoId;
    }

    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }

    public long getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(long videoCount) {
        this.videoCount = videoCount;
    }

    public static EntityInfo getEntity(Info obj) {
        EntityInfo info = new EntityInfo();
        info.setTitle(obj.snippet.title);
        info.setDescription(obj.snippet.description);
        info.setThumbUrl(obj.snippet.thumbnails.medium.url);
        if (obj.brandingSettings != null && obj.brandingSettings.image != null && obj.brandingSettings.image.bannerExternalUrl != null) {
            info.setBannerUrl(obj.brandingSettings.image.bannerExternalUrl);
        } else {
            info.setBannerUrl(null);
        }
        info.setVideoCount(obj.statistics.videoCount);
        return info;
    }
}
