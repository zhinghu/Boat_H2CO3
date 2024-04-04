/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-04-03.
 * //
 */

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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.google.android.material.navigation.NavigationView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;
import org.koishi.launcher.h2co3.ui.fragment.manage.ManageFragment;

/**
 * @author caini
 */
public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private H2CO3ToolBar toolbar;
    private NavController navController;

    private NavigationView navigationView;

    private HomeFragment homeFragment;
    private DirectoryFragment directoryFragment;
    private ManageFragment manageFragment;
    private String fragmentId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();


        toolbar.inflateMenu(R.menu.home_toolbar);

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_home);
        getSupportActionBar().setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_home));
        initFragmentHome();
    }


    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav);
    }

    private void initFragmentHome() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        transaction.replace(R.id.nav_host_fragment, homeFragment);
        transaction.commit();
    }

    private void initFragmentDirectory() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
        if (directoryFragment == null) {
            directoryFragment = new DirectoryFragment();
        }
        transaction.replace(R.id.nav_host_fragment, directoryFragment);
        transaction.commit();
    }

    private void initFragmentManage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
        if (manageFragment == null) {
            manageFragment = new ManageFragment();
        }
        transaction.replace(R.id.nav_host_fragment, manageFragment);
        transaction.commit();
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

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.navigation_home) {
            new Handler().postDelayed(() -> {
                getSupportActionBar().setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_home));
                initFragmentHome();
            }, 350);
        } else if (itemId == R.id.navigation_directory) {
            new Handler().postDelayed(() -> {
                getSupportActionBar().setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_directory));
                initFragmentDirectory();
            }, 350);
        } else if (itemId == R.id.navigation_manage) {
            new Handler().postDelayed(() -> {
                getSupportActionBar().setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_manage));
                initFragmentManage();
            }, 350);
        }
        return true;
    }
}