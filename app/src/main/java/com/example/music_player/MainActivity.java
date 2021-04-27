package com.example.music_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddFragment.AddFragmentListener, SongsFragment.OnFragmentInteractionListener {
        final String ADD_FRAGMENT_TAG = "fragment_add";
        final String PREFS_NAME = "MyPrefsFile";
        final int WRITE_PERMISSION_REQUEST = 1;
        private ImageView songImage;
        private TextView currentTime;
        private TextView totalTime;
        private SeekBar player;
        private ImageButton prev;
        private ImageButton play;
        private ImageButton next;
        private MediaPlayer mediaPlayer;
        private FrameLayout frameLayout;
        private Handler handler= new Handler();

        ArrayList<Song> SongList = new ArrayList<>();
        Song song1 = new Song("One More Cup Of Coffee", "https://www.syntax.org.il/xtra/bob.m4a", "Bob dylan", "https://upload.wikimedia.org/wikipedia/commons/2/28/Joan_Baez_Bob_Dylan_crop.jpg");
        Song song2 = new Song("Sara", "https://www.syntax.org.il/xtra/bob1.m4a", "Bob dylan","https://upload.wikimedia.org/wikipedia/commons/a/a6/Bob_dylan.jpg");
        Song song3 = new Song("The Man In Me", "https://www.syntax.org.il/xtra/bob2.mp3", "Bob dylan","https://upload.wikimedia.org/wikipedia/commons/e/e0/Bob_Dylan_in_Toronto2_crop.jpg");
        Song song4= new Song("Rolling in the Deep","https://drive.google.com/u/0/uc?id=1eRU2Q6CObFG7gfphiYruykil99rLgYCN&export=download","Adele","https://upload.wikimedia.org/wikipedia/commons/7/7c/Adele_2016.jpg");
        Song song5 = new Song("We will rock you","https://drive.google.com/u/0/uc?id=1rsrpQQN8N_XTpxNsoOkXuDVWZ-IsAGz7&export=download","Queen","https://upload.wikimedia.org/wikipedia/commons/d/d1/Label_Single_Queen_Crazy.JPG");
        Song song6 =new Song("See You Again ","https://drive.google.com/u/0/uc?id=1HFej35O30ayi75UsOkWtc5PSYP3kF-Gc&export=download","Wiz Khalifa","https://upload.wikimedia.org/wikipedia/commons/f/fd/Wiz_Khalifa_4%2C_2012.jpg");

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Toast.makeText(this, "There was a problem with external storage", Toast.LENGTH_SHORT).show();
            }

            if(Build.VERSION.SDK_INT>=23) {
                int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                }
            }

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            System.out.println(settings.getBoolean("my_first_time", true));

            if (settings.getBoolean("my_first_time", true)) {
                //the app is being launched for first time, do something
                Log.i("Comments", "First time");

                // first time task
                songToArrayList();

                settings.edit().putBoolean("my_first_time", false).commit();
            } else {
                Log.i("Comments", "NOT First time");
                readFromFile(this);
            }

        //  settings.edit().remove("my_first_time").commit();
            System.out.println(settings.getBoolean("my_first_time", true));
            initViewPager();


    }

    @Override
    protected void onPause() {
        writeToFile(this);
        super.onPause();
    }


    private void initViewPager() {
            ViewPager viewPager= findViewById(R.id.viewPager);
            TabLayout tabLayout=findViewById(R.id.tab_layout);



            readFromFile(this);
            AddFragment addFragment = AddFragment.newInstance();
            SongsFragment songsFragment =  SongsFragment.newInstance(SongList);



            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapter.addFragments(songsFragment, "Songs") ;
            viewPagerAdapter.addFragments(addFragment, "Add new song") ;
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);


   /*     Button addSongBtn = findViewById(R.id.addButton);
        addSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongsFragment songsFragment;
                songsFragment =  SongsFragment.newInstance(SongList);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_song,songsFragment,ADD_FRAGMENT_TAG);
                transaction.commit();
            }
        });

*/


    }

    private void songToArrayList() {
            SongList.add(song1);
            SongList.add(song2);
            SongList.add(song3);
            SongList.add(song4);
            SongList.add(song5);
            SongList.add(song6);
            writeToFile(this);
    }

    @Override
    public void onAdd(Song song) {
        SongList.add(song);
        System.out.println("song add");
        writeToFile(this);

      /* Fragment AddFragment = getSupportFragmentManager().findFragmentByTag(ADD_FRAGMENT_TAG);
        getSupportFragmentManager().beginTransaction().remove(AddFragment).commit();*/
    }

    @Override
    public void onFragmentSetSong(ArrayList<Song> songs) {
        readFromFile(this);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
            private ArrayList<Fragment> fragments;
            private ArrayList<String> titles;

            public ViewPagerAdapter(FragmentManager fm) {
                super(fm);
                this.fragments=new ArrayList<>();
                this.titles=new ArrayList<>();
            }
            void addFragments(Fragment fragment, String title)
            {
                fragments.add(fragment);
                titles.add(title);
            }
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    public void writeToFile(AppCompatActivity activity) {
        ObjectOutputStream oos = null;
        try {

            FileOutputStream fos = activity.openFileOutput("SongList", activity.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(SongList);

            oos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not save new song" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Could not save new song" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void readFromFile(AppCompatActivity activity) {
        try {
            FileInputStream fis = activity.openFileInput("SongList");
            ObjectInputStream ois = new ObjectInputStream(fis);

            SongList =(ArrayList<Song>)ois.readObject();
            ois.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not get song" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not get song" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
