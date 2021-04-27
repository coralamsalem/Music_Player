package com.example.music_player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import static android.app.Activity.RESULT_OK;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import static android.content.Context.MODE_PRIVATE;


public class AddFragment extends Fragment{

    final int CAMERA_REQUEST = 1;
    final int PICK_IMAGE = 2;

    private EditText link;
    private EditText SongName;
    private EditText ArtistName;

    private Button takePic;
    private Button chooseFromGallery;
    private Button addBtn;

    Bitmap bitmap;
    String imageToString;
    interface AddFragmentListener {
        void onAdd(Song song);
    }

    AddFragmentListener callBack;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callBack = (AddFragmentListener)context;
        }catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement OnRegisterFragmentListener interface");
        }


    }

    public static AddFragment newInstance() {

        AddFragment addFragment = new AddFragment();
        return addFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        link = view.findViewById(R.id.editText);
        SongName = view.findViewById(R.id.editTextSongName);
        ArtistName = view.findViewById(R.id.editTextArtistName);
        takePic= view.findViewById(R.id.takePic);
        chooseFromGallery = view.findViewById(R.id.chooseFromGallery);
        addBtn = view.findViewById(R.id.addButton);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link1 = link.getText().toString();
                String SongName1 = SongName.getText().toString();
                String Artist = ArtistName.getText().toString();


                Song newSong = new Song(SongName1, link1, Artist, imageToString);
                System.out.println(SongName1);
                System.out.println(link1);
                System.out.println(Artist);
                System.out.println(imageToString);

                callBack.onAdd(newSong);
                Toast.makeText(getActivity() , "Song saved", Toast.LENGTH_LONG).show();
                link.setText("");
                SongName.setText("");
                ArtistName.setText("");


            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap)data.getExtras().get("data");

        }

        else if (requestCode == PICK_IMAGE) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                imageToString = imageUri.toString();
                //songImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
