package com.example.englishaudiorepetationapp;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;

import com.example.englishaudiorepetationapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private File text_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
    private File json_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mapping.json");
    private Button repeatButton;
    private Button nextButton;
    private TextView textBox;
    private MediaPlayer mediaPlayer;
    private int currentNumber = 1;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
//        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
//        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 123);


        // Initialize the buttons and text box
        repeatButton = findViewById(R.id.repeat_button);
        nextButton = findViewById(R.id.next_button);
        textBox = findViewById(R.id.text_box);

        loadData();

        JSONObject xx = load_json(json_file);
        jsonObject = xx;
//        Log.d("MainActivity", "---------------- xx: " +  xx);

        show_value_from_json_object(currentNumber, jsonObject, textBox);

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudioFile(currentNumber);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("MainActivity", "---------------- Clicked on 'nextButton', the current currentNumber is: " + currentNumber);
                currentNumber++;
                playAudioFile(currentNumber);
//                // Save the current number to last_file.txt
////                File text_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
//                File text_file = new File("/storage/emulated/0/Download/last_file.txt");
//                try {
//                    Log.d("MainActivity", "---------------- text_file: " + text_file);
//                    FileWriter writer = new FileWriter(text_file);
////                    String currentNumber_str = Integer.toString(currentNumber);
////                    writer.write(currentNumber_str);
//                    writer.write(currentNumber);
//                    writer.write("108");
//                    writer.close();
//                    Log.d("MainActivity", "----------------: " +  text_file);
//
//                    Log.d("MainActivity", "---------------- Succussfully wrote the currentNumber (" + currentNumber + ") to the text file");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    if (text_file.exists()) {
//                        Log.d("MainActivity", "---------------- The text file is exists");
//                    } else {
//                        Log.d("MainActivity", "---------------- The text file is NOT exists");
//                    }
//                    Log.d("MainActivity", "---------------- Failed to write the currentNumber (" + currentNumber + ") to the text file | " + e);
//                }
//                String currentNumber_str = Integer.toString(currentNumber);
//                File text_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
//                Uri ur = Uri.fromFile(text_file);
//                saveFile(currentNumber_str, ur);
                saveFile();

            }
        });
    }


//    public void saveFile(String text, Uri existingSourceUri) {
    public void saveFile() {
        try{
//            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
//            FileOutputStream file = openFileOutput("last_file.txt", MODE_PRIVATE);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);
//            outputStreamWriter.write("222");
//            outputStreamWriter.flush();
//            outputStreamWriter.close();
//            Toast.makeText(MainActivity.this, "File Name: " + file.toURI(), Toast.LENGTH_LONG).show();


//            File file = new File(Environment.getExternalStorageDirectory() + "/Download/last_file.txt");
//            FileOutputStream fos = new FileOutputStream(file);
//
//            String data = "3333";
//            fos.write(data.getBytes());
//            fos.close();
//
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "last_file.txt");
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Now, get the ContentResolver and insert the file into the MediaStore
//            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            ContentResolver resolver = getContentResolver();
            Uri uri = Uri.fromFile(new File(text_file.toString()));
            OutputStream outputStream = resolver.openOutputStream(uri);
            outputStream.write(String.valueOf(185).getBytes());
            outputStream.close();
            Log.d("MainActivity", "---------------- File Overriden succueessfully");
        } catch (Exception e) {
            Log.d("MainActivity", "---------------- File save error: " + e);
        }
    }
    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("MainActivity", "---------------- File save error: " + e);
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void loadData() {
//        Read the current number from the text file
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(String.valueOf(text_file)));
            String x = new String(bytes);
            try{
                String numericOnly = x.replaceAll("[^\\d]", "");
                int num = Integer.parseInt(numericOnly);
                currentNumber = num;
                Log.d("MainActivity", "---------------- Successfully Read " + currentNumber);
            } catch (Exception e) {
                Log.d("MainActivity", "---------------- Error Read " + e);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        if (text_file.exists()) {
////            Log.d("MainActivity", "---------------- Text File is exists");
//            try {
//                byte[] bytes = Files.readAllBytes(Paths.get(String.valueOf(text_file)));
//                String x = new String(bytes);
//                try{
//                    String numericOnly = x.replaceAll("[^\\d]", "");
//                    int num = Integer.parseInt(numericOnly);
//                    currentNumber = num;
//                } catch (Exception e){
////                    Log.d("MainActivity", "---------------- x: " + x);
//                    Log.d("MainActivity", "---------------- >>>>>>>> Error: " + e);
//                }
//                Log.d("MainActivity", "----------------currentNumber read from file: " + currentNumber);
//            } catch (IOException e) {
//                Log.d("MainActivity", "---------------- Failed to read the text file: " + e);
//                e.printStackTrace();
//            }
//        } else {
//            Log.d("MainActivity", "---------------- Text File is NOT exists");
//            try {
//                OutputStream outputStream = resolver.openOutputStream(uri);
//                outputStream.write(String.valueOf(1).getBytes());
//                outputStream.close();
//                Log.d("MainActivity", "---------------- Successfully wrote '1' to the text file: ");
//            } catch (IOException e) {
//                Log.d("MainActivity", "---------------- Failed to write '1' to the text file: " + e);
//                e.printStackTrace();
//            }
//        }
    }
    private void show_value_from_json_object(int currentNumber, JSONObject jsonObject, TextView textBox){
        String currentNumber_str = Integer.toString(currentNumber);
        try {
            String text_to_display = jsonObject.getString(currentNumber_str);
            textBox.setText(text_to_display);
        } catch (Exception e) {
            Log.d("MainActivity", "---------------- xx ERROR: " + e);
        }
    }

    private JSONObject load_json(File json_file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(json_file))) {
            // read the contents of the file into a StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            // create a JSONTokener from the contents of the file
            JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
            // parse the JSONTokener into a JSONObject
//            JSONObject x = new JSONObject(tokener);
//            jsonObject = x;
            JSONObject jsonObject = new JSONObject(tokener);
            return jsonObject;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d("MainActivity", "---------------- Error in json: " + e);
            return jsonObject;
        }
    }

    private void playAudioFile(int number) {

        //        String filePath = "/storage/sdcard0/MP3/1.mp3";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MP3/" + number + ".mp3";

        File file = new File(filePath);
        if (file.exists()) {
//            Log.d("MainActivity", "---------------- File path: " + filePath + " is Exists");
        }
//        Log.d("MainActivity", "---------------- 'playAudioFile' is called, the current currentNumber is: " + currentNumber);
        Toast.makeText(MainActivity.this, String.valueOf(currentNumber), Toast.LENGTH_LONG).show();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error playing audio file", Toast.LENGTH_SHORT).show();
        }

        show_value_from_json_object(currentNumber, jsonObject, textBox);
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