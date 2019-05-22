package com.example.comicapp.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.comicapp.R;
import com.example.comicapp.config.PictureConfig;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.interfaces.LoadDataListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ComicDetailAdapter extends RecyclerView.Adapter<ComicDetailAdapter.ViewHolder> {
    private Chapter chapter;
    private List<String> picsList;
    private int itemLayout;
    private Context mContext;
    private LoadDataListener loadDataListener;

    public ComicDetailAdapter(Chapter chapter, int itemLayout, Context mContext, LoadDataListener loadDataListener) {
        this.chapter = chapter;
        this.itemLayout = itemLayout;
        this.mContext = mContext;
        this.loadDataListener = loadDataListener;
        this.picsList = chapter.getPicsList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ComicDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        try {
            String url = picsList.get(i);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .override(Target.SIZE_ORIGINAL);
            Glide.with(mContext)
                    .load(url)
                    .apply(options)
                    .thumbnail(PictureConfig.PIC_HIGH)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loadDataListener.onLoadFinished(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadDataListener.onLoadFinished(null);
                            return false;
                        }
                    })
                    .into(viewHolder.imageView);
            if (i == picsList.size() - 1) {
                viewHolder.hintView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.hintView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.d("测试", "onBindViewHolder: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return picsList == null ? 0 : picsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView hintView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_comic_detail_img);
            titleView = itemView.findViewById(R.id.item_comic_detail_title);
            hintView = itemView.findViewById(R.id.item_comic_detail_hint);
        }
    }
}
