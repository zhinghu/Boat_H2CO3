package org.koishi.launcher.h2co3.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigurationManager {

    private static final List<Profile> configurations = new ArrayList<>();
    private static final List<Consumer<Profile>> configurationListeners = new ArrayList<>();

    private static Profile selectedConfiguration;

    public static void addConfiguration(Profile configuration) {
        configurations.add(configuration);
        notifyConfigurationListeners();
    }

    public static void removeConfiguration(Profile configuration) {
        configurations.remove(configuration);
        notifyConfigurationListeners();
    }

    public static List<Profile> getConfigurations() {
        return configurations;
    }

    public static Profile getSelectedConfiguration() {
        return selectedConfiguration;
    }

    public static void setSelectedConfiguration(Profile configuration) {
        selectedConfiguration = configuration;
    }

    public static void registerConfigurationListener(Consumer<Profile> listener) {
        configurationListeners.add(listener);
    }

    private static void notifyConfigurationListeners() {
        for (Consumer<Profile> listener : configurationListeners) {
            listener.accept(selectedConfiguration);
        }
    }
}