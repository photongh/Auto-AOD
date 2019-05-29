package com.tjhost.autoaod.ui.main;

import android.os.Bundle;

import com.tjhost.autoaod.ui.base.BaseAppCompatActivity;

public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainFragment())
                .commit();
    }
}
