package com.juaracoding.youtube.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /* Permission required for application */
    public static final String[] PERMISSION_ALL = {
            //Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void goToPermissionSettingScreen(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static boolean isAllPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permission = PERMISSION_ALL;
            if (permission.length == 0) return false;
            for (String s : permission) {
                if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("NewApi")
    public static String[] getDeniedPermission(Activity act) {
        List<String> permissions = new ArrayList<>();
        for (int i = 0; i < PERMISSION_ALL.length; i++) {
            int status = act.checkSelfPermission(PERMISSION_ALL[i]);
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(PERMISSION_ALL[i]);
            }
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    @SuppressLint("NewApi")
    public static boolean isGranted(Context ctx, String permission) {
        if (!Tools.needRequestPermission()) return true;
        return (ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isStorageGranted(Context ctx) {
        return isGranted(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public static void showSystemDialogPermission(Fragment fragment, String perm) {
        fragment.requestPermissions(new String[]{perm}, 200);
    }

    @SuppressLint("NewApi")
    public static void showSystemDialogPermission(Activity act, String perm) {
        act.requestPermissions(new String[]{perm}, 200);
    }

    @SuppressLint("NewApi")
    public static void showSystemDialogPermission(Activity act, String perm, int code) {
        act.requestPermissions(new String[]{perm}, code);
    }
}

