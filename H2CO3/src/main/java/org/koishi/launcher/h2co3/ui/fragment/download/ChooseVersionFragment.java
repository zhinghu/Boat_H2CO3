/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.download;

import static org.koishi.launcher.h2co3.core.H2CO3Settings.getDownloadSource;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.H2CO3DownloadUtils;
import org.koishi.launcher.h2co3.core.utils.Version;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChooseVersionFragment extends H2CO3Fragment {

    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;

    private List<Version> versionList;
    private List<Version> filteredList;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private LinearProgressIndicator progressIndicator;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_mc_choose_version, container, false);
        navController = Navigation.findNavController(requireParentFragment().requireView());
        initView(view);
        initListeners();

        versionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        versionAdapter = new VersionAdapter(filteredList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(versionAdapter);

        fetchVersionsFromApi();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.loadingversionFileListView1);
        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        progressIndicator = view.findViewById(R.id.progressIndicator);
    }

    private void initListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterVersions(checkedId);
        });
    }

    private void fetchVersionsFromApi() {
        recyclerView.setAdapter(null);
        String apiUrl = getDownloadSource();
        if (apiUrl != null && !apiUrl.isEmpty()) {
            fetchVersions(apiUrl);
        } else {
            Context context = getContext();
        }
    }

    private void filterVersions(int checkedId) {
        filteredList.clear();
        for (Version version : versionList) {
            if ((checkedId == R.id.rb_release && version.getVersionType().equals("release")) ||
                    (checkedId == R.id.rb_snapshot && version.getVersionType().equals("snapshot")) ||
                    (checkedId == R.id.rb_old_beta && (version.getVersionType().equals("old_alpha") || version.getVersionType().equals("old_beta")))) {
                filteredList.add(version);
            }
        }
        versionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(versionAdapter);
    }


    public void fetchVersions(String apiUrl) {
        executor.execute(() -> {
            try {
                List<Version> versionList = new ArrayList<>();
                H2CO3DownloadUtils.H2CO3DownloaderFeedback monitor = (current, total) -> {
                };
                H2CO3DownloadUtils.downloadFileMonitored(apiUrl, new File(H2CO3Tools.APP_DATA_PATH + "/out.json"), null, monitor);

                String jsonData = H2CO3DownloadUtils.downloadString(apiUrl);
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray versionsArray = jsonObject.getJSONArray("versions");
                for (int i = 0; i < versionsArray.length(); i++) {
                    JSONObject versionObject = versionsArray.getJSONObject(i);
                    String versionName = versionObject.getString("id");
                    String versionType = versionObject.getString("type");
                    String versionUrl = versionObject.getString("url");
                    String versionSha1 = versionObject.getString("sha1");
                    Version version = new Version(versionName, versionType, versionUrl, versionSha1);
                    versionList.add(version);
                }

                uiHandler.post(() -> {
                    ChooseVersionFragment.this.versionList.clear();
                    ChooseVersionFragment.this.versionList.addAll(versionList);
                    filterVersions(typeRadioGroup.getCheckedRadioButtonId());
                    progressIndicator.hide();
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder> {

        private List<Version> versionList;

        public VersionAdapter(List<Version> versionList) {
            this.versionList = versionList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_version, parent, false);
            return new VersionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VersionAdapter.ViewHolder holder, int position) {
            Version version = versionList.get(position);
            holder.versionNameTextView.setText(version.getVersionName());
            holder.versionTypeTextView.setText(version.getVersionType());
        }

        @Override
        public int getItemCount() {
            return versionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView versionNameTextView;
            public TextView versionTypeTextView;
            public H2CO3CardView versionCardView;

            private boolean isHandlingClick = false;

            public ViewHolder(View itemView) {
                super(itemView);
                versionNameTextView = itemView.findViewById(R.id.id);
                versionTypeTextView = itemView.findViewById(R.id.type);
                versionCardView = itemView.findViewById(R.id.download_ver_item);
                versionCardView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (isHandlingClick) {
                    return;
                }

                isHandlingClick = true;

                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);
                    String baseUrl = "https://piston-meta.mojang.com/v1/packages/";
                    String url = baseUrl + version.getVersionSha1() + "/" + version.getVersionName() + ".json";

                    Bundle bundle = new Bundle();
                    bundle.putString("versionName", version.getVersionName());

                    navController.navigate(R.id.action_chooseVersionFragment_to_editVersionFragment, bundle);
                }

                isHandlingClick = false;
            }
        }
    }
}