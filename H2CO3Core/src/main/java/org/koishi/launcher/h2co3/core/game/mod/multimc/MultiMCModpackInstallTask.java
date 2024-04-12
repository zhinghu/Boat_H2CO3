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
package org.koishi.launcher.h2co3.core.game.mod.multimc;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.DefaultGameRepository;
import org.koishi.launcher.h2co3.core.download.GameBuilder;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.game.mod.MinecraftInstanceTask;
import org.koishi.launcher.h2co3.core.game.mod.Modpack;
import org.koishi.launcher.h2co3.core.game.mod.ModpackConfiguration;
import org.koishi.launcher.h2co3.core.game.mod.ModpackInstallTask;
import org.koishi.launcher.h2co3.core.utils.Arguments;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.io.CompressingUtils;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MultiMCModpackInstallTask extends Task<Void> {

    private final File zipFile;
    private final Modpack modpack;
    private final MultiMCInstanceConfiguration manifest;
    private final String name;
    private final DefaultGameRepository repository;
    private final List<Task<?>> dependencies = new ArrayList<>(1);
    private final List<Task<?>> dependents = new ArrayList<>(4);

    public MultiMCModpackInstallTask(DefaultDependencyManager dependencyManager, File zipFile, Modpack modpack, MultiMCInstanceConfiguration manifest, String name) {
        this.zipFile = zipFile;
        this.modpack = modpack;
        this.manifest = manifest;
        this.name = name;
        this.repository = dependencyManager.getGameRepository();

        File json = repository.getModpackConfiguration(name);
        if (repository.hasVersion(name) && !json.exists())
            throw new IllegalArgumentException("VersionMod " + name + " already exists.");

        GameBuilder builder = dependencyManager.gameBuilder().name(name).gameVersion(manifest.getGameVersion());

        if (manifest.getMmcPack() != null) {
            Optional<MultiMCManifest.MultiMCManifestComponent> forge = manifest.getMmcPack().getComponents().stream().filter(e -> e.getUid().equals("net.minecraftforge")).findAny();
            forge.ifPresent(c -> {
                if (c.getVersion() != null)
                    builder.version("forge", c.getVersion());
            });

            Optional<MultiMCManifest.MultiMCManifestComponent> neoForge = manifest.getMmcPack().getComponents().stream().filter(e -> e.getUid().equals("net.neoforged")).findAny();
            neoForge.ifPresent(c -> {
                if (c.getVersion() != null)
                    builder.version("neoforge", c.getVersion());
            });

            Optional<MultiMCManifest.MultiMCManifestComponent> liteLoader = manifest.getMmcPack().getComponents().stream().filter(e -> e.getUid().equals("com.mumfrey.liteloader")).findAny();
            liteLoader.ifPresent(c -> {
                if (c.getVersion() != null)
                    builder.version("liteloader", c.getVersion());
            });

            Optional<MultiMCManifest.MultiMCManifestComponent> fabric = manifest.getMmcPack().getComponents().stream().filter(e -> e.getUid().equals("net.fabricmc.fabric-loader")).findAny();
            fabric.ifPresent(c -> {
                if (c.getVersion() != null)
                    builder.version("fabric", c.getVersion());
            });

            Optional<MultiMCManifest.MultiMCManifestComponent> quilt = manifest.getMmcPack().getComponents().stream().filter(e -> e.getUid().equals("org.quiltmc.quilt-loader")).findAny();
            quilt.ifPresent(c -> {
                if (c.getVersion() != null)
                    builder.version("quilt", c.getVersion());
            });
        }

        dependents.add(builder.buildAsync());
        onDone().register(event -> {
            if (event.isFailed())
                repository.removeVersionFromDisk(name);
        });
    }

    @Override
    public List<Task<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean doPreExecute() {
        return true;
    }

    @Override
    public void preExecute() throws Exception {
        File run = repository.getRunDirectory(name);
        File json = repository.getModpackConfiguration(name);

        ModpackConfiguration<MultiMCInstanceConfiguration> config = null;
        try {
            if (json.exists()) {
                config = JsonUtils.GSON.fromJson(FileTools.readText(json), new TypeToken<ModpackConfiguration<MultiMCInstanceConfiguration>>() {
                }.getType());

                if (!MultiMCModpackProvider.INSTANCE.getName().equals(config.getType()))
                    throw new IllegalArgumentException("VersionMod " + name + " is not a MultiMC modpack. Cannot update this version.");
            }
        } catch (JsonParseException | IOException ignore) {
        }

        String subDirectory;

        try (FileSystem fs = CompressingUtils.readonly(zipFile.toPath()).setEncoding(modpack.getEncoding()).build()) {
            // /.minecraft
            if (Files.exists(fs.getPath("/.minecraft"))) {
                subDirectory = "/.minecraft";
                // /minecraft
            } else if (Files.exists(fs.getPath("/minecraft"))) {
                subDirectory = "/minecraft";
                // /[name]/.minecraft
            } else if (Files.exists(fs.getPath("/" + manifest.getName() + "/.minecraft"))) {
                subDirectory = "/" + manifest.getName() + "/.minecraft";
                // /[name]/minecraft
            } else if (Files.exists(fs.getPath("/" + manifest.getName() + "/minecraft"))) {
                subDirectory = "/" + manifest.getName() + "/minecraft";
            } else {
                subDirectory = "/" + manifest.getName() + "/.minecraft";
            }
        }

        dependents.add(new ModpackInstallTask<>(zipFile, run, modpack.getEncoding(), Collections.singletonList(subDirectory), any -> true, config).withStage("h2co3.modpack"));
        dependents.add(new MinecraftInstanceTask<>(zipFile, modpack.getEncoding(), Collections.singletonList(subDirectory), manifest, MultiMCModpackProvider.INSTANCE, manifest.getName(), null, repository.getModpackConfiguration(name)).withStage("h2co3.modpack"));
    }

    @Override
    public List<Task<?>> getDependents() {
        return dependents;
    }

    @Override
    public void execute() throws Exception {
        Version version = repository.readVersionJson(name);

        try (FileSystem fs = CompressingUtils.readonly(zipFile.toPath()).setAutoDetectEncoding(true).build()) {
            Path root = MultiMCModpackProvider.getRootPath(fs.getPath("/"));
            Path patches = root.resolve("patches");

            if (Files.exists(patches)) {
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(patches)) {
                    for (Path patchJson : directoryStream) {
                        if (patchJson.toString().endsWith(".json")) {
                            // If json is malformed, we should stop installing this modpack instead of skipping it.
                            MultiMCInstancePatch multiMCPatch = JsonUtils.GSON.fromJson(FileTools.readText(patchJson), MultiMCInstancePatch.class);

                            List<String> arguments = new ArrayList<>();
                            for (String arg : multiMCPatch.getTweakers()) {
                                arguments.add("--tweakClass");
                                arguments.add(arg);
                            }

                            Version patch = new Version(multiMCPatch.getName(), multiMCPatch.getVersion(), 1, new Arguments().addGameArguments(arguments), multiMCPatch.getMainClass(), multiMCPatch.getLibraries());
                            version = version.addPatch(patch);
                        }
                    }
                }
            }

            Path libraries = root.resolve("libraries");
            if (Files.exists(libraries))
                FileTools.copyDirectory(libraries, repository.getVersionRoot(name).toPath().resolve("libraries"));

            Path jarmods = root.resolve("jarmods");
            if (Files.exists(jarmods))
                FileTools.copyDirectory(jarmods, repository.getVersionRoot(name).toPath().resolve("jarmods"));
        }

        dependencies.add(repository.saveAsync(version));
    }
}