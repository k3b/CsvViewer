package de.k3b.android.csvviewer.tableView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import de.k3b.android.csvviewer.R;
import de.k3b.android.csvviewer.util.IntentUtil;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.analyser.TableColumnAnalyser;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.filter.TableModelColumnFilter;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;

public class TableActivity extends AppCompatActivity {
    private static final String TAG = TableActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private LinearLayout headerRow;

    /** last loaded csv data source for error message */
    private String lastCsvSource;
    private InMemoryTableModel model;
    private List<Integer> sortOrder = new ArrayList<>();
    private List<TableModelRowFilterBase> includeFilter = new ArrayList<>();
    private List<TableModelRowFilterBase> excludeFilter = new ArrayList<>();

    @Nullable private int[] columnWidthsInPixel = null;
    private enum COLUMN_INFOS {
        COLUMN_DEFINITIONS
    }

    private <T>  List<T> getInfo(COLUMN_INFOS key) {
        List<T> result = new ArrayList<>(model.getColumnCount());
        for (int col = 0; col < model.getColumnCount(); col++) {
            result.add(model.getColumnProperty(col, key));
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        recyclerView = findViewById(R.id.tableRecycler);
        headerRow = findViewById(R.id.headerRow);

        InMemoryTableModel model = null;
        try {
            model = parseTableModel();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getLocalizedMessage(), e);
            model = createSampleModel();
        }

        AnalyserReport analysed = TableModelUtils.analyse(model, 0);

        TableModelUtils.convertColumns(model, true);

        this.model = model;

        columnWidthsInPixel = calculateColumnWidths(model, analysed);
        updateTableView();
    }

    private int[] calculateColumnWidths(InMemoryTableModel model,AnalyserReport analysed) {
        int columnCount = model.getColumnCount();
        int[] result = new int[columnCount];

        int col_configType = AnalyserReport.DomainColumnModel.col_parser;
        String expression = analysed.getColumnNames()[col_configType] + "=" + TableColumnAnalyser.class.getSimpleName();
        TableModelColumnFilter filter = TableModelColumnFilter.create(col_configType, TableColumnType.String.getFormatter(),
                expression);

        InMemoryTableModel filtered = TableModelUtils.filter(analysed,filter);
        for(int columnNumber = columnCount -1; columnNumber >= 0; columnNumber--) {
            // TODO!!!!
        }
        return result;
    }

    private void updateTableView() {
        setupHeader(model);
        setupRecycler(model);
    }

    private void setupHeader(TableModelApi model) {
        headerRow.removeAllViews();

        String[] columns = model.getColumnNames();

        for (int columnNumber = 0; columnNumber < columns.length; columnNumber++) {
            String text = columns[columnNumber];
            if (sortOrder.contains(columnNumber)) text += " v";
            else if (sortOrder.contains(negate(columnNumber))) text += " ^";

            TextView tv = GuiHelper.createTextView(this, text, model.getColumnMaxWidth(columnNumber));
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            headerRow.addView(tv);
            int finalI = columnNumber;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onHeaderClick(finalI);
                }
            });
        }
    }

    private void onHeaderClick(int columnNumber) {
        Integer descending = negate(columnNumber);
        Integer ascending = columnNumber;
        String dir = "";
        if (sortOrder.remove(descending)) {
            // descending -> nothing
            // removed from sorting
        } else if (sortOrder.remove(ascending)) {
                // ascending -> descending
                sortOrder.add(0,descending);
                dir ="v";
        } else {
            // nothing -> ascending
            sortOrder.add(0,ascending);
            dir ="^";
        }

        this.model.sortBy(sortOrder);
        Log.i(TAG, model.getColumnNames()[columnNumber] + dir + ": " + sortOrder);
        updateTableView();
    }

    private Integer negate(int columnNumber) {
        if (columnNumber == 0) return -9999;
        else return -columnNumber;
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
    public InMemoryTableModel parseTableModel() throws IOException {
        int options = Csv2TableModel.OPTION_ALL;
        Intent intent = getIntent();
        Uri uri = IntentUtil.getUri(intent);
        String html = null;
        if (uri != null) {
            this.lastCsvSource = uri.toString();
            try (Reader csvReader = new InputStreamReader(getContentResolver().openInputStream(uri))) {
                try(Csv2TableModel parser = new Csv2TableModel(options)) {
                    return parser.toTableModel("DemoData", csvReader);
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
                return parser.toTableModel(DemoData.demoCsvName, csvText);
            }
        }
    }

    // Example model implementation
    private InMemoryTableModel createSampleModel() {
        String[] headers = {"ID", "Name", "Age"};

        Object[][] rows = {
                {1, "Alice", 25},
                {2, "Bob", 30},
                {3, "Charlie", 28}
        };
        InMemoryTableModel model = new InMemoryTableModel("SampleData", headers);
        for (Object[] row : rows) {
            model.addRow(row);
        }
        return model;
    }
}
