package de.k3b.android.csvviewer.lib.data.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;

import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class ComparatorTypTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorTypTests.class);

    @Test
    public void integerLessEquals() throws IOException {
        String expression = "myVar<=4";
        ComparatorTyp expectedTyp = ComparatorTyp.LESS_OR_EQUAL;
        String expectedFieldName = "myVar";
        Object expectedCompareValue = Integer.valueOf(4);
        FormatterApi<?> formatter = TableColumnType.Integer.getFormatter();
        Object trueFieldValue = Integer.valueOf(4);
        Object falseFieldValue = Integer.valueOf(5);

        check(expression, expectedTyp, expectedFieldName, expectedCompareValue, formatter, trueFieldValue, falseFieldValue);
    }

    @Test
    public void stringEquals() throws IOException {
        String expression = "myVar=Hallo";
        ComparatorTyp expectedTyp = ComparatorTyp.EQUALS;
        String expectedFieldName = "myVar";
        Object expectedCompareValue = "Hallo";
        FormatterApi<?> formatter = TableColumnType.String.getFormatter();;
        Object trueFieldValue = "haLLo";
        Object falseFieldValue = "moin";

        check(expression, expectedTyp, expectedFieldName, expectedCompareValue, formatter, trueFieldValue, falseFieldValue);
    }

    private static void check(String expression, ComparatorTyp expectedTyp, String expectedFieldName, Object expectedCompareValue, FormatterApi<?> formatter, Object trueFieldValue, Object falseFieldValue) {
        ComparatorTyp typ = ComparatorTyp.parseExpression(expression);
        String fieldName = typ.getFieldName(expression);
        Object compareValue = typ.getCompareValue(expression, formatter);

        Comparator<Object> comparator = formatter == null ? null : formatter.getComparator();
        boolean trueResult = typ.compareTo(comparator, trueFieldValue, compareValue);
        boolean falseResult = typ.compareTo(comparator, falseFieldValue, compareValue);
        String toString = typ.toExpression(fieldName,compareValue, formatter).replace(" ","");

        assertEquals("typ", expectedTyp, typ);
        assertEquals("fieldName", expectedFieldName, fieldName);
        assertEquals("compareValue", expectedCompareValue, compareValue);
        assertTrue("trueResult", trueResult);
        assertFalse("falseResult", falseResult);
        assertEquals("toExpression", expression, toString);
    }
}
