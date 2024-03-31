/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigationrail.NavigationRailView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.util.Objects;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener {

    private H2CO3ToolBar toolbar;
    private NavController navController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        NavigationRailView navView = findViewById(R.id.nav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        toolbar.inflateMenu(R.menu.home_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_directory, R.id.navigation_manage)
                .build();
        NavigationUI.setupActionBarWithNavController(this, this.navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, this.navController);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
    }

    @SuppressLint("SetTextI18n")


    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_item_home) {
        } else if (itemId == R.id.action_item_setting) {
        }
        return super.onOptionsItemSelected(item);
    }
}