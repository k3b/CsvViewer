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

package de.k3b.android.csvviewer.lib.data.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jspecify.annotations.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class ComparatorTypTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorTypTest.class);

    @Test
    public void integerLessEquals() throws IOException {
        String expression = "myVar <= 4";
        ComparatorTyp expectedTyp = ComparatorTyp.LESS_OR_EQUAL;
        String expectedFieldName = "myVar";
        Object expectedCompareValue = 4;
        FormatterApi<?> formatter = TableColumnType.Integer.getFormatter();
        Object trueFieldValue = 4;
        Object falseFieldValue = 5;

        check(expression, expectedTyp, expectedFieldName, expectedCompareValue, formatter, trueFieldValue, falseFieldValue);
    }

    @Test
    public void stringEquals() throws IOException {
        String expression = "myVar = Hallo";
        ComparatorTyp expectedTyp = ComparatorTyp.EQUALS;
        String expectedFieldName = "myVar";
        Object expectedCompareValue = "Hallo";
        FormatterApi<?> formatter = TableColumnType.String.getFormatter();
        Object trueFieldValue = "haLLo";
        Object falseFieldValue = "moin";

        check(expression, expectedTyp, expectedFieldName, expectedCompareValue, formatter, trueFieldValue, falseFieldValue);
    }

    @Test
    public void testEncodeDecode() {
        ComparatorTyp sut = ComparatorTyp.EQUALS;
        String fieldName = "myField";
        String compareValue = " some value with blank ";

        String expression = sut.toExpression(fieldName, compareValue);

        Assert.assertEquals("fieldname", fieldName, sut.getFieldName(expression));
        Assert.assertEquals("compareValue", compareValue, sut.getCompareValue(expression));
    }

    @Test
    public void testEncodeDecodeWithoutFieldName() {
        ComparatorTyp sut = ComparatorTyp.EQUALS;
        String fieldName = null;
        String compareValue = " some value with blank ";

        String expression = sut.toExpression(fieldName, compareValue);

        Assert.assertEquals("fieldname", fieldName, sut.getFieldName(expression));
        Assert.assertEquals("compareValue", compareValue, sut.getCompareValue(expression));
    }

    @Test
    public void testEncodeDecodeWithoutCompareValue() {
        ComparatorTyp sut = ComparatorTyp.EQUALS;
        String fieldName = "myField";
        String compareValue = null;

        String expression = sut.toExpression(fieldName, compareValue);

        Assert.assertEquals("fieldname", fieldName, sut.getFieldName(expression));
        Assert.assertEquals("compareValue", compareValue, sut.getCompareValue(expression));
    }

    private static void check(String expression, @NonNull ComparatorTyp expectedTyp,
                              String expectedFieldName, Object expectedCompareValue,
                              @NonNull FormatterApi<?> formatter, Object trueFieldValue,
                              Object falseFieldValue) {
        ComparatorTyp typ = ComparatorTyp.parseExpression(expression);
        Assert.assertNotNull(typ);
        String fieldName = typ.getFieldName(expression);
        Object compareValue = typ.getCompareValue(expression, formatter);

        Comparator<Object> comparator = formatter.getComparator();
        boolean trueResult = typ.compareTo(comparator, trueFieldValue, compareValue);
        boolean falseResult = typ.compareTo(comparator, falseFieldValue, compareValue);
        String expectedExpression = typ.toExpression(fieldName,compareValue, formatter);

        assertEquals("typ", expectedTyp, typ);
        assertEquals("fieldName", expectedFieldName, fieldName);
        assertEquals("compareValue", expectedCompareValue, compareValue);
        assertTrue("trueResult", trueResult);
        assertFalse("falseResult", falseResult);
        assertEquals("toExpression", expectedExpression, expression);
    }
}

