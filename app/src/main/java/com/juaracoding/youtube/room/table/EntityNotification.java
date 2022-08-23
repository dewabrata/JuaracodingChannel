package com.juaracoding.youtube.room.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.juaracoding.youtube.model.Notification;

@Entity(tableName = "notification")
public class EntityNotification {

    @PrimaryKey
    private long savedTime = 1;
    private String title;
    private String content;
    private String image;
    private boolean read = false;

    public long getSavedTime() {
        return savedTime;
    }

    public void setSavedTime(long savedTime) {
        this.savedTime = savedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static EntityNotification getEntity(Notification notification) {
        EntityNotification realm = new EntityNotification();
        realm.setSavedTime(notification.id);
        realm.setContent(notification.content);
        realm.setTitle(notification.title);
        realm.setImage(notification.image);
        realm.setRead(notification.read);
        return realm;
    }
}
