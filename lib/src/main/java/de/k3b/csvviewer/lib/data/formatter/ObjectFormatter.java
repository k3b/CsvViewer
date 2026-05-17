package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

public class ObjectFormatter implements FormatterApi<Object> {
    @NonNull private final FormatterApi<?> formatterImpl;

    public ObjectFormatter(@NonNull FormatterApi<?> formatterImpl) {
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
    public String getElementClassName() {
        return formatterImpl == null ? null :formatterImpl.getElementClassName();
    }

    @Override
    public String getFormatPattern() {
        return formatterImpl == null ? null :formatterImpl.getFormatPattern();
    }

    @Override
    public Boolean isNullable() {
        return formatterImpl == null ? true :formatterImpl.isNullable();
    }

    @Override
    public Integer getMaxStringLength() {
        return formatterImpl == null ? null :formatterImpl.getMaxStringLength();
    }

    @Override
    public String toString() {
        return "ObjectFormatter{" +
                "formatter=" + formatterImpl +
                '}';
    }

    /**
     * Creates a null-save comparator for tableColumn. Nulls are last.
     */
    @Override
    public @NonNull Comparator<Object> getComparator() {
        return formatterImpl == null ? null : formatterImpl.getComparator();
    }
}
