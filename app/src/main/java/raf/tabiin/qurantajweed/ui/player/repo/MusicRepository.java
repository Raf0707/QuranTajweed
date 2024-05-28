package raf.tabiin.qurantajweed.ui.player.repo;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    private Context context;

    public MusicRepository(Context context) {
        this.context = context;
    }

    public List<Song> getSongs() {
        return getSortedSongs(makeSongCursor(null, null));
    }

    public List<Song> getSongs(Cursor cursor) {
        List<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songs;
    }

    public List<Song> getSortedSongs(Cursor cursor) {
        List<Song> songs = getSongs(cursor);
        // Сортировка песен в соответствии с настройками пользователя
        // (реализуйте логику сортировки в соответствии с предпочтениями)
        return songs;
    }

    public Song getSong(Cursor cursor) {
        Song song;
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursor(cursor);
        } else {
            song = Song.emptySong;
        }
        if (cursor != null) {
            cursor.close();
        }
        return song;
    }

    public List<Song> getSongs(String query) {
        return getSongs(makeSongCursor(AudioColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"}));
    }

    public Song getSong(long songId) {
        return getSong(makeSongCursor(AudioColumns._ID + "=?", new String[]{String.valueOf(songId)}));
    }

    public List<Song> getSongsByFilePath(String filePath, boolean ignoreBlacklist) {
        return getSongs(
                makeSongCursor(
                        MediaStore.Audio.Media.DATA + "=?",
                        new String[]{filePath},
                        ignoreBlacklist
                )
        );
    }

    private Song getSongFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow((AudioColumns._ID)));
        String title = cursor.getString(cursor.getColumnIndexOrThrow((AudioColumns.TITLE)));
        int trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow((AudioColumns.TRACK)));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow((AudioColumns.YEAR)));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow((AudioColumns.DURATION)));
        String data = cursor.getString(cursor.getColumnIndexOrThrow((MediaStore.Audio.Media.DATA)));
        long dateModified = cursor.getLong(cursor.getColumnIndexOrThrow((AudioColumns.DATE_MODIFIED)));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow((AudioColumns.ALBUM_ID)));
        String albumName = cursor.getString(cursor.getColumnIndexOrThrow((AudioColumns.ALBUM)));
        long artistId = cursor.getLong(cursor.getColumnIndexOrThrow((AudioColumns.ARTIST_ID)));
        String artistName = cursor.getString(cursor.getColumnIndexOrThrow((AudioColumns.ARTIST)));
        String composer = cursor.getString(cursor.getColumnIndexOrThrow((AudioColumns.COMPOSER)));
        String albumArtist = cursor.getString(cursor.getColumnIndexOrThrow(("album_artist")));

        return new Song(
                id,
                title,
                trackNumber,
                year,
                duration,
                data,
                dateModified,
                albumId,
                albumName,
                artistId,
                artistName,
                composer,
                albumArtist
        );
    }

    public Cursor makeSongCursor(String selection, String[] selectionArgs) {
        return makeSongCursor(selection, selectionArgs, false);
    }

    public Cursor makeSongCursor(String selection, String[] selectionArgs, boolean ignoreBlacklist) {
        if (!ignoreBlacklist) {
            // Добавьте логику обработки черного списка
            // ...
        }

        String sortOrder = ""; // Получите порядок сортировки из настроек пользователя
        String[] projection = {
                AudioColumns._ID,
                AudioColumns.TITLE,
                AudioColumns.TRACK,
                AudioColumns.YEAR,
                AudioColumns.DURATION,
                MediaStore.Audio.Media.DATA,
                AudioColumns.DATE_MODIFIED,
                AudioColumns.ALBUM_ID,
                AudioColumns.ALBUM,
                AudioColumns.ARTIST_ID,
                AudioColumns.ARTIST,
                AudioColumns.COMPOSER,
                "album_artist"
        };

        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    // Класс Song для хранения информации о песне
    public static class Song {
        public long id;
        public String title;
        public int trackNumber;
        public int year;
        public long duration;
        public String data;
        public long dateModified;
        public long albumId;
        public String albumName;
        public long artistId;
        public String artistName;
        public String composer;
        public String albumArtist;

        public Song(long id, String title, int trackNumber, int year, long duration, String data,
                    long dateModified, long albumId, String albumName, long artistId,
                    String artistName, String composer, String albumArtist) {
            this.id = id;
            this.title = title;
            this.trackNumber = trackNumber;
            this.year = year;
            this.duration = duration;
            this.data = data;
            this.dateModified = dateModified;
            this.albumId = albumId;
            this.albumName = albumName;
            this.artistId = artistId;
            this.artistName = artistName;
            this.composer = composer;
            this.albumArtist = albumArtist;
        }

        public static Song emptySong = new Song(0, "", 0, 0, 0, "", 0, 0, "", 0, "", "", "");
    }
}