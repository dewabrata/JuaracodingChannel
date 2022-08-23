package com.juaracoding.youtube.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.juaracoding.youtube.R;
import com.juaracoding.youtube.model.TypeThumbnail;
import com.juaracoding.youtube.model.Video;
import com.juaracoding.youtube.room.AppDatabase;
import com.juaracoding.youtube.room.DAO;
import com.juaracoding.youtube.utils.OnBottomReachedListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterListVideo extends RecyclerView.Adapter<AdapterListVideo.MyViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private Context context;
    private List<Video> videoList;
    private OnBottomReachedListener bottomReachedListener;
    private String fromWhere;
    private DAO database;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView cover;
        private CardView cvItem;
        private TextView tvVideoCount;
        private TextView tvWatched;
        private LinearLayout llVideoCount;
        private View lytParent;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cover = (ImageView) itemView.findViewById(R.id.iv_item_cover);
            cvItem = (CardView) itemView.findViewById(R.id.cv_item_video);
            tvVideoCount = (TextView) itemView.findViewById(R.id.tv_item_videos_count);
            tvWatched = (TextView) itemView.findViewById(R.id.watched);
            llVideoCount = (LinearLayout) itemView.findViewById(R.id.ll_video_count);
            lytParent = (View) itemView.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterListVideo(Context context, List<Video> videoList, String fromWhere, OnBottomReachedListener bottomListener) {
        this.context = context;
        this.videoList = videoList;
        this.bottomReachedListener = bottomListener;
        this.fromWhere = fromWhere;
        database = AppDatabase.getDb(context).getDAO();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View MyItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(MyItemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Video video = videoList.get(position);
        // deleted or private video
        if(video.snippet.thumbnails.standard == null && video.snippet.thumbnails.medium == null && video.snippet.thumbnails.high == null) {
            holder.tvWatched.setVisibility(View.GONE);
            holder.title.setText(R.string.deleted_or_private);
            holder.lytParent.setVisibility(View.GONE);
            return;
        } else {
            holder.lytParent.setVisibility(View.VISIBLE);
        }
        if (video.snippet.thumbnails != null) {
            TypeThumbnail t = video.snippet.thumbnails.high;
            if (t == null) t = video.snippet.thumbnails.medium;
            if (t == null) t = video.snippet.thumbnails.standard;
            Glide.with(context).load(t.url).into(holder.cover);
        }
        holder.title.setText(Html.fromHtml(video.snippet.title));
        if (position == videoList.size() - 1) {
            bottomReachedListener.onBottomReached(position);
        }

        if (fromWhere.equals("playlist")) {
            holder.llVideoCount.setVisibility(View.VISIBLE);
            String countVideo = video.contentDetails.itemCount > 1 ? context.getString(R.string.more_than_one_video) : context.getString(R.string.one_video);
            holder.tvVideoCount.setText("" + video.contentDetails.itemCount + " " + countVideo);
            holder.tvWatched.setVisibility(View.GONE);
        } else {
            holder.llVideoCount.setVisibility(View.GONE);
            boolean watched = database.countWatched(video.contentDetails.videoId) > 0;
            holder.tvWatched.setVisibility(watched ? View.VISIBLE : View.GONE);
        }

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

    public void updateData(List<Video> videolist) {
        videoList = videolist;
        this.notifyDataSetChanged();
    }
}
