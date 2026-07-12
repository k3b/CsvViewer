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

package de.k3b.csvviewer.lib.csv;

import java.io.Serializable;

import de.k3b.csvviewer.lib.util.StringUtil;

/**
 * Configuration for CSV file format.
 * <p>
 * Implementation detail for csv support. This file should be not have dependencies to
 * android.
 */

public class CsvConfig implements Serializable {
    public static final CsvConfig DEFAULT = new CsvConfig(',', '"');
    public static final char[] CSV_DELIMITER_CANDIDATES = {DEFAULT.getFieldDelimiterChar(), ';', '\t', ':', '|'};
    public static final char[] CSV_QUOTE_CANDIDATES = {DEFAULT.getQuoteChar(), '\''};
    private final char m_fieldDelimiterChar;
    private final char m_quoteChar;

    public CsvConfig(char fieldDelimiterChar, char quoteChar) {
        m_fieldDelimiterChar = fieldDelimiterChar;
        m_quoteChar = quoteChar;
    }

    public static CsvConfig infer(String line) {
        char csvFieldDelimiterChar = findChar(line, CSV_DELIMITER_CANDIDATES);
        char csvQuoteChar = findChar(line, CSV_QUOTE_CANDIDATES);

        return new CsvConfig(csvFieldDelimiterChar, csvQuoteChar);
    }

    private static char findChar(String line, char... candidates) {
        int pos = StringUtil.indexOfAny(line, 0, line.length(), candidates);
        return pos == -1 ? candidates[0] : line.charAt(pos);
    }

    public char getFieldDelimiterChar() {
        return m_fieldDelimiterChar;
    }

    public char getQuoteChar() {
        return m_quoteChar;
    }

}
