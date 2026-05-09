package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

public class ObjectFormatter implements FormatterApi<Object> {
    @Nullable private final FormatterApi<?> formatterImpl;

    public ObjectFormatter(@Nullable FormatterApi<?> formatterImpl) {
        this.formatterImpl = formatterImpl;
    }

    @Nullable @Override
    public String format(@Nullable Object nativeValue) {
        String result = null;
        if (nativeValue != null) {
            if (formatterImpl != null) {
                result = formatterImpl.formatObject(nativeValue);
            } else {
                result = nativeValue.toString().trim();
            }
        }
        return result;
    }

    @Nullable @Override
    public Object parse(@Nullable  String nativeValue) {
        Object result = nativeValue;
        if (formatterImpl != null) {
            result = formatterImpl.parse(nativeValue);
        }
        return result;
    }

    @Override
    public String toString() {
        return "ObjectFormatter{" +
                "formatter=" + formatterImpl +
                '}';
    }
}
