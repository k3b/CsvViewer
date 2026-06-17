Goal: Android App to show/sort/find csv database content in a table view.

<img src="https://raw.githubusercontent.com/wiki/k3b/CsvViewer/CsvViewer.svg" width="80" height="80" >

## How to use CsvViewer?
* Inside a filemanager, click on a file that ends with ".csv". 
  * In the "Open with" popup select "CsvViewer".
* If CsvViewer is startet without  a ".csv" file it shows some demo data.
* Click a header to sort the table. (^ means sort from small to big; v sort from big to small)
* Long click into table cell will open a context menu to filter the table by column.
* Clicking on the search button "🔍" activates the "row contains search"
  * Example "🔍 2001 SuSI" will show all rows that contain (case insentive) in any column "2001" and "susi". 

## Features
* Automatic csv format detection,
  * Supported delimiters: ;,:| or <tab>
  * Supported optional Quoting: "'
  * First csv row is used as header
* automatic column type detection.
  * Date/Time if format is "yyyy-MM-dd HH:mm:ss" (Example "2026-12-24 17:59:55")
  * Yes/No (aka Boolean) if there are only 2 different values.
  * Numbers (Integer/Long) 
  * (not implemented yet): Decimal number (Double)
  * String if no other type is detected.

## Requirements
* MinSdk=21=LOLLIPOP (or later)
* enough memory to keep the complete csv file in memory
