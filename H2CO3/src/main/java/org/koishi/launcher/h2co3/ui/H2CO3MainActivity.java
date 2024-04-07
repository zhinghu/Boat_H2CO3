package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;
import org.koishi.launcher.h2co3.ui.fragment.download.DownloadListFragment;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;
import org.koishi.launcher.h2co3.ui.fragment.manage.ManageFragment;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private H2CO3ToolBar toolbar;
    private NavigationView navigationView;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        toolbar.inflateMenu(R.menu.home_toolbar);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_home);
        getSupportActionBar().setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_home));
        initFragment(new HomeFragment());
        setNavigationItemChecked(0);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav);
    }

    private void initFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }
        currentFragment = fragment;
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        // Handle click events
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_item_home) {
            setNavigationItemChecked(0);
            switchFragment(new HomeFragment(), org.koishi.launcher.h2co3.resources.R.string.title_home);
        } else if (item.getItemId() == R.id.action_item_setting) {
            setNavigationItemChecked(3);
            switchFragment(new ManageFragment(), org.koishi.launcher.h2co3.resources.R.string.title_manage);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (item.isChecked()) {
            return true;
        }

        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.navigation_home) {
            switchFragment(new HomeFragment(), org.koishi.launcher.h2co3.resources.R.string.title_home);
        } else if (itemId == R.id.navigation_directory) {
            switchFragment(new DirectoryFragment(), org.koishi.launcher.h2co3.resources.R.string.title_directory);
        } else if (itemId == R.id.navigation_manage) {
            switchFragment(new ManageFragment(), org.koishi.launcher.h2co3.resources.R.string.title_manage);
        } else if (itemId == R.id.navigation_download) {
            switchFragment(new DownloadListFragment(), org.koishi.launcher.h2co3.resources.R.string.title_download);
        }
        return true;
    }


    private void switchFragment(Fragment fragment, int resID) {
        new Handler().postDelayed(() -> {
            getSupportActionBar().setTitle(getString(resID));
            initFragment(fragment);
        }, 350);
    }

    private void setNavigationItemChecked(int index) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.getItem(index).setChecked(true);
    }
}