package com.juaracoding.youtube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.juaracoding.youtube.data.Constant;
import com.juaracoding.youtube.data.RemoteConfig;
import com.juaracoding.youtube.data.SharedPref;
import com.juaracoding.youtube.utils.PermissionUtil;
import com.juaracoding.youtube.utils.Tools;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySplash extends AppCompatActivity {

    private SharedPref sharedPref;
    private boolean on_permission_result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = new SharedPref(this);
        Tools.setSystemBarColor(this);
        new RemoteConfig();
    }

    private void startNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivitySplash.this.finish();
                Intent i = new Intent(getApplicationContext(), ActivityMain.class);
                startActivity(i);
            }
        }, Constant.DELAY_TIME_SPLASH);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();

        // permission checker for android M or higher
        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            } else {
                startNextActivity();
            }
        } else {
            startNextActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = shouldShowRequestPermissionRationale(perm);
                sharedPref.setNeverAskAgain(perm, !rationale);
            }
            on_permission_result = true;
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
