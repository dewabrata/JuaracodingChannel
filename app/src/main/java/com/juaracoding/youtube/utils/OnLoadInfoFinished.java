package com.juaracoding.youtube.utils;

import com.juaracoding.youtube.room.table.EntityInfo;

public interface OnLoadInfoFinished {

    void onComplete(EntityInfo data);

    void onFailed();

}
