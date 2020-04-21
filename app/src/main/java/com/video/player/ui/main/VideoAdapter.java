package com.video.player.ui.main;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.video.player.R;
import com.video.player.responses.VideoListModel;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;
    private List<VideoListModel> videosList;
    private PlayListListener listener;

    public VideoAdapter(Context context, List<VideoListModel> videosList, PlayListListener listener) {
        this.context = context;
        this.videosList = videosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, final int position) {
        VideoListModel videoListModel = videosList.get(position);

        if (videoListModel != null) {

            if (!TextUtils.isEmpty(videoListModel.getThumb())) {
                Glide.with(context)
                        .load(videoListModel.getThumb())
                        .into(holder.videoImage);
            }

            if (!TextUtils.isEmpty(videoListModel.getTitle())) {
                holder.videoTitle.setText(videoListModel.getTitle());
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClickPlayList(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView videoImage;
        private TextView videoTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);

            videoImage = itemView.findViewById(R.id.video_image);
            videoTitle = itemView.findViewById(R.id.video_title);
        }
    }

    public interface PlayListListener {
        void onClickPlayList(int position);
    }
}
