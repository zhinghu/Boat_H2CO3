package org.koishi.launcher.h2co3.ui.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.IOException;

public class WelcomeFragment extends H2CO3Fragment {


    private final Handler handler = new Handler(Looper.getMainLooper());
    H2CO3TextView title, description;
    LinearProgressIndicator progressIndicator;
    Button nextButton;
    NavController navController;
    private boolean h2co3Launcher = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        title = findViewById(view, R.id.title);
        description = findViewById(view, R.id.description);
        progressIndicator = findViewById(view, R.id.linearProgressIndicator);
        nextButton = findViewById(view, R.id.nextButton);

        boolean isFirstLaunch = H2CO3Tools.getH2CO3Value("isFirstLaunch", true, Boolean.class);

        if (isFirstLaunch) {
            H2CO3Auth.resetUserState();
            H2CO3GameHelper.setDir(H2CO3Tools.MINECRAFT_DIR);
            H2CO3Tools.setH2CO3Value("isFirstLaunch", false);
            title.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        } else {
            checkPermission();
        }
        view.findViewById(R.id.nextButton).setOnClickListener(v -> navController.navigate(R.id.action_welcomeFragment_to_eulaFragment));
    }

    private void checkPermission() {
        if (!XXPermissions.isGranted(requireContext(), Permission.MANAGE_EXTERNAL_STORAGE)) {
            navController.navigate(R.id.action_welcomeFragment_to_permissionRequestFragment);
        } else {
            initRuntimeState();
            if (checkRuntime()) {
                handler.postDelayed(() -> startActivity(new Intent(requireActivity(), H2CO3MainActivity.class)), 1000);
                requireActivity().finish();
            } else {
                navController.navigate(R.id.action_welcomeFragment_to_installFragment);
            }
        }
    }

    private boolean checkRuntime() {
        return h2co3Launcher && java8 && java11 && java17 && java21;
    }

    private void initRuntimeState() {
        try {
            h2co3Launcher = RuntimeUtils.isLatest(H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "/assets/app_runtime/h2co3Launcher");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/java/jre8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java11 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_11_PATH, "/assets/app_runtime/java/jre11");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/java/jre17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java21 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_21_PATH, "/assets/app_runtime/java/jre21");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}