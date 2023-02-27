package com.example.englishaudiorepetationapp;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.englishaudiorepetationapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LAST_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/last_file.txt";

    private Button repeatButton;
    private Button nextButton;
    private TextView textBox;
    private MediaPlayer mediaPlayer;

    private int currentNumber = 1;

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);

//        Toast.makeText(this, "AMIR", Toast.LENGTH_SHORT).show();

        // Initialize the buttons and text box
        repeatButton = findViewById(R.id.repeat_button);
        nextButton = findViewById(R.id.next_button);
        textBox = findViewById(R.id.text_box);

//         Read the current number from the last_file.txt file
        File file = new File(LAST_FILE_NAME);
        if (file.exists()) {
            Log.d("MainActivity", "---------------- Text File is exists");
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int read = inputStream.read(buffer);
                String contents = new String(buffer, 0, read);
                currentNumber = Integer.parseInt(contents);
                Log.d("MainActivity", "---------------- currentNumber: " + currentNumber);
                inputStream.close();
            } catch (IOException e) {
                Log.d("MainActivity", "---------------- Failed to read the text file: " + e);
                e.printStackTrace();
            }
        } else {
            Log.d("MainActivity", "---------------- Text File is NOT exists");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(String.valueOf(currentNumber).getBytes());
                outputStream.close();
            } catch (IOException e) {
                Log.d("MainActivity", "---------------- Failed to write '1' to the text file: " + e);
            }
        }

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudioFile(currentNumber);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentNumber++;
                playAudioFile(currentNumber);

                // Save the current number to last_file.txt
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(String.valueOf(currentNumber).getBytes());
                    outputStream.close();
                    Log.d("MainActivity", "---------------- Succussfully wrote the currentNumber (" + currentNumber + ") to the text file");
                } catch (IOException e) {
                    e.printStackTrace();
                    if (file.exists()) {
                        Log.d("MainActivity", "---------------- The text file is exists");
                    } else {
                        Log.d("MainActivity", "---------------- The text file is NOT exists");
                    }
                    Log.d("MainActivity", "---------------- Failed to write the currentNumber (" + currentNumber + ") to the text file" + e);
                }

            }
        });
    }


    private void playAudioFile(int number) {

//        String filePath = "/storage/sdcard0/MP3/1.mp3";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MP3/" + number + ".mp3";

        File file = new File(filePath);
        if (file.exists()) {
            Log.d("MainActivity", "---------------- File path: " + filePath + "is Exists");
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error playing audio file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
