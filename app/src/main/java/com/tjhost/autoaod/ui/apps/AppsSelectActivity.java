package com.tjhost.autoaod.ui.apps;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.tjhost.autoaod.ui.base.BaseAppCompatActivity;

public class AppsSelectActivity extends BaseAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new AppsSelectFragment())
                    .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
