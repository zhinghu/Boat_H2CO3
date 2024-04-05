package org.koishi.launcher.h2co3.adapter;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.Version;
import org.koishi.launcher.h2co3.dialog.DownloadDialog;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder> {

    private static List<Version> versionList;

    public VersionAdapter(List<Version> versionList) {
        VersionAdapter.versionList = versionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_version, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Version version = versionList.get(position);
        holder.versionNameTextView.setText(version.getVersionName());
        holder.versionTypeTextView.setText(version.getVersionType());
    }

    @Override
    public int getItemCount() {
        return versionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
                new FetchVersionDetailsTask().execute(url);
            }
        }

        private void showDialog(String name, String type, String details) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(itemView.getContext());
            builder.setTitle("Version Details");
            builder.setMessage("Name: " + name + "\nType: " + type + "\nDetails: " + details);
            builder.setPositiveButton("OK", null);
            builder.show();

            DownloadDialog dialog = new DownloadDialog(itemView.getContext());
            dialog.setJsonString(details);
            dialog.show();
        }

        private class FetchVersionDetailsTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                String url = urls[0];
                final String[] details = {""};

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        details[0] = "Error: " + e.getMessage();
                        handleResponse(details[0]);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            details[0] = response.body().string();
                        } else {
                            details[0] = "HTTP error: " + response.code();
                        }
                        handleResponse(details[0]);
                    }
                });

                return details[0];
            }

            private void handleResponse(String details) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);
                    showDialog(version.getVersionName(), version.getVersionType(), details);
                }
            }
        }
    }
}