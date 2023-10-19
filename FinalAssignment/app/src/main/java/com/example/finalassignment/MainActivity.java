package com.example.finalassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int imageCounter = 0;  // Initialize image counter
    private DatabaseHelper db;



    private void saveImageCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ImageCounter", imageCounter);
        editor.apply();
    }

    // imageCounterの値を読み込む
    private void loadImageCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        imageCounter = sharedPreferences.getInt("ImageCounter", 0);  // default value is 0
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        final int REQUEST_PERMISSION = 1;
        db = new DatabaseHelper(this);
        loadImageCounter();

        Button button1 = findViewById(R.id.take);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                }
                dispatchTakePictureIntent();
            }
        });

        Button button2 = findViewById(R.id.gallery);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時のアクションをここに書く
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent); //次やること
            }
        });

        Button button3 = findViewById(R.id.search);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時のアクションをここに書く
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent); //次やること
            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // ここで取得した画像を扱う
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(imageBitmap);
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);

            //handleNewBitmap(imageBitmap);

            List<String> putLabels = new ArrayList<>();
            float[] confidences = new float[5];
            String[] tags = new String[5];



            final TextView labelText = findViewById(R.id.labelText);
            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            // ラベルが正常に取得できた場合の処理を記述します。
                            int i = 0;
                            for (ImageLabel label : labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                // ここで取得したラベル（text）と信頼度（confidence）を使用します。
                                if (i == 0){
                                    labelText.setText("Label: " + text + ", Confidence: " + confidence);
                                }
                                tags[i] = text;
                                confidences[i] = confidence;
                                //Log.d("TAG", "text: " + text + ", tags: " + tags[i]);
                                i++;

                            }
                            Log.d("TAG", "Tags: " + Arrays.toString(tags) + ", Accuracies: " + Arrays.toString(confidences));
                            handleNewBitmap(imageBitmap,tags,confidences);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // ラベルの取得に失敗した場合の処理を記述します。
                        }
                    });

            //Log.d("TAG", "Tags: " + Arrays.toString(tags) + ", Accuracies: " + Arrays.toString(confidences));
            //handleNewBitmap(imageBitmap,tags,confidences);

        }
    }

    private void handleNewBitmap(Bitmap bitmap, String[] tags, float[] accuracies) {
        // File path for the new image
        String imagePath = getExternalFilesDir(null) + "/" + (++imageCounter) + ".png";

        // Save the Bitmap to external storage
        try (FileOutputStream out = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("TAG", "Tags: " + Arrays.toString(tags) + ", Accuracies: " + Arrays.toString(accuracies));
        // Add the image path to the database
        db.addImage(imagePath, tags, accuracies);

        saveImageCounter();
    }

    /*
    private void handleNewBitmap(Bitmap bitmap) {
        // File path for the new image
        String imagePath = getExternalFilesDir(null) + "/" + (++imageCounter) + ".png";

        // Save the Bitmap to external storage
        try (FileOutputStream out = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the image path to the database
        db.addImagePath(imagePath);

        saveImageCounter();
    }

     */
}
