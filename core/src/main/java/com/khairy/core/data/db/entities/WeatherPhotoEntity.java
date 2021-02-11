package com.khairy.core.data.db.entities;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.khairy.core.R;

import java.io.File;

@Entity(tableName = "weatherPhoto")
public class WeatherPhotoEntity {
    String name;
    @PrimaryKey
    @NonNull
    String photoPath;

    public WeatherPhotoEntity(String name, @NonNull String photoPath) {
        this.name = name;
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @BindingAdapter("displayPhoto")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view).
                load(new File(imageUrl)).
                error(R.drawable.placeholder).
                into(view);
    }
}
