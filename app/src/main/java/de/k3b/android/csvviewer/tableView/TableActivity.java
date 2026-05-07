package de.k3b.android.csvviewer.tableView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import de.k3b.android.csvviewer.R;
import de.k3b.android.csvviewer.util.IntentUtil;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.TableModelApi;

public class TableActivity extends AppCompatActivity {
    public static final String LOG_TAG = "TableActivity";

    private RecyclerView recyclerView;
    private LinearLayout headerRow;

    /** last loaded csv data source for error message */
    private String lastCsvSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        recyclerView = findViewById(R.id.tableRecycler);
        headerRow = findViewById(R.id.headerRow);

        TableModelApi model = null;
        try {
            model = getTableModel();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
            model = createSampleModel();
        }

        setupHeader(model);
        setupRecycler(model);
    }

    private void setupHeader(TableModelApi model) {
        headerRow.removeAllViews();

        String[] columns = model.getColumnNames();

        for (int i = 0; i < columns.length; i++) {
            String text = columns[i];
            TextView tv = TableHelper.createTextView(this, text, model.getColumnWidth(i));
            // tv.setTypeface(Typeface.DEFAULT_BOLD);
            headerRow.addView(tv);
        }
    }

    private void setupRecycler(TableModelApi model) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TableAdapter(model));
    }

    /**
     *
     * @return table model from
     * * View/Edit/SENDTO(uri=DATA) or
     * * SEND(uri=EXTRA_STREAM)
     * * Clip-Text(csv=EXTRA_TEXT)
     * * else demo data
     * @throws IOException
     */
    public TableModelApi getTableModel() throws IOException {
        int options = Csv2TableModel.OPTION_ALL;
        Intent intent = getIntent();
        Uri uri = IntentUtil.getUri(intent);
        String html = null;
        if (uri != null) {
            this.lastCsvSource = uri.toString();
            try (Reader csvReader = new InputStreamReader(getContentResolver().openInputStream(uri))) {
                try(Csv2TableModel parser = new Csv2TableModel(options)) {
                    TableModelApi model = parser.toTableModel(csvReader);
                    return model;
                }
            }
        } else {
            this.lastCsvSource = "";
            String csvText = DemoData.demoCsv;
            Object extraValue = IntentUtil.getExtra(intent, Intent.EXTRA_TEXT);
            if (extraValue != null) {
                csvText = extraValue.toString();
            }

            try(Csv2TableModel parser = new Csv2TableModel(options)) {
                TableModelApi model = parser.toTableModel(csvText);
                return model;
            }
        }
    }

    // Example model implementation
    private TableModelApi createSampleModel() {
        return new TableModelApi() {

            String[] headers = {"ID", "Name", "Age"};

            Object[][] data = {
                    {1, "Alice", 25},
                    {2, "Bob", 30},
                    {3, "Charlie", 28}
            };

            @Override
            public Object getValueAt(int row, int column) {
                return data[row][column];
            }

            @Override
            public Object[] getRow(int row) {
                return data[row];
            }

            /**
             * Sets the value for the cell in the table model at <code>row</code>
             * and <code>column</code>.
             * <p>
             * <b>Note</b>: The column is specified in the table view's display
             * order, and not in the <code>TableModel</code>'s column
             * order.  This is an important distinction because as the
             * user rearranges the columns in the table,
             * the column at a given index in the view will change.
             * Meanwhile the user's actions never affect the model's
             * column ordering.
             *
             * <code>aValue</code> is the new value.
             *
             * @param aValue the new value
             * @param row    the row of the cell to be changed
             * @param column the column of the cell to be changed
             * @see #getValueAt
             */
            @Override
            public void setValueAt(@Nullable Object aValue, int row, int column) {

            }

            @Override
            public int getRowCount() {
                return data.length;
            }

            /**
             * Returns the number of columns in the column model. Note that this may
             * be different from the number of columns in the table model.
             *
             * @return the number of columns in the table
             * @see #getRowCount
             */
            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public String[] getColumnNames() {
                return headers;
            }

            /**
             * @param rowCandidate
             * @return a valid version of rowCandidate.
             */
            @Override
            public @NonNull Object[] fixRow(@androidx.annotation.Nullable @Nullable Object[] rowCandidate) {
                return new Object[0];
            }
        };
    }
}
