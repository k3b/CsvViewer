/*
 * Copyright (c) 2026 by k3b.
 *
 * This file is part of https://github.com/k3b/CsvViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

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
