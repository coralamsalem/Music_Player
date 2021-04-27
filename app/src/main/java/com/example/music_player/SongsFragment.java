package com.example.music_player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;


public class SongsFragment extends Fragment {


    static ArrayList<Song> songs = new ArrayList<>();
    SongAdapter songAdapter;
    RecyclerView recyclerView;
    String link;
    String name;
    String artistName;
    String image;
    PlayerActivity playerActivity = new PlayerActivity();

    public interface OnFragmentInteractionListener  {
        public void onFragmentSetSong(ArrayList<Song> songs);


    }
    public static SongsFragment newInstance(ArrayList<Song> songsList) {

        SongsFragment songsFragment= new SongsFragment();
        songs= songsList;
        return songsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new SongAdapter(getContext(),songs));


        OnFragmentInteractionListener listener = (OnFragmentInteractionListener) playerActivity;
        listener.onFragmentSetSong(songs);
        songAdapter = new SongAdapter(getContext(),songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        songAdapter.setListener(new SongAdapter.MySongListener() {

            @Override
            public void onSongClicked(int position, View view) {

            }

            @Override
            public void onSongLongClicked(int position, View view) {


            }

        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(songs, fromPosition, toPosition);

              //  recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                songAdapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
              //  saveList(MainActivity.this);

                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.RIGHT||direction == ItemTouchHelper.LEFT) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete Song").setMessage("Are you sure you want to delete this song?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    songs.remove(viewHolder.getAdapterPosition());
                                    songAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                   // saveList(MainActivity.this);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    songAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .setCancelable(false)
                            .show();

                   // songs.remove(viewHolder.getAdapterPosition());
                  //  songAdapter.onItemRemoved(viewHolder);

                }
            }
        };



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(songAdapter);
        return view;
    }

}