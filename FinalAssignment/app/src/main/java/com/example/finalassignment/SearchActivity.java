package com.example.finalassignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button button1 = findViewById(R.id.back);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ボタンがクリックされた時のアクションをここに書く
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("key", "value");
                startActivity(intent);
            }
        });

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                // ここで検索処理などを行う
                DatabaseHelper db = new DatabaseHelper(context);
                List<String> imagePaths = db.getImagesByTag(userInput);
                HashMap<String, String[]> imageTags = db.getTagsForImages(imagePaths);
                HashMap<String, float[]> imageAccuracies = db.getAccuraciesForImages(imagePaths);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

                ImageAdapter mAdapter = new ImageAdapter(context, imagePaths, imageTags, imageAccuracies);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }
        });



    }
}
