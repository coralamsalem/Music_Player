package com.example.music_player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

        private ArrayList<Song> songs;
        private Context context;
        private MySongListener listener;

    public void onItemMoved(int adapterPosition, int adapterPosition1) {
        Collections.swap(songs, adapterPosition, adapterPosition1);
        this.notifyItemMoved(adapterPosition,adapterPosition1);
    }



    public void onItemRemoved(RecyclerView.ViewHolder viewHolder) {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("position", viewHolder.getAdapterPosition());
        context.startActivity(intent);
      //  songs.remove(viewHolder.getAdapterPosition());
      //  this.notifyItemRemoved(viewHolder.getAdapterPosition());
    }


    interface MySongListener {
            void onSongClicked(int position, View view);
            void onSongLongClicked(int position,View view);
        }
        public void setListener(MySongListener listener) {
            this.listener = listener;
        }

        public SongAdapter(Context context,ArrayList<Song> countries) {
            this.songs = countries;
            this.context = context;
        }


        public class SongViewHolder extends RecyclerView.ViewHolder {

            TextView songName;
            TextView artistName;
            ImageView image;


            public SongViewHolder(View itemView) {
                super(itemView);

                songName = itemView.findViewById(R.id.Song_name);
                artistName = itemView.findViewById(R.id.artist_name);
                image = itemView.findViewById(R.id.song_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(listener!=null)
                            listener.onSongClicked(getAdapterPosition(),view);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(listener!=null)
                            listener.onSongLongClicked(getAdapterPosition(),view);
                        return false;
                    }
                });
            }
        }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.song_cell,parent,false);
            SongViewHolder songViewHolder = new SongViewHolder(view);
            return songViewHolder;
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, int position) {
            Song song = songs.get(position);
            holder.songName.setText(song.getName());
            holder.artistName.setText(song.getArtist());
            Glide.with(holder.image.getContext())
                    .load(song.getimageID())
                    .into(holder.image);


            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PlayerActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });


        }


        @Override
        public int getItemCount() {
            return songs.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);

    }



}
