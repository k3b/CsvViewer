package de.k3b.android.csvviewer.csv;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.k3b.android.csvviewer.data.TableModelApi;

/** Converts {@link TableModelApi} to html-Table */
public class TableModel2Html {
    @NonNull public static String toHtmlTable(@NonNull TableModelApi tableModel, int[] columns,int[] rows, int pageNumber) {
        return new TableModel2Html(tableModel, columns,rows, pageNumber).toHtmlTable();
    }

    private static final String HtmlStart = "<!DOCTYPE html []>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset='UTF-8' />\n" +
            "    <style type='text/css'>\n" +
            "            \n" +
            "/* Avoid page breaks inside the most common attributes, especially for exports (i.e. PDF) */\n" +
            "td, h1, h2, h3, h4, h5, p, ul, ol, li {\n" +
            "    page-break-inside: avoid; \n" +
            "}\n" +
            "    </style>\n" +
            "  </head>\n" +
            "<body>\n";
    private static final String HtmlEnd = "</body>\n</html>\n";

    private static final String TableStart = "<table><thead><tr>\n";
    private static final String TableHeaderEnd = "</tr></thead><tbody>\n";
    private static final String TableEnd = "</tbody></table>\n";

    private static final String BLANK = "&nbsp;";

    @NonNull private final StringBuilder mdMarkup = new StringBuilder();
    @NonNull private final TableModelApi tableModel;
    private final int[] columns;
    private final int[] rows;
    private final int pageNumber;

    private TableModel2Html(@NonNull TableModelApi tableModel, int[] columns, int[] rows, int pageNumber) {
        this.tableModel = tableModel;
        this.columns = columns;
        this.rows = rows;
        this.pageNumber = pageNumber;
    }

    @NonNull private String toHtmlTable() {
        mdMarkup.append(HtmlStart);
        addPager();

        addTableHeader();
        if (rows != null && rows.length > 0) {
            for (int id : rows) {
                addRow(id, tableModel.getRow(id));
            }
        } else {
            int rowCount = tableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                addRow(i, tableModel.getRow(i));
            }
        }
        mdMarkup.append(TableEnd);

        addPager();

        mdMarkup.append(HtmlEnd);
        return mdMarkup.toString();
    }

    private void addPager() {
        if (rows != null && rows.length > 0) {
            int rowCount = tableModel.getRowCount();
            int pageSize = rowCount / rows.length;
            int maxPage = (rowCount / pageSize) + 1;
            mdMarkup.append("<p>");

            for(int i = 1; i <= maxPage; i++) {
                String text = "#" + i;
                if (i == pageNumber) {
                    mdMarkup.append(text);
                } else {
                    addButton(text, "app:page?id=" + i);
                }
                mdMarkup.append(BLANK);
            }
            mdMarkup.append("</p>");
        }
    }


    private void addTableHeader() {
        mdMarkup.append(TableStart);
        String[] names = tableModel.getColumnNames() ;
        if (columns != null && columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                int id = columns[i];
                addHeaderColumn(i, id, names[id]);
            }
        } else {
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                addHeaderColumn(i, i, names[i]);

            }
        }
        mdMarkup.append(TableHeaderEnd);
    }

    private void addHeaderColumn(int virtualColumn, int physicalColumn, String name) {
        mdMarkup.append("<th>");
        addButton(toString(name), "app:col?id=" + physicalColumn);
        mdMarkup.append("</th>\n");
    }

    private void addRow(int rowNumber, @NonNull Object[] row) {
        mdMarkup.append("<tr>\n");

        if (columns != null && columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                int id = columns[i];
                addRowColumn(rowNumber,i, id, row[id]);
            }
        } else {
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                addRowColumn(rowNumber,i, i, row[i]);
            }
        }
        mdMarkup.append("</tr>\n");
    }

    private void addRowColumn(int rowNumber, int virtualColumn, int physicalColumn,@Nullable Object cell) {
        mdMarkup.append("<td>");
        if (virtualColumn == 0) {
            addButton("#" + toString(cell), "app:row?id=" + tableModel.getValueAt(rowNumber, 0));
        } else {
            mdMarkup.append(toString(cell));
        }
        mdMarkup.append("</td>\n");
    }

    @NonNull
    private String toString(@Nullable Object cellObject) {
        String cellString = null;
        if (cellObject != null) cellString = cellObject.toString().trim();
        if (cellString == null || cellString.isEmpty()) cellString = BLANK;
        return cellString;
    }

    private void addButton(String text, String link) {
        mdMarkup.append("<a href='").append(link)
                .append("'><button type='button'>")
                .append(text).append("</button></a>");
    }
}
