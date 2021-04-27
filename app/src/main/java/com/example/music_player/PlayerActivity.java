package com.example.music_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.music_player.ApplicationClass.ACTION_NEXT;
import static com.example.music_player.ApplicationClass.ACTION_PLAY;
import static com.example.music_player.ApplicationClass.ACTION_PREV;
import static com.example.music_player.ApplicationClass.CHANNEL_ID_1;
import static com.example.music_player.ApplicationClass.CHANNEL_ID_2;

public class PlayerActivity  extends AppCompatActivity implements SongsFragment.OnFragmentInteractionListener,ActionPlaying, ServiceConnection {
    private ImageView songImage;
    private TextView currentTime;
    private TextView totalTime;
    private TextView SongName;
    private TextView ArtistName;
    private SeekBar player;
    private ImageView prev;
    private ImageView play;
    private ImageView next;

    private Thread playThread, prevThread, nextThread;
    MusicPlayerService musicPlayerService;
    MediaSessionCompat mediaSessionCompat;
    //static MediaPlayer mediaPlayer;
    int position1=-1;
    private final Handler handler= new Handler();

    static ArrayList<Song> ListOfSongs = new ArrayList <>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
        getIntentMethod();
        initViews();
        player.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayerService!= null && fromUser)
                {
                    musicPlayerService.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override

            public void run() {
                if(musicPlayerService!= null)
                {
                    int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                    player.setProgress(currentPosition);
                    currentTime.setText(formattedTime(currentPosition));

                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    private String formattedTime(int currentPosition) {
        String totalOut;
        String totalNew;
        String seconds =String.valueOf(currentPosition % 60);
        String minutes =String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length()==1)
        {
            return totalNew;
        }
        else return totalOut;
    }


    private void getIntentMethod() {
        position1 = getIntent().getIntExtra("position", -1);
        Intent intent= new Intent(this,MusicPlayerService.class);
        intent.putExtra("servicePosition", position1);
        startService(intent);

    }

    private void initViews() {

        songImage = findViewById(R.id.songImage);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.total);
        player = findViewById(R.id.seekBar);
        prev = findViewById(R.id.prev_btn);
        play = findViewById(R.id.Play);
        next = findViewById(R.id.next_btn);
        SongName = findViewById(R.id.SongNameUpdate);
        ArtistName = findViewById(R.id.ArtistNameUpdate);

        //    player.setMax(100);


        SongName.setText(ListOfSongs.get(position1).getName());
        ArtistName.setText(ListOfSongs.get(position1).getArtist());
        // metaData(ListOfSongs.get(position1).getimageID());

        Glide.with(PlayerActivity.this)
                .asBitmap()
                .load(ListOfSongs.get(position1).getimageID())
                .into(songImage);


    }



    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, this,BIND_AUTO_CREATE);
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(this);
    }

    private void playThreadBtn() {
        playThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       playPauseBtnClicked();
                    }

                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() {
        if (musicPlayerService.isPlaying()) {

            musicPlayerService.pause();
            musicPlayerService.release();
            musicPlayerService.createMediaPlayer(position1);
            player.setMax(musicPlayerService.getDuration()/1000);
            musicPlayerService.OnCompleted();
            PlayerActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (musicPlayerService != null) {
                        int currentPosition = musicPlayerService.getCurrentPosition() / 1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            musicPlayerService.showNotification(R.drawable.ic_baseline_play_circle_outline_24);

        } else {
            musicPlayerService.release();

            musicPlayerService.createMediaPlayer(position1);
            musicPlayerService.start();
            player.setMax(musicPlayerService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicPlayerService!= null)
                    {
                        int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicPlayerService.showNotification(R.drawable.pause);
            play.setImageResource(R.drawable.pause);
        }
    }

    private void prevThreadBtn() {
        prevThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }

                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {

        if(musicPlayerService.isPlaying())
        {
            musicPlayerService.stop();
            musicPlayerService.release();
            position1 =((position1-1)%ListOfSongs.size());
            musicPlayerService.createMediaPlayer(position1);
            musicPlayerService.start();
            SongName.setText(ListOfSongs.get(position1).getName());
            ArtistName.setText(ListOfSongs.get(position1).getArtist());
            try {
                Glide.with(PlayerActivity.this)
                        .asBitmap()
                        .load(ListOfSongs.get(position1).getimageID())
                        .into(songImage);
            }
            catch (Exception e)
            {

            }
            int durationTotal = (musicPlayerService.getDuration()/1000);
            totalTime.setText(formattedTime(durationTotal));
            player.setMax(musicPlayerService.getDuration()/1000);


            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicPlayerService!= null)
                    {
                        int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });

            musicPlayerService.showNotification(R.drawable.pause);
            musicPlayerService.OnCompleted();
            play.setImageResource(R.drawable.pause);
        }
        else {
            musicPlayerService.stop();
            musicPlayerService.release();
            position1 =((position1-1)%ListOfSongs.size());
            musicPlayerService.createMediaPlayer(position1);
            SongName.setText(ListOfSongs.get(position1).getName());
            ArtistName.setText(ListOfSongs.get(position1).getArtist());
            try {
                Glide.with(PlayerActivity.this)
                        .asBitmap()
                        .load(ListOfSongs.get(position1).getimageID())
                        .into(songImage);
            }
            catch (Exception e)
            {

            }
            int durationTotal = (musicPlayerService.getDuration()/1000);
            totalTime.setText(formattedTime(durationTotal));
            player.setMax(musicPlayerService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicPlayerService!= null)
                    {
                        int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
          //  musicPlayerService.start();
            musicPlayerService.OnCompleted();
            musicPlayerService.showNotification(R.drawable.pause);
            play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }

    }

    private void nextThreadBtn() {
        nextThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }

                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if(musicPlayerService.isPlaying())
        {
            musicPlayerService.stop();
            musicPlayerService.release();
            position1 =((position1+1)%ListOfSongs.size());
            musicPlayerService.createMediaPlayer(position1);
            musicPlayerService.start();
            SongName.setText(ListOfSongs.get(position1).getName());
            ArtistName.setText(ListOfSongs.get(position1).getArtist());
            try {
                Glide.with(PlayerActivity.this)
                        .asBitmap()
                        .load(ListOfSongs.get(position1).getimageID())
                        .into(songImage);
            }
            catch (Exception e)
            {

            }

            int durationTotal = (musicPlayerService.getDuration()/1000);
            totalTime.setText(formattedTime(durationTotal));
            player.setMax(musicPlayerService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicPlayerService!= null)
                    {
                        int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
      //      musicPlayerService.start();
            musicPlayerService.showNotification(R.drawable.pause);
            musicPlayerService.OnCompleted();
            play.setImageResource(R.drawable.pause);
        }
        else {
            musicPlayerService.stop();
            musicPlayerService.release();
            position1 =((position1+1)%ListOfSongs.size());
            musicPlayerService.createMediaPlayer(position1);
            SongName.setText(ListOfSongs.get(position1).getName());
            ArtistName.setText(ListOfSongs.get(position1).getArtist());
            try {
                Glide.with(PlayerActivity.this)
                        .asBitmap()
                        .load(ListOfSongs.get(position1).getimageID())
                        .into(songImage);
            }
            catch (Exception e)
            {

            }
            int durationTotal = (musicPlayerService.getDuration()/1000);
            totalTime.setText(formattedTime(durationTotal));
            player.setMax(musicPlayerService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicPlayerService!= null)
                    {
                        int currentPosition = musicPlayerService.getCurrentPosition()/1000;
                        player.setProgress(currentPosition);
                        currentTime.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            //musicPlayerService.start();
            musicPlayerService.showNotification(R.drawable.pause);
            musicPlayerService.OnCompleted();
            play.setImageResource(R.drawable.pause);
        }

    }

    @Override
    public void onFragmentSetSong(ArrayList<Song> songs) {
        ListOfSongs = songs;

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayerService.MyBinder myBinder= (MusicPlayerService.MyBinder) service;
        musicPlayerService =myBinder.getService();
        player.setMax(musicPlayerService.getDuration()/1000);
        SongName.setText(ListOfSongs.get(position1).getName());
        ArtistName.setText(ListOfSongs.get(position1).getArtist());
        musicPlayerService.showNotification(R.drawable.ic_baseline_play_circle_outline_24);
        int durationTotal = (musicPlayerService.getDuration()/1000);
        totalTime.setText(formattedTime(durationTotal));
        musicPlayerService.OnCompleted();
        musicPlayerService.setCallBack(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicPlayerService= null;
    }


}

