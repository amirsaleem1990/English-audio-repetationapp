package com.example.englishaudiorepetationapp;
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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
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
//    private static final String LAST_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/last_file.txt";

    private Button repeatButton;
    private Button nextButton;
    private TextView textBox;
    private MediaPlayer mediaPlayer;

    private int currentNumber = 1;
    private JSONObject jsonObject;

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int PERMISSIONS_REQUEST_CODE = 123;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);

        // Initialize the buttons and text box
        repeatButton = findViewById(R.id.repeat_button);
        nextButton = findViewById(R.id.next_button);
        textBox = findViewById(R.id.text_box);

        // First, create the file object for the file you want to save
        File text_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
        Log.d("MainActivity", "----------------: " +  text_file);

        // Next, create the ContentValues object with the metadata for the file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "last_file.txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Now, get the ContentResolver and insert the file into the MediaStore
        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);





        // Read the current number from the text file
        if (text_file.exists()) {
            Log.d("MainActivity", "---------------- Text File is exists");
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(String.valueOf(text_file)));
                String x = new String(bytes);
                try{
                    String numericOnly = x.replaceAll("[^\\d]", "");
                    int num = Integer.parseInt(numericOnly);
                    currentNumber = num;
                } catch (Exception e){
                    Log.d("MainActivity", "---------------- x: " + x);
                    Log.d("MainActivity", "---------------- >>>>>>>> Error: " + e);
                }
                Log.d("MainActivity", "----------------currentNumber read from file: " + currentNumber);
            } catch (IOException e) {
                Log.d("MainActivity", "---------------- Failed to read the text file: " + e);
                e.printStackTrace();
            }
        } else {
            Log.d("MainActivity", "---------------- Text File is NOT exists");
            try {
                OutputStream outputStream = resolver.openOutputStream(uri);
                outputStream.write(String.valueOf(1).getBytes());
                outputStream.close();
                Log.d("MainActivity", "---------------- Successfully wrote '1' to the text file: ");
            } catch (IOException e) {
                Log.d("MainActivity", "---------------- Failed to write '1' to the text file: " + e);
                e.printStackTrace();
            }
        }

        File json_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mapping.json");

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
                Log.d("MainActivity", "---------------- Clicked on 'nextButton', the current currentNumber is: " + currentNumber);
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
                String currentNumber_str = Integer.toString(currentNumber);
                File text_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "last_file.txt");
                Uri ur = Uri.fromFile(text_file);
                saveFile(currentNumber_str, ur);
            }
        });
    }

    public void saveFile(String text, Uri existingSourceUri)
    {
        try {
            ContentResolver cr = getContentResolver();
            OutputStream os = cr.openOutputStream(existingSourceUri, "wt");
            os.write(text.getBytes());
            os.flush();
            os.close();
            Log.d("MainActivity", "---------------- File Overriden succueessfully");

        } catch (Exception e) {
            Log.d("MainActivity", "---------------- File save error: " + e);
        }
    }
    private void show_value_from_json_object(int currentNumber, JSONObject jsonObject, TextView textBox){
        String currentNumber_str = Integer.toString(currentNumber);
        try {
            String text_to_display = currentNumber + "-" + jsonObject.getString(currentNumber_str);
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
            Log.d("MainActivity", "---------------- File path: " + filePath + "is Exists");
        }
        Log.d("MainActivity", "---------------- 'playAudioFile' is called, the current currentNumber is: " + currentNumber);

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