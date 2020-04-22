package com.video.player.ui.main;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.video.player.R;
import com.video.player.databinding.ActivityMainBinding;
import com.video.player.repositories.VideosRepository;
import com.video.player.responses.VideoListModel;
import com.video.player.responses.VideosModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        VideoAdapter.PlayListListener {

    private MainViewModel viewModel;

    private List<VideoListModel> videosList;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (binding == null) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        }

        MainViewModelFactory factory = new MainViewModelFactory(this, binding);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        initView();
    }

    private void initView() {
        if (VideosRepository.isNetworkAvailable(this)) {
            viewModel.fetchVideoData(this).observe(this, new Observer<VideosModel>() {
                @Override
                public void onChanged(VideosModel videosModel) {
                    if (videosModel != null) {
                        videosList = videosModel.getVideos();
                        if (videosList != null && videosList.size() > 0) {

                            //viewModel.setData(0, videosList.get(0));

                            setVideoData(0);

                            setVideoAdapter(videosList);
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
        }


        //clciklisteners
        binding.rewind.setOnClickListener(this);
        binding.play.setOnClickListener(this);
        binding.pause.setOnClickListener(this);
        binding.forward.setOnClickListener(this);
    }

    protected void setVideoData(int position) {

        VideoListModel videoListModel = videosList.get(position);

        if (!TextUtils.isEmpty(videoListModel.getTitle()))
            binding.videoTitle.setText(videoListModel.getTitle());


        if (!TextUtils.isEmpty(videoListModel.getUrl())) {
            binding.videoProgress.setVisibility(View.VISIBLE);

            binding.videoView.setVideoURI(Uri.parse(videoListModel.getUrl()));

            viewModel.prepareVideo(position);
        }
    }


    //set playlist adapter
    private void setVideoAdapter(List<VideoListModel> videosList) {

        VideoAdapter videoAdapter = new VideoAdapter(this, videosList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.videosRsv.setLayoutManager(mLayoutManager);
        binding.videosRsv.setAdapter(videoAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rewind:
                viewModel.rewind();
                break;

            case R.id.play:
                viewModel.play();
                break;

            case R.id.pause:
                viewModel.pause();
                break;

            case R.id.forward:
                viewModel.forward(videosList.size());
                break;
        }
    }


    @Override
    public void onClickPlayList(int position) {
        if (videosList != null && videosList.size() > 0) {
            //viewModel.setData(position, videosList.get(position));
            setVideoData(position);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        //accordingly manage the screen size for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.vidGuideline.setGuidelinePercent(1);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.vidGuideline.setGuidelinePercent(0.35f);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.stopMediaPlayer();
    }

    protected int getVideoListSize() {
        return videosList.size();
    }
}
