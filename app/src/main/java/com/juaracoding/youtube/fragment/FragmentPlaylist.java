package com.juaracoding.youtube.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.juaracoding.youtube.ActivityPlaylistDetail;
import com.juaracoding.youtube.R;
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

public class FragmentPlaylist extends Fragment {

    private Request request;
    private AdapterListVideo mAdapter;
    private List<Video> videoList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String nextKey = "";
    private ProgressBar pbLoadMore;
    private View root_view;

    public FragmentPlaylist() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_playlist, container, false);

        request = new Request();
        pbLoadMore = (ProgressBar) root_view.findViewById(R.id.pb_load_more);

        initRecyclerView(root_view);
        getPlaylist("");

        return root_view;
    }

    private void initRecyclerView(final View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dip2px(getActivity(), 10), true));
        mAdapter = new AdapterListVideo(getActivity(), videoList, "playlist", new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                pbLoadMore.setVisibility(!nextKey.equals("") ? View.VISIBLE : View.GONE);
                if (!nextKey.equals("")) getPlaylist(nextKey);
            }
        });
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterListVideo.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Video obj, int pos) {
                ActivityPlaylistDetail.navigate(getActivity(), obj);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_rv_video);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoList.clear();
                getPlaylist("");
            }
        });
    }

    private void getPlaylist(final String nextToken) {
        request.getPlaylist(nextToken, new CallbackVideo() {
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
                swipeRefreshLayout.setRefreshing(false);
                onFailedChecker();
                noItemChecker();
            }
        });
    }

    private void noItemChecker() {
        View lyt_no_item = (View) root_view.findViewById(R.id.lyt_no_item);
        if (mAdapter == null || mAdapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void onFailedChecker() {
        if (!NetworkCheck.isConnect(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.failed_connect_server), Toast.LENGTH_SHORT).show();
        }
    }
}
