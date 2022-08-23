package com.juaracoding.youtube.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juaracoding.youtube.R;
import com.juaracoding.youtube.room.table.EntityNotification;
import com.juaracoding.youtube.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EntityNotification> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, EntityNotification obj, int position);
    }

    public void setOnItemClickListener(final AdapterNotification.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterNotification(Context context, RecyclerView view, List<EntityNotification> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView content;
        public TextView date;
        public View read;
        public MaterialRippleLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            read = (View) v.findViewById(R.id.read);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        vh = new AdapterNotification.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterNotification.OriginalViewHolder) {
            final EntityNotification n = items.get(position);
            AdapterNotification.OriginalViewHolder vItem = (AdapterNotification.OriginalViewHolder) holder;
            vItem.title.setText(n.getTitle());
            vItem.content.setText(n.getContent());
            vItem.date.setText(Tools.getFormattedDateSimple(n.getSavedTime()));
            if (!n.isRead()) {
                vItem.read.setVisibility(View.VISIBLE);
            } else {
                vItem.read.setVisibility(View.GONE);
            }
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, n, position);
                    }
                }
            });
        }
    }

    // Return the size of your data set ( invoked by the layout manager )
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<EntityNotification> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.items.clear();
        notifyDataSetChanged();
    }
}