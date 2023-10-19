package com.example.finalassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context context;
    private List<String> imagePaths;
    private HashMap<String, String[]> imageTags;
    private HashMap<String, float[]> imageAccuracies;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView myImage;
        public TextView imageName;
        public TextView[] tagTexts;
        public TextView[] accuracyTexts;

        public MyViewHolder(View view) {
            super(view);
            myImage = (ImageView) view.findViewById(R.id.my_image);
            imageName = (TextView) view.findViewById(R.id.image_name);
            tagTexts = new TextView[5];
            accuracyTexts = new TextView[5];

            tagTexts[0] = view.findViewById(R.id.tag1);
            tagTexts[1] = view.findViewById(R.id.tag2);
            tagTexts[2] = view.findViewById(R.id.tag3);
            tagTexts[3] = view.findViewById(R.id.tag4);
            tagTexts[4] = view.findViewById(R.id.tag5);

            accuracyTexts[0] = view.findViewById(R.id.probability1);
            accuracyTexts[1] = view.findViewById(R.id.probability2);
            accuracyTexts[2] = view.findViewById(R.id.probability3);
            accuracyTexts[3] = view.findViewById(R.id.probability4);
            accuracyTexts[4] = view.findViewById(R.id.accuracies5);
        }
    }

    public ImageAdapter(Context context, List<String> imagePaths, HashMap<String, String[]> imageTags, HashMap<String, float[]> imageAccuracies) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.imageTags = imageTags;
        this.imageAccuracies = imageAccuracies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String path = imagePaths.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.myImage.setImageBitmap(bitmap);

        File file = new File(path);
        String imageName = file.getName();
        holder.imageName.setText(imageName);

        String[] tags = imageTags.get(path);
        float[] accuracies = imageAccuracies.get(path);

        //Log.d("TAG", "Tags: " + Arrays.toString(tags) + ", Accuracies: " + Arrays.toString(accuracies));


        for (int i = 0; i < 5; i++) {
            holder.tagTexts[i].setText(tags[i]);
            holder.accuracyTexts[i].setText(String.valueOf(accuracies[i]));
        }
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

}
