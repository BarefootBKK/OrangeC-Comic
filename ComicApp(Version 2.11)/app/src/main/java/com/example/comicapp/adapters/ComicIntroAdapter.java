package com.example.comicapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.R;
import com.example.comicapp.activities.ComicDetailActivity;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;

import java.util.List;

public class ComicIntroAdapter extends RecyclerView.Adapter<ComicIntroAdapter.ViewHolder> {

    private int itemLayout;
    private Context mContext;
    private Comic comic;
    private List<Chapter> chapterList;

    public ComicIntroAdapter(Comic comic, int itemLayout, Context mContext) {
        this.comic = comic;
        this.itemLayout = itemLayout;
        this.mContext = mContext;
        this.chapterList = comic.getChapterList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ComicIntroAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Chapter chapter = chapterList.get(i);
        viewHolder.chapterTitleView.setText(chapter.getChapterName());
        viewHolder.frameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicDetailActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_CHAPTER, chapter);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_COMIC, comic);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chapterList == null ? 0 : chapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterTitleView;
        public FrameLayout frameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTitleView = itemView.findViewById(R.id.item_cl_chapter_title);
            frameView = itemView.findViewById(R.id.item_cl_frameLayout);
        }
    }
}
