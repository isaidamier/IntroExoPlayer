package com.isaid.movingwater;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

    private static final String APP_NAME = MainActivity.class.getSimpleName();
    private static final java.lang.String POS_KEY = "pos";
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        SimpleExoPlayerView exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        exoPlayerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, APP_NAME),
                (DefaultBandwidthMeter) bandwidthMeter);//note the type casting
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //normally we would have this in string.xml or such instead of hardcoding
        String waterUrl = "https://storage.googleapis.com/android-tv/Sample%20videos/" +
                "Google%2B/Google%2B_%20Instant%20Upload.mp4";
        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(waterUrl),
                dataSourceFactory,
                extractorsFactory,
                null, null
        );
        player.prepare(videoSource);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(POS_KEY, player.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long position = savedInstanceState.getLong(POS_KEY);
        player.seekTo(position);
    }


    @Override
    public void onPause() {
        super.onPause();
        if ( Util.SDK_INT <= 23 ) {
            releasePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if ( null != player ) {
            player.release();
            player = null;
        }
    }

}
