package com.juaracoding.youtube.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.juaracoding.youtube.ActivitySplash;
import com.juaracoding.youtube.R;
import com.juaracoding.youtube.data.SharedPref;
import com.juaracoding.youtube.model.Notification;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.room.table.EntityNotification;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        new SharedPref(this).setFcmRegId(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DAO database = AppDatabase.getDb(this).getDAO();
        Notification notification = new Notification();
        if (remoteMessage.getData().size() > 0) {
            Object obj = remoteMessage.getData();
            notification = new Gson().fromJson(new Gson().toJson(obj), Notification.class);
        } else if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification rn = remoteMessage.getNotification();
            notification.title = rn.getTitle();
            notification.content = rn.getBody();
        }

        if (notification.title == null || notification.title.equals("")) {
            notification.title = getString(R.string.app_name);
        }
        notification.id = System.currentTimeMillis();
        notification.read = false;

        // display notification
        prepareImageNotification(notification);

        // save notification to realm db
        database.insertNotification(EntityNotification.getEntity(notification));
    }

    private void prepareImageNotification(final Notification notif) {
        if (notif.image == null || notif.image.equals("")) {
            displayNotificationIntent(notif, null);
            return;
        }

        glideLoadImageFromUrl(this, notif.image, new CallbackImageNotif() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                displayNotificationIntent(notif, bitmap);
            }

            @Override
            public void onFailed(String string) {
                Log.e("onFailed", string);
                displayNotificationIntent(notif, null);
            }
        });
    }

    private void displayNotificationIntent(Notification notif, Bitmap bitmap) {
        Intent intent = new Intent(this, ActivitySplash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        String channelId = getString(R.string.notification_channel_default);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(notif.title);
        builder.setContentText(notif.content);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setDefaults(android.app.Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(android.app.Notification.PRIORITY_HIGH);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notif.content));
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(notif.content));
        }

        // display push notif
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        int unique_id = (int) System.currentTimeMillis();
        notificationManager.notify(unique_id, builder.build());
        playSound();
    }

    private void playSound() {
        //if (!PermissionUtil.isStorageGranted(this)) return;
        try {
            RingtoneManager.getRingtone(this, Uri.parse("content://settings/system/notification_sound")).play();
        } catch (Exception e) {
        }
    }

    // load image with callback
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable myRunnable;

    private void glideLoadImageFromUrl(final Context ctx, final String url, final CallbackImageNotif callback) {

        myRunnable = new Runnable() {
            @Override
            public void run() {
                Glide.with(ctx).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        callback.onSuccess(bitmap);
                        mainHandler.removeCallbacks(myRunnable);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        callback.onFailed("On Load Failed");
                        mainHandler.removeCallbacks(myRunnable);
                    }
                });
            }
        };
        mainHandler.post(myRunnable);
    }

    public interface CallbackImageNotif {

        void onSuccess(Bitmap bitmap);

        void onFailed(String string);

    }
}
