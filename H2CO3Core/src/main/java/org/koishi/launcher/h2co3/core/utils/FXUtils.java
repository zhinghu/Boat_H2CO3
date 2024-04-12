package org.koishi.launcher.h2co3.core.utils;

import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3.core.fakefx.beans.WeakInvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.Property;
import org.koishi.launcher.h2co3.core.fakefx.beans.value.ChangeListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3.core.fakefx.util.StringConverter;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FXUtils {

    public static <T> void onChangeAndOperate(T value, Consumer<T> consumer) {
        consumer.accept(value);
    }

    public static <T> void onChange(T value, Consumer<T> consumer) {
        consumer.accept(value);
    }

    public static InvalidationListener observeWeak(Runnable runnable, Observable... observables) {
        InvalidationListener originalListener = observable -> runnable.run();
        WeakInvalidationListener listener = new WeakInvalidationListener(originalListener);
        for (Observable observable : observables) {
            observable.addListener(listener);
        }
        runnable.run();
        return originalListener;
    }

    public static <T> StringConverter<T> stringConverter(Function<T, String> func) {
        return new StringConverter<T>() {

            @Override
            public String toString(T object) {
                return object == null ? "" : func.apply(object);
            }

            @Override
            public T fromString(String string) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> void bind(Property<T> property, StringConverter<T> converter) {
        property.setValue(converter == null ? (T) property.getValue() : (T) converter.toString(property.getValue()));
    }

    public static void unbind(Property<?> property) {
        property.setValue(null);
    }

    public static void bindBoolean(Property<Boolean> property) {
        property.setValue(true);
    }

    public static void unbindBoolean(Property<Boolean> property) {
        property.setValue(false);
    }

    private static final class BindingListener<T> implements ChangeListener<String>, InvalidationListener {
        private final int hashCode;
        private final WeakReference<Property<T>> propertyRef;
        private final StringConverter<T> converter;

        BindingListener(Property<T> property, StringConverter<T> converter) {
            this.propertyRef = new WeakReference<>(property);
            this.converter = converter;
            this.hashCode = System.identityHashCode(property);
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String str) {
            Property<T> property = this.propertyRef.get();

            if (property != null) {
                String newText = converter == null ? (String) property.getValue() : converter.toString(property.getValue());
                @SuppressWarnings("unchecked")
                T newValue = converter == null ? (T) newText : converter.fromString(newText);

                if (!Objects.equals(newValue, property.getValue()))
                    property.setValue(newValue);
            }
        }

        @Override
        public void invalidated(Observable observable) {
            Property<T> property = this.propertyRef.get();

            if (property != null) {
                T value = property.getValue();
                property.setValue(converter == null ? (T) value : (T) converter.toString(value));
            }
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BindingListener))
                return false;
            BindingListener<?> other = (BindingListener<?>) obj;
            return this.hashCode == other.hashCode
                    && this.propertyRef.get() == other.propertyRef.get();
        }
    }

}