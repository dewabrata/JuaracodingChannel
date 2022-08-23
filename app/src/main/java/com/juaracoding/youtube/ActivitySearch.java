package com.juaracoding.youtube;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.juaracoding.youtube.adapter.AdapterSearchVideo;

import com.juaracoding.youtube.connection.Request;
import com.juaracoding.youtube.connection.callback.CallbackSearchVideo;
import com.juaracoding.youtube.connection.responses.ResponseSearchVideo;
import com.juaracoding.youtube.model.ContentDetails;
import com.juaracoding.youtube.model.SearchItemModel;
import com.juaracoding.youtube.model.Video;
import com.juaracoding.youtube.utils.NetworkCheck;
import com.juaracoding.youtube.utils.OnBottomReachedListener;
import com.juaracoding.youtube.utils.SpacingItemDecoration;
import com.juaracoding.youtube.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearch extends AppCompatActivity {

    private Request request;
    private AdapterSearchVideo mAdapter;
    private List<SearchItemModel> videoList = new ArrayList<>();
    private String nextKey = "";
    private ProgressBar pbLoadMore, pbLoadVideos;
    public Toolbar toolbar;
    private SearchView searchView;
    private String keyword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar(R.id.toolbar);

        pbLoadMore = (ProgressBar) findViewById(R.id.pb_load_more);
        pbLoadVideos = (ProgressBar) findViewById(R.id.pb_load_videos);
        pbLoadVideos.setVisibility(View.GONE);

        initSearchView();
        initRecyclerView();

    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.grey_90));
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey_40));
        searchEditText.setHint(getResources().getString(R.string.input_keyword));

        ImageView magImage = (ImageView) searchView.findViewById(R.id.search_mag_icon);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        ImageView ivClose = (ImageView) searchView.findViewById(R.id.search_close_btn);
        ivClose.setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        ivClose.setImageResource(R.drawable.ic_close);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    Toast.makeText(ActivitySearch.this, getString(R.string.fill_form), Toast.LENGTH_SHORT).show();
                } else {
                    videoList.clear();
                    keyword = query;
                    pbLoadVideos.setVisibility(View.VISIBLE);
                    searchVideo(nextKey);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void setupToolbar(int toolbarId) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.grey_20));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dip2px(this, 10), true));
        mAdapter = new AdapterSearchVideo(this, videoList, new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                pbLoadMore.setVisibility(!nextKey.equals("") ? View.VISIBLE : View.GONE);
                if (!nextKey.equals("")) searchVideo(nextKey);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterSearchVideo.OnItemClickListener() {
            @Override
            public void onItemClick(View view, SearchItemModel obj, int pos) {
                Video video = new Video();
                video.snippet = obj.snippet;
                video.contentDetails = new ContentDetails();
                video.contentDetails.videoId = obj.id.videoId;
                ActivityVideoDetail.navigate(ActivitySearch.this, video);
            }
        });
    }

    private void searchVideo(String nextToken) {
        request = new Request();
        request.searchVideo(keyword, nextToken, new CallbackSearchVideo() {
            @Override
            public void onComplete(ResponseSearchVideo data) {
                pbLoadMore.setVisibility(View.GONE);
                pbLoadVideos.setVisibility(View.GONE);
                if (data != null && data.items != null) {
                    if (!nextKey.equals("")) {
                        videoList.addAll(data.items);
                    } else {
                        videoList = data.items;
                    }
                    mAdapter.updateData(videoList);
                    if (data.nextPageToken != null && data.items.size() > 0) {
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
                pbLoadVideos.setVisibility(View.GONE);
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
