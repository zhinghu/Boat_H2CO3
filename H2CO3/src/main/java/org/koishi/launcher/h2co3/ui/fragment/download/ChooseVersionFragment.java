/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.download;

import static org.koishi.launcher.h2co3.core.H2CO3Settings.getDownloadSource;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.Version;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseVersionFragment extends H2CO3Fragment {

    private static final String API_URL_BMCLAPI = "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json";
    private static final String API_URL_MOJANG = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;

    private List<Version> versionList;
    private List<Version> filteredList;

    NavController navController;

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
    }

    private void initListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterVersions(checkedId);
        });
    }

    private void fetchVersionsFromApi() {
        // 清除RecyclerView的适配器
        recyclerView.setAdapter(null);
        // 获取下载源
        String apiUrl = getDownloadSource();
        // 检查下载源是否有效
        if (apiUrl != null && !apiUrl.isEmpty()) {
            // 如果下载源有效，执行异步任务
            new FetchVersionsTask().execute(API_URL_BMCLAPI);
        } else {
            // 如果下载源无效，获取当前的Context
            Context context = getContext();
            // 检查Context是否有效
            if (context != null) {
                // 如果Context有效，显示Toast提示
                Toast.makeText(context, "Invalid source", Toast.LENGTH_SHORT).show();
            } else {
                // 如果Context无效，记录错误日志或进行其他错误处理
                // Log.e(TAG, "Context is null when trying to show toast.");
            }
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

    private class FetchVersionsTask extends AsyncTask<String, Void, List<Version>> {
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
            private final String baseUrl = "https://piston-meta.mojang.com/v1/packages/";
            public TextView versionNameTextView;
            public TextView versionTypeTextView;
            public H2CO3CardView versionCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                versionNameTextView = itemView.findViewById(R.id.id);
                versionTypeTextView = itemView.findViewById(R.id.type);
                versionCardView = itemView.findViewById(R.id.download_ver_item);
                versionCardView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);
                    String url = baseUrl + version.getVersionSha1() + "/" + version.getVersionName() + ".json";

                    Bundle bundle = new Bundle();
                    bundle.putString("versionName", version.getVersionName());

                    navController.navigate(R.id.action_chooseVersionFragment_to_editVersionFragment, bundle);
                }

            }
        }
    }
}