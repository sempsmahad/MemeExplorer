package com.example.memeexplorer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.memeexplorer.R;
import com.example.memeexplorer.memeClasses.Meme;
import com.example.memeexplorer.memeClasses.MemeLab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterGridBasic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Meme> mMemes = new ArrayList<>();
    private MemeLab mMemeLab;

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private Activity activity;


    public interface OnItemClickListener {
        void onItemClick(View view, Meme meme, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public AdapterGridBasic(Activity a, ArrayList<String> d) {
        mMemeLab = MemeLab.get(a);
        activity = a;
//        Toast.makeText(a, ""+d.size(), Toast.LENGTH_SHORT).show();
        for (int k = 0;k<d.size();k++){
            Meme meme = mMemeLab.get(a).getMeme(d.get(k));
            mMemes.add(meme);
        }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_image, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            if (mMemes.get(position) == null){
                return;
            }
            displayImageOriginal(activity, view.image, new File(mMemes.get(position).getLocation()));

            view.lyt_parent.setOnClickListener(view1 -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view1, mMemes.get(position), position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mMemes.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }
    public static void displayImageOriginal(Context ctx, ImageView img, File file) {
        try {
            Glide.with(ctx).asBitmap().load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
        } catch (Exception ignored) {
        }
    }
}


