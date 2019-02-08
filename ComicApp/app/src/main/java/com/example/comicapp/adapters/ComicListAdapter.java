package com.example.comicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

public class ComicListAdapter extends RecyclerView.Adapter<ComicListAdapter.ViewHolder> {

    private List<Comic> comicList;
    private int itemLayout;
    private Context mContext;

    public ComicListAdapter(List<Comic> comicList, int itemLayout, Context mContext) {
        this.comicList = comicList;
        this.itemLayout = itemLayout;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ComicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Comic comic = comicList.get(i);
        RequestOptions options = new RequestOptions().override(290, 420);
        Glide.with(mContext)
                .load(comic.getComicPosterUrl())
                .apply(options)
                .thumbnail(PictureConfig.PIC_MIDDLE)
                .into(viewHolder.comicImageView);
        viewHolder.comicNameTextView.setText(ActivityUtil.getSubString(comic.getComicName(), 6, true));
        viewHolder.comicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicIntroActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_COMIC, comic);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicList == null ? 0 : comicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView comicImageView;
        public TextView comicNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comicImageView = itemView.findViewById(R.id.item_main_img);
            comicNameTextView = itemView.findViewById(R.id.item_main_comic_name);
        }
    }
}
