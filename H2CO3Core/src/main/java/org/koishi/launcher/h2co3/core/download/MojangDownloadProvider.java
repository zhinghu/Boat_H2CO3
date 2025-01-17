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

import org.koishi.launcher.h2co3.core.download.fabric.FabricAPIVersionList;
import org.koishi.launcher.h2co3.core.download.fabric.FabricVersionList;
import org.koishi.launcher.h2co3.core.download.forge.ForgeBMCLVersionList;
import org.koishi.launcher.h2co3.core.download.game.GameVersionList;
import org.koishi.launcher.h2co3.core.download.liteloader.LiteLoaderVersionList;
import org.koishi.launcher.h2co3.core.download.neoforge.NeoForgeOfficialVersionList;
import org.koishi.launcher.h2co3.core.download.optifine.OptiFineBMCLVersionList;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltAPIVersionList;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltVersionList;

/**
 * @see <a href="http://wiki.vg">http://wiki.vg</a>
 */
public class MojangDownloadProvider implements DownloadProvider {
    private final GameVersionList game;
    private final FabricVersionList fabric;
    private final FabricAPIVersionList fabricApi;
    private final ForgeBMCLVersionList forge;
    private final NeoForgeOfficialVersionList neoforge;
    private final LiteLoaderVersionList liteLoader;
    private final OptiFineBMCLVersionList optifine;
    private final QuiltVersionList quilt;
    private final QuiltAPIVersionList quiltApi;

    public MojangDownloadProvider() {
        String apiRoot = "https://bmclapi2.bangbang93.com";

        this.game = new GameVersionList(this);
        this.fabric = new FabricVersionList(this);
        this.fabricApi = new FabricAPIVersionList(this);
        this.forge = new ForgeBMCLVersionList(apiRoot);
        this.neoforge = new NeoForgeOfficialVersionList(this);
        this.liteLoader = new LiteLoaderVersionList(this);
        this.optifine = new OptiFineBMCLVersionList(apiRoot);
        this.quilt = new QuiltVersionList(this);
        this.quiltApi = new QuiltAPIVersionList(this);
    }

    @Override
    public String getVersionListURL() {
        return "https://piston-meta.mojang.com/mc/game/version_manifest.json";
    }

    @Override
    public String getAssetBaseURL() {
        return "https://resources.download.minecraft.net/";
    }

    @Override
    public VersionList<?> getVersionListById(String id) {
        return switch (id) {
            case "game" -> game;
            case "fabric" -> fabric;
            case "fabric-api" -> fabricApi;
            case "forge" -> forge;
            case "neoforge" -> neoforge;
            case "liteloader" -> liteLoader;
            case "optifine" -> optifine;
            case "quilt" -> quilt;
            case "quilt-api" -> quiltApi;
            default -> throw new IllegalArgumentException("Unrecognized version list id: " + id);
        };
    }

    @Override
    public String injectURL(String baseURL) {
        return baseURL;
    }

    @Override
    public int getConcurrency() {
        return 6;
    }
}
