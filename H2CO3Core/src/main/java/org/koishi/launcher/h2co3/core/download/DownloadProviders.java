package org.koishi.launcher.h2co3.core.download;

import android.content.Context;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DownloadProviders {
    private static final String DEFAULT_PROVIDER_ID = "balanced";
    public static final String DEFAULT_RAW_PROVIDER_ID = "bmclapi";
    private static final String BMCLAPI_ROOT = "https://bmclapi2.bangbang93.com";

    private static final MojangDownloadProvider MOJANG = new MojangDownloadProvider();
    private static final BMCLAPIDownloadProvider BMCLAPI = new BMCLAPIDownloadProvider(BMCLAPI_ROOT);
    private static final Map<String, DownloadProvider> providersById = initializeProvidersById();
    private static final Map<String, DownloadProvider> rawProviders = initializeRawProviders();
    private static final AdaptedDownloadProvider FILE_DOWNLOAD_PROVIDER = new AdaptedDownloadProvider();
    private static DownloadProvider currentDownloadProvider;

    public DownloadProviders() {
        init();
    }

    private static Map<String, DownloadProvider> initializeProvidersById() {
        return Map.of(
                "official", new AutoDownloadProvider(MOJANG, FILE_DOWNLOAD_PROVIDER),
                "balanced", new AutoDownloadProvider(new BalancedDownloadProvider(MOJANG, BMCLAPI), FILE_DOWNLOAD_PROVIDER),
                "mirror", new AutoDownloadProvider(BMCLAPI, FILE_DOWNLOAD_PROVIDER)
        );
    }

    private static Map<String, DownloadProvider> initializeRawProviders() {
        return Map.of(
                "mojang", MOJANG,
                "bmclapi", BMCLAPI
        );
    }

    public static void init() {
        if (!providersById.containsKey(H2CO3Settings.getDownloadSource())) {
            H2CO3Settings.setDownloadSource(DEFAULT_PROVIDER_ID);
            return;
        }

        currentDownloadProvider = providersById.get(DEFAULT_PROVIDER_ID);

        if (!rawProviders.containsKey(H2CO3Settings.getDownloadSource())) {
            H2CO3Settings.setDownloadSource(DEFAULT_RAW_PROVIDER_ID);
            return;
        }

        DownloadProvider primary = rawProviders.get(DEFAULT_RAW_PROVIDER_ID);
        FILE_DOWNLOAD_PROVIDER.setDownloadProviderCandidates(
                Stream.concat(
                        Stream.of(primary),
                        rawProviders.values().stream().filter(x -> x != primary)
                ).collect(Collectors.toList())
        );
    }

    public DownloadProvider getDownloadProvider() {
        return isAutoChooseDownloadType() ? currentDownloadProvider : FILE_DOWNLOAD_PROVIDER;
    }

    private boolean isAutoChooseDownloadType() {
        return true;
    }

    public String localizeErrorMessage(Context context, Throwable exception) {
        return StringUtils.getStackTrace(exception);
    }
}