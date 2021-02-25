package com.tjhost.autoaod.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tjhost.autoaod.ui.base.BaseAppCompatActivity;
import com.tjhost.autoaod.utils.SettingUtil;

public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Debug");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 0) {
            SettingUtil.refreshDebugState(!SettingUtil.getDebugState());
            Toast.makeText(this, "debug mode " +
                    (SettingUtil.getDebugState() ? "on" : "off"), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
