package com.example.comicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.R;
import com.example.comicapp.activities.ComicIntroActivity;
import com.example.comicapp.config.PictureConfig;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.utils.ActivityUtil;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChineseComicListAdapter extends RecyclerView.Adapter<ChineseComicListAdapter.ViewHolder> {

    private List<Comic> comicList;
    private List<Comic> bannerComics;
    private int itemLayout;
    private Context mContext;

    public ChineseComicListAdapter(List<Comic> comicList, int itemLayout, Context mContext) {
        this.comicList = comicList;
        this.itemLayout = itemLayout;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ChineseComicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Comic comic = comicList.get(i);
        if (i == 0) {
            viewHolder.banner.setVisibility(View.VISIBLE);
            if (bannerComics != null) {
                initBanner(viewHolder.banner);
            }
        } else {
            viewHolder.banner.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions()
                .placeholder(mContext.getResources().getDrawable(R.drawable.ic_pic_preload))
                .override(Target.SIZE_ORIGINAL);
        Glide.with(mContext)
                .load(comic.getComicPosterUrl())
                .apply(options)
                .thumbnail(PictureConfig.PIC_MIDDLE)
                .into(viewHolder.comicImageView);
        viewHolder.comicNameTextView.setText(ActivityUtil.getSubString(comic.getComicName(), 20, true));
        viewHolder.comicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comic.setComicType(0);
                Intent intent = new Intent(mContext, ComicIntroActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_COMIC, comic);
                mContext.startActivity(intent);
            }
        });
    }

    private void initBanner(Banner banner) {
        List<String> urls = new ArrayList<>();
        for (Comic comic : bannerComics) {
            urls.add(comic.getComicPosterUrl());
            Log.d("测试4", "initBanner: " + comic.getComicPosterUrl());
        }
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        })
                .setImages(urls)
                .setDelayTime(4000)
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        switch (position) {
                            case 0:
                        }
                    }
                })
                .start();
    }

    public void updateBannerImgList(List<Comic> bannerComics) {
        this.bannerComics = bannerComics;
        notifyItemChanged(0, 1);
    }

    @Override
    public int getItemCount() {
        return comicList == null ? 0 : comicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView comicImageView;
        public TextView comicNameTextView;
        public Banner banner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comicImageView = itemView.findViewById(R.id.item_main_img);
            comicNameTextView = itemView.findViewById(R.id.item_main_comic_name);
            banner = itemView.findViewById(R.id.item_main_banner);
        }
    }
}
