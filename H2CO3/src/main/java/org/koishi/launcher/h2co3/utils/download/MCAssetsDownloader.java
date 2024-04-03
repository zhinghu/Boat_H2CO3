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
 * // Created by cainiaohh on 2024-04-03.
 * //
 */

package org.koishi.launcher.h2co3.utils.download;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MCAssetsDownloader {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MCAssetsDownloader";

    private static ProgressDialog progressDialog;

    public static void startDownload(Context context, String versionName) {
        // 检查并请求网络权限
        downloadAssets(context, versionName);
    }

    private static void downloadAssets(Context context, String versionName) {
        String version = versionName;
        String path = H2CO3Tools.PUBLIC_FILE_PATH;
        boolean log = true;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Downloading Assets");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new DownloadAssetsTask(context, version, path, log).execute();
    }

    private static void downloadAssets(String version, String path, boolean log, ProgressListener progressListener) throws Exception {
        List<String> index = getAssets(version, log);
        int filesToDownload = 0;
        for (String asset : index) {
            if (!new File(path + "/assets/objects/" + asset.substring(0, 2) + "/" + asset).exists()) {
                filesToDownload++;
            }
        }

        log("There are " + filesToDownload + " assets to download.");

        int downloadedFiles = 0;
        int progress = 0;
        for (String asset : index) {
            String assetPath = asset.substring(0, 2) + "/" + asset;
            String filePath = path + "/assets/objects/" + assetPath;
            File anAsset = new File(filePath);
            anAsset.getParentFile().mkdirs();
            try {
                log("Downloading " + assetPath);
                FileChannel fileChannel = FileChannel.open(anAsset.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                fileChannel.transferFrom(Channels.newChannel(new URL("http://resources.download.minecraft.net/" + assetPath).openConnection().getInputStream()), 0L, Long.MAX_VALUE);
                log("Downloaded " + assetPath);
                downloadedFiles++;
                progress = (downloadedFiles * 100) / filesToDownload;
                progressListener.onProgressUpdate(progress, 100);
            } catch (IOException e) {
                log("[ERROR] Unable to download asset " + assetPath);
            }
        }
    }

    private static List<String> getAssets(String version, boolean log) throws Exception {
        List<String> assets = new ArrayList<>();
        for (String ver : getPageContent("https://launchermeta.mojang.com/mc/game/version_manifest.json").split(",")) {
            if (version.equals("snapshot")) {
                version = ver.split("\"snapshot\": \"")[1].split("\"")[0];
            }
            if (version.equals("release")) {
                version = ver.split("\"snapshot\": \"")[1].split("\"")[0];
            }

            if (!ver.contains("latest") && ver.contains("\"" + version + "\"")) {
                String jsonPage = getPageContent(ver.split("\"url\": \"")[1].split("\"")[0]);
                for (String j : getPageContent(jsonPage.split("\\.json")[0].split("\"url\": \"")[1] + ".json").replace("{\"objects\": {", "").split("},")) {
                    assets.add(j.split("hash\": \"")[1].split("\"")[0]);
                }
            }
        }
        return assets;
    }

    private static String getPageContent(String u) throws Exception {
        URLConnection c = new URL(u).openConnection();
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        c.connect();
        return new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8)).readLine();
    }

    private static void log(String t) {
        Log.d(TAG, t);
    }

    public interface ProgressListener {
        void onProgressUpdate(int progress, int maxProgress);
    }

    private static class DownloadAssetsTask extends AsyncTask<Void, Integer, Void> {
        private Context context;
        private String version;
        private String path;
        private boolean log;

        public DownloadAssetsTask(Context context, String version, String path, boolean log) {
            this.context = context;
            this.version = version;
            this.path = path;
            this.log = log;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                downloadAssets(version, path, log, new ProgressListener() {
                    @Override
                    public void onProgressUpdate(int progress, int maxProgress) {
                        publishProgress(progress, maxProgress);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            int maxProgress = values[1];
            progressDialog.setMax(maxProgress);
            progressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Log.d(TAG, "Download completed");
        }
    }
}