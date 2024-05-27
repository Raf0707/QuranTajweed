package raf.tabiin.qurantajweed.ui.player.players;

import android.media.MediaPlayer;

import java.io.IOException;

public class StandartMediaPlayer {
    private MediaPlayer mediaPlayer;
    private int currentPosition;

    static MediaPlayer instance;

    public StandartMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    /*public void play(String fileUrl) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    public void play(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to play the audio", Toast.LENGTH_SHORT).show();
        }
    }


    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        currentPosition = 0;
    }

    public void release() {
        stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

}