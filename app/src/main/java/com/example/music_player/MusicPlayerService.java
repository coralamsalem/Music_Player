package com.example.music_player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.music_player.ApplicationClass.ACTION_NEXT;
import static com.example.music_player.ApplicationClass.ACTION_PLAY;
import static com.example.music_player.ApplicationClass.ACTION_PREV;
import static com.example.music_player.ApplicationClass.CHANNEL_ID_2;
import static com.example.music_player.PlayerActivity.ListOfSongs;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{


    IBinder mBinder =new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<Song> songList = new ArrayList<>();
   static int position = -1;
    ActionPlaying actionPlaying;
    Uri uri;
    MediaSessionCompat mediaSessionCompat;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");

    }

    void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

     int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

     void setDataSource(String link) {
         try {
             mediaPlayer.setDataSource(link);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }



     void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }


    public class MyBinder extends Binder{
        MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        position= intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");

        if (position != -1) {
            playMedia(position);
        }

        if(actionName != null) {
            switch(actionName) {
                case "playPause":
                    Toast.makeText(this, "playPause", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null) {
                        actionPlaying.playPauseBtnClicked();
                    }

                    break;
                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null) {
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null) {
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void playMedia(int startPosition) {

       songList=ListOfSongs;
        position = startPosition;
        if(mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (songList != null) {
                createMediaPlayer(position);


            }
        } else {
            createMediaPlayer(position);

        }
    }

    void start(){ mediaPlayer.start();
    }
    boolean isPlaying()
    {
         return mediaPlayer.isPlaying();
    }
    void stop()
    {
        mediaPlayer.stop();
    }
    void release()
    {
        mediaPlayer.release();
    }
    int getDuration()
    {
       return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);

    }

    void createMediaPlayer(int positionInner) {


        position = positionInner;

        uri = Uri.parse(songList.get(position).getNLink());
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);


    }


    MediaPlayer returnMediaPlayer()
    {
        return this.mediaPlayer;
    }

    void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
       if (actionPlaying!= null) {
           actionPlaying.nextBtnClicked();
       }
       if(mediaPlayer!=null) {
           createMediaPlayer(position);
           mediaPlayer.start();
           OnCompleted();
       }
    }
    void showNotification(int playPauseBtn){
       //Intent intent = new Intent(this, NotificationReceiver.class);
       // PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,
         //      0);
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREV);
        PendingIntent prevPending = PendingIntent
                .getBroadcast(this,0,prevIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent
                .getBroadcast(this,0,pauseIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent
                .getBroadcast(this,0,nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);




        System.out.println(position);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
               .setContentTitle(ListOfSongs.get(position).getName()).setContentText(ListOfSongs.get(position).getArtist())
                .addAction(R.drawable.prev, "Previous", prevPending)
                .addAction(playPauseBtn,"Pause", pausePending)
                .addAction(R.drawable.next,"Next",nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification) ;

        startForeground(1, notification);


    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
