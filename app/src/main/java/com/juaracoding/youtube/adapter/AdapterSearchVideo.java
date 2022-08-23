package com.juaracoding.youtube.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.juaracoding.youtube.R;
import com.juaracoding.youtube.model.SearchItemModel;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.utils.OnBottomReachedListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterSearchVideo extends RecyclerView.Adapter<AdapterSearchVideo.MyViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, SearchItemModel obj, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private Context context;
    private DAO database;
    private List<SearchItemModel> videoList;
    private OnBottomReachedListener bottomReachedListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView cover;
        private CardView cvItem;
        private LinearLayout llItem;
        private TextView tvWatched;
        private View lytParent;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cover = (ImageView) itemView.findViewById(R.id.iv_item_cover);
            cvItem = (CardView) itemView.findViewById(R.id.cv_item_video);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item_video);
            tvWatched = (TextView) itemView.findViewById(R.id.watched);
            lytParent = (View) itemView.findViewById(R.id.lyt_parent);

        }
    }

    public AdapterSearchVideo(Context context, List<SearchItemModel> videoList, OnBottomReachedListener bottomListener) {
        this.context = context;
        this.videoList = videoList;
        this.bottomReachedListener = bottomListener;
        database = AppDatabase.getDb(context).getDAO();
    }

    @Override
    public AdapterSearchVideo.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View MyItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new AdapterSearchVideo.MyViewHolder(MyItemView);

    }

    @Override
    public void onBindViewHolder(final AdapterSearchVideo.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SearchItemModel video = videoList.get(position);
        if (video.id.videoId == null) {
            holder.llItem.setVisibility(View.GONE);
            holder.cvItem.setVisibility(View.GONE);
            return;
        }
        holder.title.setText(Html.fromHtml(video.snippet.title));
        Glide.with(context).load(video.snippet.thumbnails.high.url).into(holder.cover);
        if (position == videoList.size() - 1) {
            bottomReachedListener.onBottomReached(position);
        }

        boolean watched = database.countWatched(video.id.videoId) > 0;
        holder.tvWatched.setVisibility(watched ? View.VISIBLE : View.GONE);

        holder.lytParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener == null) return;
                onItemClickListener.onItemClick(v, video, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateData(List<SearchItemModel> videolist) {
        videoList = videolist;
        this.notifyDataSetChanged();
    }
}
