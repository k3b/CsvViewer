package de.k3b.csvviewer.lib.data.formatter;

public class FormatterDefinitionDto implements FormatterDefinition {
    private final String elementClassName;
    private final String formatPattern;
    private final Boolean nullable;
    private final Integer maxStringLength;

    public FormatterDefinitionDto(FormatterDefinition formatter) {
        this(formatter.getElementClassName(), formatter.getFormatPattern(), formatter.isNullable(), formatter.getMaxStringLength());
    }

    public FormatterDefinitionDto(String elementClassName, String formatPattern, Boolean nullable, Integer maxStringLength) {
        this.elementClassName = elementClassName;
        this.formatPattern = formatPattern;
        this.nullable = nullable;
        this.maxStringLength = maxStringLength;
    }

    public String getElementClassName() {
        return elementClassName;
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public Integer getMaxStringLength() {
        return maxStringLength;
    }
}
