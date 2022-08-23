package com.juaracoding.youtube.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juaracoding.youtube.ActivityVideoDetail;
import com.juaracoding.youtube.R;
import com.juaracoding.youtube.adapter.AdapterFavorite;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.room.table.EntityFavorite;
import com.juaracoding.youtube.utils.SpacingItemDecoration;
import com.juaracoding.youtube.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    private AdapterFavorite mAdapter;
    private List<EntityFavorite> videoList = new ArrayList<>();
    private View root_view;

    public FragmentFavorite() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_favorite, container, false);

        initRecyclerView(root_view);

        return root_view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_video);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dip2px(getActivity(), 10), true));

        mAdapter = new AdapterFavorite(getActivity(), videoList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View view, EntityFavorite obj) {
                try {

                        ActivityVideoDetail.navigate(getActivity(), EntityFavorite.getVideo(obj));

                } catch (Exception e) {
                    ActivityVideoDetail.navigate(getActivity(), EntityFavorite.getVideo(obj));
                }
            }
        });
    }

    private void getFavorite() {
        videoList.clear();
        DAO database = AppDatabase.getDb(getActivity()).getDAO();
        videoList = database.getAllFavorite();
        mAdapter.updateData(videoList);
        noItemChecker();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            getFavorite();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFavorite();
    }

    private void noItemChecker() {
        View lyt_no_item = (View) root_view.findViewById(R.id.lyt_no_item);
        if (mAdapter == null || mAdapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }
}
