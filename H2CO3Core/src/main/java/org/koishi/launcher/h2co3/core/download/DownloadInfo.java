package org.koishi.launcher.h2co3.core.download;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.koishi.launcher.h2co3.core.utils.DigestUtils;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.gson.tools.TolerableValidationException;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.io.IOException;
import java.nio.file.Path;

public class DownloadInfo implements Validation {

    @SerializedName("url")
    private final String url;
    @SerializedName("sha1")
    private final String sha1;
    @SerializedName("size")
    private final int size;

    public DownloadInfo() {
        this("");
    }

    public DownloadInfo(String url) {
        this(url, null);
    }

    public DownloadInfo(String url, String sha1) {
        this(url, sha1, 0);
    }

    public DownloadInfo(String url, String sha1, int size) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public String getSha1() {
        return "invalid".equals(sha1) ? null : sha1;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("url", url).append("sha1", sha1).append("size", size).toString();
    }

    @Override
    public void validate() throws JsonParseException, TolerableValidationException {
        if (StringUtils.isBlank(url))
            throw new TolerableValidationException();
    }

    public boolean validateChecksum(Path file, boolean defaultValue) throws IOException {
        if (getSha1() == null) return defaultValue;
        return DigestUtils.digestToString("SHA-1", file).equalsIgnoreCase(getSha1());
    }
}
