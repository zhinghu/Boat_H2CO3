package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

public class EditVersionFragment extends H2CO3Fragment {

    TextInputEditText versionNameEditText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_edit_version, container, false);
        initView();
        Bundle args = getArguments();
        System.out.println(args);
        if (args != null) {
            String versionName = args.getString("versionName");
            versionNameEditText.setText(versionName);
        }
        return view;
    }

    private void initView() {
        versionNameEditText = findViewById(view, R.id.dialog_dir_name);
    }

}