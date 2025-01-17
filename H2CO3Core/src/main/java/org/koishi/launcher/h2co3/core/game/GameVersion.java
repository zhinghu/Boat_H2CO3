package org.koishi.launcher.h2co3.core.game;

import static org.koishi.launcher.h2co3.core.utils.Lang.tryCast;
import static org.koishi.launcher.h2co3.core.utils.Logging.LOG;

import com.google.gson.JsonParseException;

import org.jenkinsci.constant_pool_scanner.ConstantPool;
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;
import org.jenkinsci.constant_pool_scanner.ConstantType;
import org.koishi.launcher.h2co3.core.fakefx.binding.StringConstant;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class GameVersion {
    private GameVersion() {
    }

    private static Optional<String> getVersionFromJson(InputStream versionJson) {
        try {
            Map<?, ?> version = JsonUtils.fromNonNullJsonFully(versionJson, Map.class);
            return tryCast(version.get("id"), String.class);
        } catch (IOException | JsonParseException e) {
            LOG.log(Level.WARNING, "Failed to parse version.json", e);
            return Optional.empty();
        }
    }

    private static Optional<String> getVersionOfClassMinecraft(InputStream bytecode) throws IOException {
        ConstantPool pool = ConstantPoolScanner.parse(bytecode, ConstantType.STRING);

        return StreamSupport.stream(pool.list(StringConstant.class).spliterator(), false)
                .map(StringConstant::get)
                .filter(s -> s.startsWith("Minecraft Minecraft "))
                .map(s -> s.substring("Minecraft Minecraft ".length()))
                .findFirst();
    }

    private static Optional<String> getVersionFromClassMinecraftServer(InputStream bytecode) throws IOException {
        ConstantPool pool = ConstantPoolScanner.parse(bytecode, ConstantType.STRING);

        List<String> list = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            list = StreamSupport.stream(pool.list(StringConstant.class).spliterator(), false)
                    .map(StringConstant::get)
                    .toList();
        }

        int idx = -1;

        if (list != null) {
            for (int i = 0; i < list.size(); ++i)
                if (list.get(i).startsWith("Can't keep up!")) {
                    idx = i;
                    break;
                }
        }

        for (int i = idx - 1; i >= 0; --i)
            if (list.get(i).matches(".*[0-9].*"))
                return Optional.of(list.get(i));

        return Optional.empty();
    }

    public static Optional<String> minecraftVersion(File file) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead())
            return Optional.empty();

        try (ZipFile gameJar = new ZipFile(file)) {
            ZipEntry versionJson = gameJar.getEntry("version.json");
            if (versionJson != null) {
                Optional<String> result = getVersionFromJson(gameJar.getInputStream(versionJson));
                if (result.isPresent())
                    return result;
            }

            ZipEntry minecraft = gameJar.getEntry("net/minecraft/client/Minecraft.class");
            if (minecraft != null) {
                try (InputStream is = gameJar.getInputStream(minecraft)) {
                    Optional<String> result = getVersionOfClassMinecraft(is);
                    if (result.isPresent())
                        return result;
                }
            }

            ZipEntry minecraftServer = gameJar.getEntry("net/minecraft/server/MinecraftServer.class");
            if (minecraftServer != null) {
                try (InputStream is = gameJar.getInputStream(minecraftServer)) {
                    return getVersionFromClassMinecraftServer(is);
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
