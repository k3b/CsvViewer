package de.k3b.csvviewer.lib.csv;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.TableModelApi;

/** Converts {@link TableModelApi} to html-Table */
public class TableModel2Html {

    @NonNull public static String toHtmlTable(@NonNull TableModelApi tableModel, int[] columns, int[] rows, int pageNumber) {
        return new TableModel2Html(tableModel, columns,rows, pageNumber).toHtmlTable();
    }

    /** added before every link text. Note: including a "#" will crash html rendering */
    private static final String LINE_ITEM_PREFIX = "# ";

    private static final String HtmlStart = "<!DOCTYPE html>\n" +
            "<html>\n" +
                "<head>\n" +
                    "<meta charset='UTF-8' />\n" +
                    "<style type='text/css'>\n" +
                            "/* Avoid page breaks inside the most common attributes, especially for exports (i.e. PDF) */\n" +
                            "td, h1, h2, h3, h4, h5, p, ul, ol, li {\n" +
                            "  page-break-inside: avoid; \n" +
                            "}\n" +
                            "td, th {\n" +
                            "  border: 1px solid;\n" +
                            "}\n" +
                    "</style>\n" +
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
                String text = escapeHtml(LINE_ITEM_PREFIX + i);
                if (i == pageNumber) {
                    mdMarkup.append(text);
                } else {
                    addButton(text, "app:/?page=" + i);
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
        addButton(escapeHtml(name), "app:/?col=" + physicalColumn);
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
            addButton(escapeHtml(LINE_ITEM_PREFIX + cell), "app:/?row=" + tableModel.getValueAt(rowNumber, 0));
        } else {
            mdMarkup.append(escapeHtml(cell));
        }
        mdMarkup.append("</td>\n");
    }

    @NonNull
    public static String escapeHtml(@Nullable Object cellObject) {
        String cellString = null;
        if (cellObject != null) cellString = StringEscapeUtils.escapeHtml4(cellObject.toString()
                // "#" inside the column of a html-table stops from table rendering in android
                .trim().replace("#","*"));
        if (cellString == null || cellString.isEmpty()) cellString = BLANK;
        return cellString;
    }

    private void addButton(String text, String link) {
        mdMarkup.append("<button type='button'>");
        mdMarkup.append("<a href='");
        mdMarkup.append(link);
        mdMarkup.append("'>");
        mdMarkup.append(text);
        mdMarkup.append("</a>");
        mdMarkup.append("</button>");
    }
}
