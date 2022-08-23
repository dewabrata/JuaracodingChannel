package com.juaracoding.youtube;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.juaracoding.youtube.adapter.AdapterNotification;
import com.juaracoding.youtube.data.SharedPref;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.room.table.EntityNotification;
import com.juaracoding.youtube.utils.Tools;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ActivityNotification extends AppCompatActivity {

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivityNotification.class);
        activity.startActivity(i);
    }

    public View parent_view;
    private RecyclerView recyclerView;
    private DAO database;
    private SharedPref sharedPref;

    public AdapterNotification adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        parent_view = findViewById(R.id.main_content);
        database = AppDatabase.getDb(this).getDAO();
        sharedPref = new SharedPref(this);

        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_activity_notification);
        Tools.setSystemBarColor(this);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        adapter = new AdapterNotification(this, recyclerView, new ArrayList<EntityNotification>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterNotification.OnItemClickListener() {
            @Override
            public void onItemClick(View view, EntityNotification obj, int pos) {
                showDialogNotification(obj, pos);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) return true;
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setItems(database.getAllNotification());
        noItemChecker();
    }

    private void noItemChecker() {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        if (adapter == null || adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void showDialogNotification(final EntityNotification notification, final int pos) {
        if (!notification.isRead()) {
            notification.setRead(true);
            adapter.updateItem(pos);
            // update database
            database.insertNotification(notification);
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_notification);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((TextView) dialog.findViewById(R.id.title)).setText(notification.getTitle());
        ((TextView) dialog.findViewById(R.id.content)).setText(notification.getContent());
        ImageView image = dialog.findViewById(R.id.image);
        ((TextView) dialog.findViewById(R.id.date)).setText(Tools.getFormattedDateFull(notification.getSavedTime()));
        image.setVisibility(View.GONE);

        if (notification.getImage() != null && !notification.getImage().equals("")) {
            image.setVisibility(View.VISIBLE);
            Glide.with(this).load(notification.getImage()).into(image);
        }


        (dialog.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        (dialog.findViewById(R.id.btn_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteNotification(notification.getSavedTime());
                adapter.removeItem(pos);
                dialog.dismiss();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
                noItemChecker();
            }
        });
        dialog.show();
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_notification));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                database.deleteAllNotification();
                adapter.clearAll();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
                noItemChecker();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

}
