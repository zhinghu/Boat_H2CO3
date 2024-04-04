/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.VersionAdapter;
import org.koishi.launcher.h2co3.core.utils.Version;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadFragment extends H2CO3Fragment {

    private static final String API_URL_BMCLAPI = "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json";
    private static final String API_URL_MOJANG = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;

    private List<Version> versionList;
    private List<Version> filteredList;

    private Spinner spDownloadSourceMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        initView(view);
        initListeners();

        versionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        versionAdapter = new VersionAdapter(filteredList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(versionAdapter);

        fetchVersionsFromApi(0);

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.loadingversionFileListView1);
        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        RadioButton rbRelease = view.findViewById(R.id.rb_release);
        RadioButton rbSnapshot = view.findViewById(R.id.rb_snapshot);
        RadioButton rbOldbeta = view.findViewById(R.id.rb_old_beta);

        spDownloadSourceMode = view.findViewById(R.id.sp_download_source_mode);
        String[] mItems = getResources().getStringArray(R.array.download_source);
        ArrayAdapter<String> adapter_source = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, mItems);
        spDownloadSourceMode.setAdapter(adapter_source);
        spDownloadSourceMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchVersionsFromApi(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterVersions(checkedId);
        });
    }

    private void fetchVersionsFromApi(int position) {
        recyclerView.setAdapter(null);
        String apiUrl = getApiUrl(position);
        if (apiUrl != null) {
            new FetchVersionsTask().execute(apiUrl);
        } else {
            Toast.makeText(getContext(), "Invalid source", Toast.LENGTH_SHORT).show();
        }
    }

    private String getApiUrl(int position) {
        return switch (position) {
            case 0 -> API_URL_BMCLAPI;
            case 1 -> API_URL_MOJANG;
            default -> null;
        };
    }

    private void filterVersions(int checkedId) {
        filteredList.clear();
        for (Version version : versionList) {
            if (checkedId == R.id.rb_release && version.getVersionType().equals("release")) {
                filteredList.add(version);
            } else if (checkedId == R.id.rb_snapshot && version.getVersionType().equals("snapshot")) {
                filteredList.add(version);
            } else if (checkedId == R.id.rb_old_beta && (version.getVersionType().equals("old_alpha") || version.getVersionType().equals("old_beta"))) {
                filteredList.add(version);
            }
        }
        versionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(versionAdapter);
    }

    private class FetchVersionsTask extends AsyncTask<String, Void, List<Version>> {

        private static final int CONNECTION_TIMEOUT = 10000;
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected List<Version> doInBackground(String... urls) {
            String apiUrl = urls[0];
            List<Version> versionList = new ArrayList<>();

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
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
                } else {
                    Toast.makeText(getContext(), "HTTP error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return versionList;
        }

        @Override
        protected void onPostExecute(List<Version> versionListFromApi) {
            versionList.clear();
            versionList.addAll(versionListFromApi);
            filterVersions(typeRadioGroup.getCheckedRadioButtonId());
        }
    }
}