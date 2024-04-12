/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.core.download;

import static org.koishi.launcher.h2co3.core.utils.Lang.mapOf;
import static org.koishi.launcher.h2co3.core.utils.Pair.pair;

import android.content.Context;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DownloadProviders {
    public static final Map<String, DownloadProvider> providersById;
    public static final Map<String, DownloadProvider> rawProviders;
    public static final String DEFAULT_PROVIDER_ID = "balanced";
    public static final String DEFAULT_RAW_PROVIDER_ID = "bmclapi";
    private static final AdaptedDownloadProvider fileDownloadProvider = new AdaptedDownloadProvider();

    private static final MojangDownloadProvider MOJANG;
    private static final BMCLAPIDownloadProvider BMCLAPI;
    private static DownloadProvider currentDownloadProvider;

    static {
        String bmclapiRoot = "https://bmclapi2.bangbang93.com";

        MOJANG = new MojangDownloadProvider();
        BMCLAPI = new BMCLAPIDownloadProvider(bmclapiRoot);
        rawProviders = mapOf(
                pair("mojang", MOJANG),
                pair("bmclapi", BMCLAPI)
        );

        AdaptedDownloadProvider fileProvider = new AdaptedDownloadProvider();
        fileProvider.setDownloadProviderCandidates(Arrays.asList(BMCLAPI, MOJANG));
        BalancedDownloadProvider balanced = new BalancedDownloadProvider(MOJANG, BMCLAPI);

        providersById = mapOf(
                pair("official", new AutoDownloadProvider(MOJANG, fileProvider)),
                pair("balanced", new AutoDownloadProvider(balanced, fileProvider)),
                pair("mirror", new AutoDownloadProvider(BMCLAPI, fileProvider)));
    }

    private DownloadProviders() {
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
        fileDownloadProvider.setDownloadProviderCandidates(
                Stream.concat(
                        Stream.of(primary),
                        rawProviders.values().stream().filter(x -> x != primary)
                ).collect(Collectors.toList())
        );
    }


    /**
     * Get current primary preferred download provider
     */
    public static DownloadProvider getDownloadProvider() {
        return isAutoChooseDownloadType() ? currentDownloadProvider : fileDownloadProvider;
    }

    private static boolean isAutoChooseDownloadType() {
        return true;
    }

    public static String localizeErrorMessage(Context context, Throwable exception) {
        return StringUtils.getStackTrace(exception);
    }
}
