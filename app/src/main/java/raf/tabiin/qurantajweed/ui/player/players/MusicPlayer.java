package raf.tabiin.qurantajweed.ui.player.players;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPaused = false;
            }
        });
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

    public void create(int rawResourceId) {
        mediaPlayer = MediaPlayer.create(context, rawResourceId);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Error occurred while playing audio", Toast.LENGTH_SHORT).show();
                return false;
            }
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
        isPaused = false;
    }

    // Метод для получения текущего прогресса воспроизведения
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    // Метод для получения общей продолжительности аудиофайла
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    // Метод для установки пути источника данных для аудиофайла
    public void setDataSource(String filePath) {
        try {
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to set data source", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для асинхронной подготовки MediaPlayer к воспроизведению
    public void prepareAsync() {
        mediaPlayer.prepareAsync();
    }

    // Метод для установки слушателя для события готовности MediaPlayer к воспроизведению
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mediaPlayer.setOnPreparedListener(listener);
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