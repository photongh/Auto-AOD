package com.tjhost.autoaod.ui.donate;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tjhost.autoaod.BuildConfig;
import com.tjhost.autoaod.R;
import com.tjhost.autoaod.utils.Util;

public class BuyCoffeeActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coffee);
        setTitle(getTitle() + "v"+ BuildConfig.VERSION_NAME);
        imageView = findViewById(R.id.qrimg);
        imageView.setImageBitmap(Util.f2(this));
    }
}
