package org.koishi.launcher.h2co3.core;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.download.CacheRepository;
import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.DefaultGameRepository;
import org.koishi.launcher.h2co3.core.download.DownloadProvider;
import org.koishi.launcher.h2co3.core.download.DownloadProviders;
import org.koishi.launcher.h2co3.core.download.H2CO3GameRepository;
import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3.core.game.H2CO3CacheRepository;
import org.koishi.launcher.h2co3.core.game.ObservableHelper;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;

import java.io.File;
import java.lang.reflect.Type;

@JsonAdapter(Profile.Serializer.class)
public final class Profile implements Observable {
    private final ObservableHelper observableHelper = new ObservableHelper(this);
    private H2CO3GameRepository repository;
    private ObjectProperty<File> gameDir;
    private String name;

    public Profile(String name) {
        this.name = name;
        this.gameDir = new SimpleObjectProperty<>(this, "gameDir", new File(H2CO3GameHelper.getGameDirectory()));
        this.repository = new H2CO3GameRepository(new File(H2CO3GameHelper.getGameDirectory()));
    }

    public File getGameDir() {
        return gameDir.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DefaultGameRepository getRepository() {
        return repository;
    }

    public DefaultDependencyManager getDependency() {
        DownloadProviders downloadProviders = new DownloadProviders();
        return getDependency(downloadProviders.getDownloadProvider());
    }

    public DefaultDependencyManager getDependency(DownloadProvider downloadProvider) {
        H2CO3CacheRepository cacheRepository = H2CO3CacheRepository.REPOSITORY;
        CacheRepository.setInstance(cacheRepository);
        cacheRepository.setDirectory(H2CO3Tools.CACHE_DIR);
        System.out.println(cacheRepository.getDirectory());
        return new DefaultDependencyManager(repository, downloadProvider, cacheRepository);
    }

    @Override
    public @NotNull String toString() {
        return new ToStringBuilder(this)
                .append("gameDir", getGameDir())
                .append("name", getName())
                .toString();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableHelper.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableHelper.removeListener(listener);
    }

    public static final class Serializer implements JsonSerializer<Profile>, JsonDeserializer<Profile> {
        @Override
        public JsonElement serialize(Profile src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null)
                return JsonNull.INSTANCE;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameDir", src.getGameDir().getPath());

            return jsonObject;
        }

        @Override
        public Profile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject)) return null;

            return new Profile("Default");
        }

    }
}