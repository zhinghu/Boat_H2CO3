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
package org.koishi.launcher.h2co3.core.download.game;

import com.google.gson.JsonParseException;

import org.koishi.launcher.h2co3.core.download.AbstractDependencyManager;
import org.koishi.launcher.h2co3.core.download.CacheRepository;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.game.AssetIndex;
import org.koishi.launcher.h2co3.core.game.AssetIndexInfo;
import org.koishi.launcher.h2co3.core.game.AssetObject;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public final class GameAssetDownloadTask extends Task<Void> {

    public static final boolean DOWNLOAD_INDEX_FORCIBLY = true;
    public static final boolean DOWNLOAD_INDEX_IF_NECESSARY = false;
    private final AbstractDependencyManager dependencyManager;
    private final Version version;
    private final AssetIndexInfo assetIndexInfo;
    private final Path assetIndexFile;
    private final boolean integrityCheck;
    private final List<Task<?>> dependents = new ArrayList<>(1);
    private final List<Task<?>> dependencies = new ArrayList<>();

    public GameAssetDownloadTask(AbstractDependencyManager dependencyManager, Version version, boolean forceDownloadingIndex, boolean integrityCheck) {
        this.dependencyManager = dependencyManager;
        this.version = version.resolve(dependencyManager.getGameRepository());
        this.assetIndexInfo = this.version.getAssetIndex();
        this.assetIndexFile = dependencyManager.getGameRepository().getIndexFile(version.getId(), assetIndexInfo.getId());
        this.integrityCheck = integrityCheck;

        setStage("h2co3.install.assets");
        dependents.add(new GameAssetIndexDownloadTask(dependencyManager, this.version, forceDownloadingIndex));
    }

    @Override
    public Collection<Task<?>> getDependents() {
        return dependents;
    }

    @Override
    public Collection<Task<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public void execute() throws Exception {
        AssetIndex index;
        try {
            index = JsonUtils.fromNonNullJson(FileTools.readText(assetIndexFile), AssetIndex.class);
        } catch (IOException | JsonParseException e) {
            throw new GameAssetIndexDownloadTask.GameAssetIndexMalformedException();
        }

        int progress = 0;
        for (AssetObject assetObject : index.getObjects().values()) {
            if (isCancelled())
                throw new InterruptedException();

            Path file = dependencyManager.getGameRepository().getAssetObject(version.getId(), assetIndexInfo.getId(), assetObject);
            boolean download = !Files.isRegularFile(file);
            try {
                if (!download && integrityCheck && !assetObject.validateChecksum(file, true))
                    download = true;
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to calc hash value of file " + file, e);
            }
            if (download) {
                List<URL> urls = dependencyManager.getDownloadProvider().getAssetObjectCandidates(assetObject.getLocation());

                FileDownloadTask task = new FileDownloadTask(urls, file.toFile(), new FileDownloadTask.IntegrityCheck("SHA-1", assetObject.getHash()));
                task.setName(assetObject.getHash());
                task.setCandidate(dependencyManager.getCacheRepository().getCommonDirectory()
                        .resolve("assets").resolve("objects").resolve(assetObject.getLocation()));
                task.setCacheRepository(dependencyManager.getCacheRepository());
                task.setCaching(true);
                dependencies.add(task.withCounter("h2co3.install.assets"));
            } else {
                dependencyManager.getCacheRepository().tryCacheFile(file, CacheRepository.SHA1, assetObject.getHash());
            }

            updateProgress(++progress, index.getObjects().size());
        }

        if (!dependencies.isEmpty()) {
            getProperties().put("total", dependencies.size());
            notifyPropertiesChanged();
        }
    }
}
