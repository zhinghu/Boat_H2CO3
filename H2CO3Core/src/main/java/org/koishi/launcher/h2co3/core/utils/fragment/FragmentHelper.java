package org.koishi.launcher.h2co3.core.utils.fragment;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class FragmentHelper {
    public NavController findNavController(FragmentManager fragmentManager, int id) {
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(id);
        return navHostFragment.getNavController();
    }
}
