package com.example.comicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comicapp.R;
import com.example.comicapp.activities.ComicIntroActivity;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.config.PictureConfig;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.entities.ComicCollection;

import java.util.List;

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder> {

    private List<ComicCollection> userComicLikeList;
    private Context mContext;

    public BookShelfAdapter(List<ComicCollection> userComicLikeList, Context mContext) {
        this.userComicLikeList = userComicLikeList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bookshelf, viewGroup, false);
        return new BookShelfAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final ComicCollection userComicLike = userComicLikeList.get(i);
        viewHolder.comicNameTextView.setText(userComicLike.getComicName());
        Glide.with(mContext)
                .load(userComicLike.getComicPosterUrl())
                .thumbnail(PictureConfig.PIC_MIDDLE)
                .into(viewHolder.comicImageView);
        viewHolder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comic comic = new Comic();
                comic.setComicName(userComicLike.getComicName());
                comic.setComicPosterUrl(userComicLike.getComicPosterUrl());
                comic.setComicWebUrl(userComicLike.getComicWebUrl());
                comic.setComicType(userComicLike.getComicType());
                Intent intent = new Intent(mContext, ComicIntroActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_COMIC, comic);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userComicLikeList == null ? 0 : userComicLikeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView comicImageView;
        private TextView comicNameTextView;
        private ConstraintLayout containerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comicImageView = itemView.findViewById(R.id.item_bs_img);
            comicNameTextView = itemView.findViewById(R.id.item_bs_name);
            containerView = itemView.findViewById(R.id.item_bs_container);
        }
    }
}
