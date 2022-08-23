package com.juaracoding.youtube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.juaracoding.youtube.adapter.AdapterListVideo;

import com.juaracoding.youtube.connection.Request;
import com.juaracoding.youtube.connection.callback.CallbackVideo;
import com.juaracoding.youtube.connection.responses.ResponseVideos;
import com.juaracoding.youtube.model.Video;
import com.juaracoding.youtube.utils.NetworkCheck;
import com.juaracoding.youtube.utils.OnBottomReachedListener;
import com.juaracoding.youtube.utils.SpacingItemDecoration;
import com.juaracoding.youtube.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlaylistDetail extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";

    // activity transition
    public static void navigate(Activity activity, Video obj) {
        Intent i = new Intent(activity, ActivityPlaylistDetail.class);
        i.putExtra(EXTRA_OBJECT, obj);
        activity.startActivity(i);
    }

    private Video video;
    private Request request;
    private AdapterListVideo mAdapter;
    private List<Video> videoList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String nextKey = "";
    private ProgressBar pbLoadMore;
    private RelativeLayout rlListVideos;
    private int click_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        video = (Video) getIntent().getSerializableExtra(EXTRA_OBJECT);

        request = new Request();
        pbLoadMore = (ProgressBar) findViewById(R.id.pb_load_more);
        rlListVideos = (RelativeLayout) findViewById(R.id.rl_list_video);

        setupToolbar(R.id.toolbar, video.snippet.title);
        initRecyclerView();
        getVideos("");

    }

    public void setupToolbar(int toolbarId, String title) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && 0 <= click_position && click_position < mAdapter.getItemCount()) {
            mAdapter.notifyItemChanged(click_position);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dip2px(this, 10), true));
        mAdapter = new AdapterListVideo(this, videoList, "listVideo", new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                pbLoadMore.setVisibility(!nextKey.equals("") ? View.VISIBLE : View.GONE);
                if (!nextKey.equals("")) getVideos(nextKey);
            }
        });

        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterListVideo.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Video obj, int pos) {

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_rv_video);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoList.clear();
                getVideos("");
            }
        });
    }

    private void getVideos(final String nextToken) {
        if (nextToken.equals("")) swipeRefreshLayout.setRefreshing(true);
        request.getVideos(nextToken, video.id, new CallbackVideo() {
            @Override
            public void onComplete(ResponseVideos data) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (data != null && data.items != null) {
                    if (!nextKey.equals("")) {
                        videoList.addAll(data.items);
                    } else {
                        videoList = data.items;
                    }
                    mAdapter.updateData(videoList);
                    if (data.nextPageToken != null && !data.nextPageToken.equals("")) {
                        nextKey = data.nextPageToken;
                    } else {
                        nextKey = "";
                    }
                } else {
                    onFailedChecker();
                }
                noItemChecker();
            }

            @Override
            public void onFailed() {
                onFailedChecker();
                noItemChecker();
            }
        });
    }

    private void noItemChecker() {
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        if (mAdapter == null || mAdapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void onFailedChecker() {
        if (!NetworkCheck.isConnect(this)) {
            Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.failed_connect_server), Toast.LENGTH_SHORT).show();
        }
    }


}
