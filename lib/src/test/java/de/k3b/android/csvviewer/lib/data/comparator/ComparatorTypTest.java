package de.k3b.android.csvviewer.lib.data.comparator;

import org.junit.Assert;
import org.junit.Test;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;

public class ComparatorTypTest {
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
}
