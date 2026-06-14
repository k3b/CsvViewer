package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

import java.io.Serializable;

/** api to
  * {@link #createFormatter()} inside a {@link de.k3b.csvviewer.lib.data.analyser.AnalyserApi}
  * which can convert between string and native type T */
public interface FormatterFactoryApi<T> extends Serializable {
    @Nullable
    FormatterApi<T> createFormatter();
}
