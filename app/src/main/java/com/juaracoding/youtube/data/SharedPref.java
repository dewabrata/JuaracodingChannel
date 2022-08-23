package com.juaracoding.youtube.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPref {

    private Context ctx;
    private SharedPreferences custom_prefence;
    private SharedPreferences default_prefence;

    public SharedPref(Context context) {
        this.ctx = context;
        custom_prefence = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    /**
     * Preference for Last Channel Id
     */
    public void setLastChannelId(String fcmRegId) {
        custom_prefence.edit().putString("LAST_CHANNEL_ID", fcmRegId).apply();
    }

    public String getLastChannelId() {
        return custom_prefence.getString("LAST_CHANNEL_ID", "");
    }

    /**
     * Preference for Fcm register
     */
    public void setFcmRegId(String fcmRegId) {
        custom_prefence.edit().putString("FCM_PREF_KEY", fcmRegId).apply();
    }

    public String getFcmRegId() {
        return custom_prefence.getString("FCM_PREF_KEY", "");
    }

    /**
     * To save dialog permission state
     */
    public void setNeverAskAgain(String key, boolean value) {
        custom_prefence.edit().putBoolean(key, value).apply();
    }

    public void setSubscibeNotif(boolean value) {
        custom_prefence.edit().putBoolean("SUBSCRIBE_NOTIF", value).apply();
    }

    public boolean isSubscibeNotif() {
        return custom_prefence.getBoolean("SUBSCRIBE_NOTIF", false);
    }


    // Preference for first launch
    public void setIntersCounter(int counter) {
        custom_prefence.edit().putInt("INTERS_COUNT", counter).apply();
    }

    public int getIntersCounter() {
        return custom_prefence.getInt("INTERS_COUNT", 0);
    }

    public void clearIntersCounter() {
        custom_prefence.edit().putInt("INTERS_COUNT", 0).apply();
    }


}
