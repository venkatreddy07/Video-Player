package com.video.player.ui.main;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.video.player.R;
import com.video.player.responses.VideoListModel;
import com.video.player.ui.base.BaseActivity;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        VideoAdapter.PlayListListener {

    private List<VideoListModel> videosList;

    private MediaPlayer mediaPlayer;

    private Guideline viewGuideLine;

    private SeekBar seekBar;

    private VideoView videoView;
    private TextView videoTitle;
    private RecyclerView videoRsv;

    private ProgressBar videoProgress;
    private LinearLayout controlLayout;

    private ImageView rewind, play, pause, forward;

    private int currentPosition;


    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

        //viewmodel initialization
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewGuideLine = findViewById(R.id.vid_guideline);

        videoView = findViewById(R.id.videoView);
        videoTitle = findViewById(R.id.video_title);
        videoRsv = findViewById(R.id.videos_rsv);

        seekBar = findViewById(R.id.seekbar);

        videoProgress = findViewById(R.id.video_progress);
        controlLayout = findViewById(R.id.control_layout);

        rewind = findViewById(R.id.rewind);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        forward = findViewById(R.id.forward);

        //fetch video details from assets folder and set the data
        videosList = viewModel.fetchVideoData(this);
        if (videosList != null && videosList.size() > 0) {
            setData(0, videosList.get(0));

            setVideoAdapter(videosList);
        }


        //clciklisteners
        rewind.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        forward.setOnClickListener(this);


    }


    private void setData(int position, VideoListModel videoListModel) {
        videoView.setClickable(false);

        currentPosition = position;

        //scroll selected item
        if (videoRsv.getLayoutManager() != null) {
            videoRsv.getLayoutManager().scrollToPosition(currentPosition);
        }

        if (!TextUtils.isEmpty(videoListModel.getTitle()))
            videoTitle.setText(videoListModel.getTitle());


        if (!TextUtils.isEmpty(videoListModel.getUrl())) {
            videoProgress.setVisibility(View.VISIBLE);

            videoView.setVideoURI(Uri.parse(videoListModel.getUrl()));

            videoListener();
        }
    }

    private void videoListener() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;

                videoProgress.setVisibility(View.GONE);
                videoView.start();

                videoView.setClickable(true);

                //set seekbar max length and update every 1 second
                seekBar.setMax(mediaPlayer.getDuration() / 100);
                seekBarHandler.postDelayed(updateSeekBar, 0);

                seekBarListener();

                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);

                videoClickListeners();
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoProgress.setVisibility(View.VISIBLE);
                playNextVideo();
            }
        });
    }

    private void videoClickListeners() {
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLayout.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlLayout.setVisibility(View.GONE);
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
                    seekBar.setProgress(mCurrentPosition);
                    seekBarHandler.postDelayed(this, 100);
                } catch (IllegalStateException e) {
                }

            }
        }
    };

    //to handle user movements
    private void seekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    //set playlist adapter
    private void setVideoAdapter(List<VideoListModel> videosList) {

        VideoAdapter videoAdapter = new VideoAdapter(this, videosList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        videoRsv.setLayoutManager(mLayoutManager);
        videoRsv.setAdapter(videoAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rewind:
                if (controlLayout.getVisibility() == View.VISIBLE) {
                    controlLayout.setVisibility(View.GONE);
                }

                if (currentPosition > 0) {
                    currentPosition--;
                    setData(currentPosition, videosList.get(currentPosition));
                }
                break;

            case R.id.play:
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                break;

            case R.id.pause:
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);

                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                break;

            case R.id.forward:
                if (controlLayout.getVisibility() == View.VISIBLE) {
                    controlLayout.setVisibility(View.GONE);
                }

                playNextVideo();
                break;
        }
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

    @Override
    public void onClickPlayList(int position) {
        if (videosList != null && videosList.size() > 0) {
            setData(position, videosList.get(position));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        //accordingly manage the screen size for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewGuideLine.setGuidelinePercent(0.95f);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewGuideLine.setGuidelinePercent(0.35f);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            seekBarHandler.removeCallbacks(updateSeekBar);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            seekBarHandler.removeCallbacks(updateSeekBar);
        }
    }
}
