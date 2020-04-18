package com.android.videoplayernoapi;

/**
 * Created by Surendar D on 18/04/20.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.TimeUtilities;

class CustomPlayerUiController extends AbstractYouTubePlayerListener implements YouTubePlayerFullScreenListener {

    private final View playerUi;
    private boolean isPlaying = false;
    private Context context;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private boolean seekBarTouchStarted = false;
    private int newSeekBarProgress = -1;
    private View panel;
    private View progressbar;
    private TextView videoCurrentTimeTextView;
    private TextView videoDurationTextView;
    private SeekBar seekBar;

    private final YouTubePlayerTracker playerTracker;
    private boolean fullscreen = false;

    CustomPlayerUiController(Context context, View customPlayerUi, YouTubePlayer youTubePlayer, YouTubePlayerView youTubePlayerView) {
        this.playerUi = customPlayerUi;
        this.context = context;
        this.youTubePlayer = youTubePlayer;
        this.youTubePlayerView = youTubePlayerView;

        playerTracker = new YouTubePlayerTracker();
        youTubePlayer.addListener(playerTracker);

        initViews(customPlayerUi);
    }

    private void initViews(View playerUi) {
        panel = playerUi.findViewById(R.id.panel);
        progressbar = playerUi.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        videoCurrentTimeTextView = playerUi.findViewById(R.id.video_current_time);
        videoDurationTextView = playerUi.findViewById(R.id.video_duration);
        seekBar = playerUi.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoCurrentTimeTextView.setText(TimeUtilities.formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarTouchStarted = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPlaying)
                    newSeekBarProgress = seekBar.getProgress();

                if (youTubePlayer != null)
                    youTubePlayer.seekTo(seekBar.getProgress());
                seekBarTouchStarted = false;
            }
        });
        final Button playPauseButton = playerUi.findViewById(R.id.play_pause_button);
        final Button playFor = playerUi.findViewById(R.id.play_fro);
        final Button playRev = playerUi.findViewById(R.id.play_rev);
        Button enterExitFullscreenButton = playerUi.findViewById(R.id.enter_exit_fullscreen_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayer.pause();
                    playPauseButton.setBackgroundResource(R.drawable.ic_play);
                } else {
                    youTubePlayer.play();
                    playPauseButton.setBackgroundResource(R.drawable.ic_pause);
                }
            }
        });

        playFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying)
                    newSeekBarProgress = seekBar.getProgress() + 10;

                if (youTubePlayer != null)
                    youTubePlayer.seekTo(seekBar.getProgress() + 10);
                seekBarTouchStarted = false;
            }
        });

        playRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying)
                    newSeekBarProgress = seekBar.getProgress() - 10;
                if (youTubePlayer != null)
                    youTubePlayer.seekTo(seekBar.getProgress() - 10);
                seekBarTouchStarted = false;
            }
        });

        enterExitFullscreenButton.setVisibility(View.GONE);
        enterExitFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen) youTubePlayerView.exitFullScreen();
                else youTubePlayerView.enterFullScreen();

                fullscreen = !fullscreen;
            }
        });
    }

    @Override
    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
        newSeekBarProgress = -1;
        if (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        else if (state == PlayerConstants.PlayerState.BUFFERING)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
        //videoCurrentTimeTextView.setText(second + "");
        if (seekBarTouchStarted)
            return;

        if (newSeekBarProgress > 0 && !TimeUtilities.formatTime(second).equals(TimeUtilities.formatTime(newSeekBarProgress)))
            return;

        newSeekBarProgress = -1;
        seekBar.setProgress((int) second);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
        videoDurationTextView.setText(duration / 100 + "");
        seekBar.setMax((int) duration);
    }

    @Override
    public void onYouTubePlayerEnterFullScreen() {
        ViewGroup.LayoutParams viewParams = playerUi.getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerUi.setLayoutParams(viewParams);
    }

    @Override
    public void onYouTubePlayerExitFullScreen() {
        ViewGroup.LayoutParams viewParams = playerUi.getLayoutParams();
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerUi.setLayoutParams(viewParams);
    }
}

