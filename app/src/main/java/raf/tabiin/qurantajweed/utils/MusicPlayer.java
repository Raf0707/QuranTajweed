package raf.tabiin.qurantajweed.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MusicPlayer {
    private Context context;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private SeekBar seekBar;
    private boolean isPaused = false;
    private int pausedPosition = 0;

    public MusicPlayer(Context context, SeekBar seekBar) {
        this.context = context;
        this.seekBar = seekBar;
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        setupSeekBar();
    }

    // Метод для настройки SeekBar
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Метод для проигрывания аудиофайла по его пути
    public void play(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            if (isPaused) {
                mediaPlayer.seekTo(pausedPosition);
                isPaused = false;
            } else {
                mediaPlayer.start();
            }
            startUpdatingProgress();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to play the audio", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для паузы воспроизведения
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            pausedPosition = mediaPlayer.getCurrentPosition();
        }
    }

    // Метод для остановки проигрывания
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        stopUpdatingProgress();
    }

    // Метод для получения текущего прогресса воспроизведения
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    // Метод для получения общей продолжительности аудиофайла
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    // Метод для запуска обновления прогресса воспроизведения
    private void startUpdatingProgress() {
        handler.postDelayed(updateProgressTask, 1000); // Обновлять каждую секунду
    }

    // Метод для остановки обновления прогресса воспроизведения
    private void stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressTask);
    }

    // Задача для обновления прогресса воспроизведения
    private Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            int currentPosition = getCurrentPosition();
            int duration = getDuration();
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            handler.postDelayed(this, 1000); // Повторно запускаем через 1 секунду
        }
    };
}