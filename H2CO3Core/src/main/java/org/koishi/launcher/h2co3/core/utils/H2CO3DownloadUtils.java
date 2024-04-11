package org.koishi.launcher.h2co3.core.utils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class H2CO3DownloadUtils {

    public static void download(String url, OutputStream os) throws IOException {
        download(new URL(url), os);
    }

    public static void download(URL url, OutputStream os) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setDoInput(true);
        conn.connect();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned HTTP " + conn.getResponseCode()
                    + ": " + conn.getResponseMessage());
        }
        try (InputStream is = conn.getInputStream()) {
            IOUtils.copy(is, os);
        }
    }

    public static String downloadString(String url) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            download(url, bos);
            return bos.toString("UTF-8");
        } finally {
            bos.close();
        }
    }

    public static void downloadFile(String url, File out) throws IOException {
        out.getParentFile().mkdirs();
        File tempOut = new File(out.getParentFile(), out.getName() + ".part");
        try (OutputStream bos = new BufferedOutputStream(new FileOutputStream(tempOut))) {
            download(url, bos);
            if (!tempOut.renameTo(out)) {
                throw new IOException("File rename failed");
            }
        } finally {
            if (tempOut.exists()) {
                tempOut.delete();
            }
        }
    }

    public static void downloadFileMonitored(String urlInput, File outputFile, byte[] buffer,
                                             H2CO3DownloaderFeedback monitor) throws IOException {
        if (outputFile == null) {
            throw new IllegalArgumentException("Output file cannot be null");
        }

        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(urlInput).openConnection();
        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            int cur;
            int oval = 0;
            int len = conn.getContentLength();

            if (buffer == null) buffer = new byte[65535];

            while ((cur = is.read(buffer)) != -1) {
                oval += cur;
                fos.write(buffer, 0, cur);
                monitor.updateProgress(oval, len);
            }
        }
    }

    public interface H2CO3DownloaderFeedback {
        void updateProgress(int current, int total);
    }
}