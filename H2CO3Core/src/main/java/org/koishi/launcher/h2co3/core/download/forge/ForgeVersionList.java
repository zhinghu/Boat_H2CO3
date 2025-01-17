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
package org.koishi.launcher.h2co3.core.download.forge;


import org.koishi.launcher.h2co3.core.download.DownloadProvider;
import org.koishi.launcher.h2co3.core.download.VersionList;
import org.koishi.launcher.h2co3.core.download.VersionNumber;
import org.koishi.launcher.h2co3.core.utils.HttpRequest;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class ForgeVersionList extends VersionList<ForgeRemoteVersion> {
    public static final String FORGE_LIST = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/json";
    private final DownloadProvider downloadProvider;

    public ForgeVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(downloadProvider.injectURL(FORGE_LIST)).getJsonAsync(ForgeVersionRoot.class)
                .thenAcceptAsync(root -> {
                    lock.writeLock().lock();

                    try {
                        if (root == null)
                            return;
                        versions.clear();

                        for (Map.Entry<String, int[]> entry : root.getGameVersions().entrySet()) {
                            String gameVersion = VersionNumber.normalize(entry.getKey());
                            for (int v : entry.getValue()) {
                                ForgeVersion version = root.getNumber().get(v);
                                if (version == null)
                                    continue;
                                String jar = null;
                                for (String[] file : version.getFiles())
                                    if (file.length > 1 && "installer".equals(file[1])) {
                                        String classifier = version.getGameVersion() + "-" + version.getVersion()
                                                + (StringUtils.isNotBlank(version.getBranch()) ? "-" + version.getBranch() : "");
                                        String fileName = root.getArtifact() + "-" + classifier + "-" + file[1] + "." + file[0];
                                        jar = root.getWebPath() + classifier + "/" + fileName;
                                    }

                                if (jar == null)
                                    continue;
                                versions.put(gameVersion, new ForgeRemoteVersion(
                                        version.getGameVersion(), version.getVersion(), null, Collections.singletonList(jar)
                                ));
                            }
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                });
    }
}
