package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

public class DownloadListFragment extends H2CO3Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Fragment currentFragment;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_list, container, false);
        initUI();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_download);
        initFragment(new MinecraftVersionListFragment());
        setNavigationItemChecked(0);
        return view;
    }

    private void initUI() {
        navigationView = view.findViewById(R.id.nav);
    }

    private void initFragment(Fragment fragment) {
        // 检查是否已经被添加过，避免重复添加
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
            if (currentFragment != null) {
                transaction.remove(currentFragment);
            }
            currentFragment = fragment;
            transaction.replace(R.id.fragmentContainerView, fragment);
            transaction.commit(); // commit 必须在onSaveInstanceState之前
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前Fragment的状态，如果有必要
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

        if (itemId == R.id.navigation_minecraftVersion) {
            switchFragment(new MinecraftVersionListFragment());
        } else if (itemId == R.id.navigation_modList) {
            switchFragment(new ModListFragment());
        } else if (itemId == R.id.navigation_modPackList) {
            switchFragment(new ModPackListFragment());
        } else if (itemId == R.id.navigation_resourcesPack) {
            switchFragment(new ResourcesPackListFragment());
        }
        return true;
    }

    private void switchFragment(Fragment fragment) {
        // 直接在这里进行Fragment的切换，而不是延迟执行
        initFragment(fragment);
    }

    private void setNavigationItemChecked(int index) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.getItem(index).setChecked(true);
    }
}