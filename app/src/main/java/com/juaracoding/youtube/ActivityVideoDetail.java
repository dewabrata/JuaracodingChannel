package com.juaracoding.youtube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.juaracoding.youtube.data.Constant;
import com.juaracoding.youtube.fragment.FragmentVideoPlayer;
import com.juaracoding.youtube.model.Video;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.room.table.EntityFavorite;
import com.juaracoding.youtube.room.table.EntityWatched;
import com.juaracoding.youtube.utils.Tools;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;

public class ActivityVideoDetail extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";

    // activity transition
    public static void navigate(Activity activity, Video obj) {
        Intent i = new Intent(activity, ActivityVideoDetail.class);
        i.putExtra(EXTRA_OBJECT, obj);
        activity.startActivity(i);
    }

    private Video video;
    private Toolbar toolbar;
    private DAO database;
    private MenuItem favIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        video = (Video) getIntent().getSerializableExtra(EXTRA_OBJECT);
        database = AppDatabase.getDb(this).getDAO();

        TextView tvDesc = (TextView) findViewById(R.id.tv_detail_desc);
        if (video.snippet.description == null || video.snippet.description.equals("")) {
            tvDesc.setText(getString(R.string.no_desc_video));
        } else {
            tvDesc.setText(video.snippet.description);
        }

        ((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(video.snippet.title));
        setupToolbar(R.id.toolbar);

        ((View) findViewById(R.id.lyt_content)).requestFocus();

        try{
            prepareYoutube();
        } catch (Exception e) {

        }
    }

    private void prepareYoutube() {
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);

        if (result != YouTubeInitializationResult.SUCCESS) {
            result.getErrorDialog(this, 0).show();
            return;
        }

        final FragmentVideoPlayer fragment = (FragmentVideoPlayer) getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
        fragment.setVideoId(video.contentDetails.videoId);
        fragment.setOnVideoPlayListener(new FragmentVideoPlayer.OnVideoPlayListener() {
            @Override
            public void onPlaying(String videoId) {
                database.insertWatched(new EntityWatched(videoId, System.currentTimeMillis()));
                if (database.countWatched(videoId) > 0) return;
            }
        });
    }

    public void setupToolbar(int toolbarId) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_video_detail, menu);
        favIcon = menu.findItem(R.id.save);
        if (isFav())
            favIcon.setIcon(R.drawable.ic_bookmark);
        else
            favIcon.setIcon(R.drawable.ic_bookmark_border);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareVideo();
                break;
            case R.id.save:
                if (isFav()) {
                    deleteVideo();
                } else {
                    saveVideo();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareVideo() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, video.snippet.title + " " + Constant.YOUTUBE_URL + video.contentDetails.videoId);
        startActivity(Intent.createChooser(sharingIntent, ""));
    }

    private boolean isFav() {
        boolean isFav = false;
        for (EntityFavorite dt : database.getAllFavorite()) {
            if (dt.getVideoId().equals(video.contentDetails.videoId)) {
                isFav = true;
            }
        }
        return isFav;
    }

    private void saveVideo() {
        database.insertFavorite(EntityFavorite.getEntity(video));
        favIcon.setIcon(R.drawable.ic_bookmark);
    }

    private void deleteVideo() {
        database.deleteFavorite(video.contentDetails.videoId);
        favIcon.setIcon(R.drawable.ic_bookmark_border);
    }

}
