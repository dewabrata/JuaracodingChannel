package com.juaracoding.youtube.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.juaracoding.youtube.R;
import com.juaracoding.youtube.room.table.EntityFavorite;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.MyViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, EntityFavorite obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private Context context;
    private List<EntityFavorite> videoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView cover;
        private CardView cvItem;
        private View lytParent;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cover = (ImageView) itemView.findViewById(R.id.iv_item_cover);
            cvItem = (CardView) itemView.findViewById(R.id.cv_item_video);
            lytParent = (View) itemView.findViewById(R.id.lyt_parent);

        }
    }

    public AdapterFavorite(Context context, List<EntityFavorite> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public AdapterFavorite.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View MyItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new AdapterFavorite.MyViewHolder(MyItemView);

    }

    @Override
    public void onBindViewHolder(final AdapterFavorite.MyViewHolder holder, final int position) {
        final EntityFavorite video = videoList.get(position);
        holder.title.setText(Html.fromHtml(video.getTitle()));
        if (!video.getThumbnail().equals(""))
            Glide.with(context).load(video.getThumbnail()).into(holder.cover);

        holder.lytParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener == null) return;
                onItemClickListener.onItemClick(v, video);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateData(List<EntityFavorite> videolist) {
        videoList = videolist;
        this.notifyDataSetChanged();
    }
}
