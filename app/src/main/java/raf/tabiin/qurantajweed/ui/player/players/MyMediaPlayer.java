package raf.tabiin.qurantajweed.ui.player.players;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyMediaPlayer {
    private static MediaPlayer instance;
    private MediaPlayer mediaPlayer;
    private String filePath;
    private int currentPosition;
    private SeekBar seekBar;

    int counterClick = 0;

    boolean repeatOne = false;

    public MyMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentPosition = progress;
                    mediaPlayer.seekTo(currentPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Ничего не делаем при начале перемещения ползунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Ничего не делаем при окончании перемещения ползунка
            }
        });
    }

    public void play(String filePath) {
        //mediaPlayer.reset();
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            mediaPlayer.start();
            this.filePath = filePath;
            if (seekBar != null) {
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
            //mediaPlayer.reset();
        }
    }

    public void resume() {
        if (!mediaPlayer.isPlaying() && filePath != null) {
            mediaPlayer.reset();
            try {
                //mediaPlayer.reset();
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                updateSeekBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        filePath = null;
        currentPosition = 0;
        if (seekBar != null) {
            seekBar.setProgress(0);
        }
    }

    public void release() {
        stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void repeatOne() {
        // повторение одной композиции
        //mediaPlayer.start();
        /*counterClick++;

        if (counterClick % 2 == 1) { // исправить
            //mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        mediaPlayer.stop();*/

        repeatOne = true;

    }

    private void updateSeekBar() {
        if (seekBar != null) {
            if (mediaPlayer.getCurrentPosition() != 1) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition() - 1);
            }
            if (mediaPlayer.isPlaying()) {
                Runnable runnable = this::updateSeekBar;
                seekBar.postDelayed(runnable, 100);
            }
        }
    }

    public void onComplete(ImageButton imageButton, int drawable) {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!repeatOne) {
                    imageButton.setImageResource(drawable);
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                } else {
                    // Если включено повторение, то запустите аудио снова
                    mediaPlayer.start();
                }
            }
        });
    }

    public void playFromAssets(Context context, String assetFilePath) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(assetFilePath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.prepare();
            mediaPlayer.start();

            // Другой ваш код
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlay() {
        return mediaPlayer.isPlaying();
    }
}