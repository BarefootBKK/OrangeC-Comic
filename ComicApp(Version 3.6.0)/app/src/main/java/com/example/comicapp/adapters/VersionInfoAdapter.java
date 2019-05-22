package com.example.comicapp.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comicapp.R;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.entities.VersionInfo;

import java.util.List;

public class VersionInfoAdapter extends RecyclerView.Adapter<VersionInfoAdapter.ViewHolder> {

    private List<APKUpdate> versionInfoList;

    public VersionInfoAdapter(List<APKUpdate> versionInfoList) {
        this.versionInfoList = versionInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_version, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        APKUpdate versionInfo = versionInfoList.get(i);
        viewHolder.version.setText(versionInfo.getVersion());
        viewHolder.updateAt.setText("发布时间：" + versionInfo.getCreatedAt());
        viewHolder.versionInfo.setText(versionInfo.getUpdateMessage());
        if (i == versionInfoList.size() - 1) {
            viewHolder.hint.setVisibility(View.VISIBLE);
        } else {
            viewHolder.hint.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return versionInfoList == null ? 0 : versionInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView version;
        private TextView updateAt;
        private TextView versionInfo;
        private ConstraintLayout hint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            version = itemView.findViewById(R.id.item_version_title);
            updateAt = itemView.findViewById(R.id.item_version_updateAt);
            versionInfo = itemView.findViewById(R.id.item_version_info);
            hint = itemView.findViewById(R.id.item_version_bottom_hint);
        }
    }
}
