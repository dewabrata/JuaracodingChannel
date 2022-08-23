package com.juaracoding.youtube.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.juaracoding.youtube.room.table.EntityFavorite;
import com.juaracoding.youtube.room.table.EntityInfo;
import com.juaracoding.youtube.room.table.EntityNotification;
import com.juaracoding.youtube.room.table.EntityWatched;

import java.util.List;

@Dao
public interface DAO {

    /* table favorite transaction ------------------------------------------------------------------ */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(EntityFavorite fav);

    @Query("DELETE FROM favorite WHERE videoId = :id")
    void deleteFavorite(String id);

    @Query("SELECT * FROM favorite ORDER BY savedTime DESC")
    List<EntityFavorite> getAllFavorite();


    /* table info transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInfo(EntityInfo info);

    @Query("SELECT * FROM info WHERE infoId = 1")
    EntityInfo getInfo();

    @Query("DELETE FROM info")
    void deleteInfo();


    /* table notification transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(EntityNotification notification);

    @Query("DELETE FROM notification WHERE savedTime = :id")
    void deleteNotification(long id);

    @Query("DELETE FROM notification")
    void deleteAllNotification();

    @Query("SELECT * FROM notification ORDER BY savedTime DESC")
    List<EntityNotification> getAllNotification();


    /* table watched transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWatched(EntityWatched watched);

    @Query("SELECT COUNT(videoId) FROM watched WHERE videoId = :id")
    Integer countWatched(String id);
}
