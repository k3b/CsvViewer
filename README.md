Goal: Android App to show csv data in a table view.

## How to use CsvViewer?
* Inside a filemanager, click on a file that ends with ".csv". In the "Open with" popup select "CsvViewer".
* If CsvViewer is startet without  a ".csv" file it shows some demo data.
* click a header to sort the table.
* long click into table cell will open a context menu to filter the table.

## Features
* automatic csv format detection,
  * Supported delimiters: ;,:| or <tab>
  * Supported optional Quoting: "'
  * first csv row is used as header
* automatic column type detection.
  * Date/Time if format is "yyyy-MM-dd HH:mm:ss" (Example "2026-12-24 17:59:55")
  * Yes/No (aka Boolean) if there are only 2 different values.
  * Numbers (Integer/Long) 
  * (not implemented yet): Decimal number (Double)
  * String if no other type is detected.

## Requirements
* MinSdk=21=LOLLIPOP (or later)
* enough memory to keep the complete csv file in memory
