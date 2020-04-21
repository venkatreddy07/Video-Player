package com.video.player.ui.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.video.player.databinding.ActivityMainBinding;
import com.video.player.repositories.VideosRepository;
import com.video.player.responses.VideoListModel;
import com.video.player.responses.VideosModel;

import java.util.List;

public class MainViewModel extends ViewModel {

    private ActivityMainBinding binding;

    private MediaPlayer mediaPlayer;

    private int currentPosition;

    private List<VideoListModel> videosList;


    MutableLiveData<VideosModel> fetchVideoData(Context context) {
        MutableLiveData<VideosModel> mutableLiveData = new MutableLiveData<>();

        String videoDetails = VideosRepository.getJsonFromAssets(context, "videodetails.json");

        mutableLiveData.postValue(new Gson().fromJson(videoDetails, VideosModel.class));

        return mutableLiveData;
    }


    void setInitValues(ActivityMainBinding binding, List<VideoListModel> videosList) {
        this.binding = binding;
        this.videosList = videosList;
    }

    void setData(int position, VideoListModel videoListModel) {
        binding.videoView.setClickable(false);

        currentPosition = position;

        //scroll selected item
        if (binding.videosRsv.getLayoutManager() != null) {
            binding.videosRsv.getLayoutManager().scrollToPosition(currentPosition);
        }

        if (!TextUtils.isEmpty(videoListModel.getTitle()))
            binding.videoTitle.setText(videoListModel.getTitle());


        if (!TextUtils.isEmpty(videoListModel.getUrl())) {
            binding.videoProgress.setVisibility(View.VISIBLE);

            binding.videoView.setVideoURI(Uri.parse(videoListModel.getUrl()));

            videoListener();
        }
    }

    //start playing video and seekbar initialization
    private void videoListener() {
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;

                binding.videoProgress.setVisibility(View.GONE);
                binding.videoView.start();

                binding.videoView.setClickable(true);

                //set seekbar max length and update every 1 second
                binding.seekbar.setMax(mediaPlayer.getDuration() / 100);
                seekBarHandler.postDelayed(updateSeekBar, 0);

                seekBarListener();

                binding.play.setVisibility(View.GONE);
                binding.pause.setVisibility(View.VISIBLE);

                videoClickListeners();
            }
        });


        binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.videoProgress.setVisibility(View.VISIBLE);
                playNextVideo();
            }
        });
    }

    //handle video controllers toggling
    private void videoClickListeners() {
        binding.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.controlLayout.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.controlLayout.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        });
    }

    //update seekabr on every seconds
    private Handler seekBarHandler = new Handler();
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                try {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 100;
                    binding.seekbar.setProgress(mCurrentPosition);
                    seekBarHandler.postDelayed(this, 100);
                } catch (IllegalStateException e) {
                }
            }
        }
    };

    //to handle user movements
    private void seekBarListener() {
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void rewind() {
        if (binding.controlLayout.getVisibility() == View.VISIBLE) {
            binding.controlLayout.setVisibility(View.GONE);
        }

        if (currentPosition > 0) {
            currentPosition--;
            setData(currentPosition, videosList.get(currentPosition));
        }
    }

    void play() {
        binding.play.setVisibility(View.GONE);
        binding.pause.setVisibility(View.VISIBLE);

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    void pause() {
        binding.play.setVisibility(View.VISIBLE);
        binding.pause.setVisibility(View.GONE);

        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    void forward() {
        if (binding.controlLayout.getVisibility() == View.VISIBLE) {
            binding.controlLayout.setVisibility(View.GONE);
        }

        playNextVideo();
    }

    private void playNextVideo() {
        if (currentPosition >= 0) {
            if (currentPosition == videosList.size() - 1) {
                currentPosition = 0;
            } else {
                currentPosition++;
            }
            setData(currentPosition, videosList.get(currentPosition));
        }
    }

    void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
                seekBarHandler.removeCallbacks(updateSeekBar);
            } catch (IllegalStateException e) {
            }
        }
    }
}
