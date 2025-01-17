package org.koishi.launcher.h2co3.core.fakefx.collections;

import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;

import java.util.Map;

public interface ObservableMap<K, V> extends Map<K, V>, Observable {
    /**
     * Add a listener to this observable map.
     *
     * @param listener the listener for listening to the list changes
     */
    void addListener(MapChangeListener<? super K, ? super V> listener);

    /**
     * Tries to removed a listener from this observable map. If the listener is not
     * attached to this map, nothing happens.
     *
     * @param listener a listener to remove
     */
    void removeListener(MapChangeListener<? super K, ? super V> listener);
}
