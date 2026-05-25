package de.k3b.csvviewer.lib.data.comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

/** used to sort a TableModell by column numbers */
public class TableModelRowComparator implements Comparator<Object[]> {
    private final int col;
    private final @NonNull Comparator<Object> comparator;
    private final int inverse;

    private final TableModelRowComparator next;

    /** create a TableModelRowComparator sorted by columns in columnNos. Negative columnNo means reverse order */
    public static TableModelRowComparator create(@NonNull FormatterApi<?>[] columnDefinitions, List<Integer> columnNos) {
        TableModelRowComparator result = null;
        for (int i = columnNos.size() - 1; i >= 0; i--) {
            boolean inverse = false;
            int columnNo = columnNos.get(i);
            if (columnNo < 0) {
                inverse = true;
                columnNo = columnNo * -1;
            }
            if(columnNo >= columnDefinitions.length) columnNo = 0;

            FormatterApi<?> columnDefinition = columnDefinitions[columnNo];
            Comparator<Object> comparator;
            if (columnDefinition == null) {
                comparator = null;
            } else {
                FormatterApi<?> formatter = columnDefinition;
                comparator = formatter == null ? null : formatter.getComparator();
            }
            if (comparator != null) {
                result = new TableModelRowComparator(columnNo, comparator,inverse, result);
            }
        }
        return result;
    }

    public TableModelRowComparator(int col, @NonNull Comparator<Object> comparator, boolean inverse, @Nullable TableModelRowComparator next) {
        this.col = col;
        this.comparator = comparator;
        this.inverse = inverse ? -1 : 1;
        this.next = next;
    }

    public int compare(Object[] row1, Object[] row2) {
        int result = comparator.compare(row1[col],row2[col]) * inverse;
        if (result == 0 && next != null) {
            result = next.compare(row1,row2);
        }
        return result;
    }

    @Override
    public String toString() {
        return "TableModelRowComparator{" +
                "col=" + col +
                ", comparator=" + (comparator == null ? "" : comparator.getClass().getSimpleName()) +
                ", inverse=" + inverse +
                ", next=" + next +
                '}';
    }
}
