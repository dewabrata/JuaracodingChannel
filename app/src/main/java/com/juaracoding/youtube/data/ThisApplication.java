package com.juaracoding.youtube.data;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.juaracoding.youtube.BuildConfig;

import com.juaracoding.youtube.connection.Request;
import com.juaracoding.youtube.connection.callback.CallbackInfo;
import com.juaracoding.youtube.connection.responses.ResponseInfo;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.room.table.EntityInfo;
import com.juaracoding.youtube.utils.NetworkCheck;
import com.juaracoding.youtube.utils.OnLoadInfoFinished;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class ThisApplication extends Application {

    private Request request;
    private DAO database;
    private OnLoadInfoFinished onLoadInfoFinished;
    private int retry_index = 0;
    private boolean on_request_info = false;
    private boolean ever_update = false;
    private SharedPref shared_pref;
    private int fcm_count = 0;
    private final int FCM_MAX_COUNT = 10;

    private static ThisApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        shared_pref = new SharedPref(this);

        // Init firebase.
        FirebaseApp.initializeApp(this);

        // fetch firebase remote config
        fetchRemoteConfig();



        request = new Request();
        database = AppDatabase.getDb(this).getDAO();

        // request channel info data
        retryLoadChannelInfo();
        obtainFirebaseToken();
        subscribeTopicNotif();

        Log.d("FCM_REG_ID", shared_pref.getFcmRegId());
    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    public void retryLoadChannelInfo() {
        EntityInfo info = database.getInfo();
        boolean newChannel = !shared_pref.getLastChannelId().equals(RemoteConfig.channel_id);
        if (info == null || newChannel) {
            retry_index = 0;
            loadChannelInfo();
        }
    }

    private void loadChannelInfo() {
        on_request_info = true;
        if (retry_index >= Constant.MAX_RETRY_INFO) {
            on_request_info = false;
            if (onLoadInfoFinished != null) onLoadInfoFinished.onFailed();
            return;
        }
        retry_index++;
        request.getInfo(new CallbackInfo() {
            @Override
            public void onComplete(ResponseInfo data) {
                on_request_info = false;
                if (data != null && data.items != null && data.items.size() > 0) {
                    setEverUpdate(true);
                    database.insertInfo(EntityInfo.getEntity(data.items.get(0)));
                    EntityInfo info = database.getInfo();
                    shared_pref.setLastChannelId(RemoteConfig.channel_id);
                    if (onLoadInfoFinished != null) onLoadInfoFinished.onComplete(info);
                } else {
                    delayLoadChannelInfo();
                }
            }

            @Override
            public void onFailed() {
                on_request_info = false;
                delayLoadChannelInfo();
            }
        });
    }

    private void delayLoadChannelInfo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadChannelInfo();
            }
        }, 500);
    }

    public void setOnLoadInfoFinished(OnLoadInfoFinished onLoadInfoFinished) {
        this.onLoadInfoFinished = onLoadInfoFinished;
    }

    public boolean isOnRequestInfo() {
        return on_request_info;
    }

    private void obtainFirebaseToken() {
        if (NetworkCheck.isConnect(this)) {

            fcm_count++;
            Log.d("FCM_SUBMIT", "obtainFirebaseToken");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d("FCM_SUBMIT", "obtainFirebaseToken : " + fcm_count + "-onFailure : " + task.getException().getMessage());
                    if (fcm_count > FCM_MAX_COUNT) return;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            obtainFirebaseToken();
                        }
                    }, 500);
                } else {
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM_SUBMIT", "obtainFirebaseToken : " + fcm_count + "onSuccess");
                    shared_pref.setFcmRegId(token);
                    if (!TextUtils.isEmpty(token)) {
                        Log.d("FCM_REG_ID", token);
                    }
                }
            });
        }
    }


    private void subscribeTopicNotif() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.NOTIF_TOPIC).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                shared_pref.setSubscibeNotif(task.isSuccessful());
            }
        });
    }

    public boolean isEverUpdate() {
        return ever_update;
    }

    public void setEverUpdate(boolean ever_update) {
        this.ever_update = ever_update;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * For Remote Config
     */

    private void fetchRemoteConfig() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate();

        RemoteConfig.setFromRemoteConfig(mFirebaseRemoteConfig);
    }

    /** ---------------------------------------- For Remote Config ----------------------------------
     * */
}
