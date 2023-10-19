package com.example.finalassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Button button1 = findViewById(R.id.back);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時のアクションをここに書く
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent);
            }
        });

        DatabaseHelper db = new DatabaseHelper(this);
        List<String> imagePaths = db.getAllImagePaths();
        HashMap<String, String[]> imageTags = db.getAllImageTags();
        HashMap<String, float[]> imageAccuracies = db.getAllImageAccuracies();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        ImageAdapter mAdapter = new ImageAdapter(this, imagePaths, imageTags, imageAccuracies);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }
}