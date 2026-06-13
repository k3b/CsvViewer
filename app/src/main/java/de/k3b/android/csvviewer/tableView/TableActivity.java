package de.k3b.android.csvviewer.tableView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.k3b.android.csvviewer.R;
import de.k3b.android.csvviewer.util.IntentUtil;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.filter.TableModelColumnFilter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.model.TableProperties;

public class TableActivity extends AppCompatActivity {
    private static final String TAG = TableActivity.class.getSimpleName();
    private static final int DYNAMIC_MENU_FIRST = 32411; // View.generateViewId();

    private RecyclerView recyclerView;
    private LinearLayout headerRow;

    /** last loaded csv data source for error message */
    private String lastCsvSource;
    private InMemoryTableModel modelFiltered;
    private InMemoryTableModel modelLoaded;
    private List<Integer> sortOrder = new ArrayList<>();

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

        this.modelLoaded = model;

        updateTableView(model.createClone(model.getName()));
    }

    private void updateTableView(@NonNull InMemoryTableModel modifiedModel) {
        modelFiltered = modifiedModel;
        updateTableView();
    }

    private void updateTableView() {
        setupHeader(modelFiltered);
        setupRecycler(modelFiltered);
    }

    private void setupHeader(TableModelApi model) {
        headerRow.removeAllViews();

        String[] columns = model.getColumnNames();

        for (int columnNumber = 0; columnNumber < columns.length; columnNumber++) {
            String text = columns[columnNumber];
            if (sortOrder.contains(columnNumber)) text += " ^";
            else if (sortOrder.contains(negate(columnNumber))) text += " v";

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

        this.modelFiltered.sortBy(sortOrder);
        Log.i(TAG, modelFiltered.getColumnNames()[columnNumber] + dir + ": " + sortOrder);
        updateTableView();
    }

    private Integer negate(int columnNumber) {
        if (columnNumber == 0) return -9999;
        else return -columnNumber;
    }

    private void setupRecycler(TableModelApi model) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TableAdapter adapter = new TableAdapter(model);
        adapter.registerLongCellClickEvent(new TableAdapter.LongCellClickListener() {
            @Override
            public boolean onCellLongClick(TextView tv, @NonNull TableModelApi model, int rowIndex, int columnNumber) {
                return TableActivity.this.onCellLongClick(tv, model, rowIndex, columnNumber);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void renameMenuItem(@NonNull TreeMap<Integer, String> menuDefintion,
                                @NonNull ComparatorTyp typ,@Nullable String newMenuText) {
        if (menuDefintion.get(typ.getMenuOffset()) != null) {
            menuDefintion.put(typ.getMenuOffset(), newMenuText);
        }
    }

    private boolean onCellLongClick(TextView tv, @NonNull TableModelApi model, int rowIndex, int columnNumber) {
        FormatterApi<?> formatter = TableProperties.getColumnFormatter(model, columnNumber);
        if (formatter != null) {
            String stringValue = tv.getText().toString();
            PopupMenu popup = createOnCellLongClickMenu(tv, formatter, stringValue);

            // This activity implements OnMenuItemClickListener.
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    List<TableModelRowFilterBase> filterList = TableProperties.getColumnFilterList(model);
                    if (filterList == null) {
                        filterList = new ArrayList<>();
                        TableProperties.setColumnFilterList(model, filterList);
                    }

                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.action_clear_filter) {
                        filterList.clear();
                        TableActivity.this.updateTableView(TableModelUtils.filter(TableActivity.this.modelLoaded, filterList));
                    } else {
                        ComparatorTyp comparatorTyp = ComparatorTyp.getComparatorTyp(itemId - DYNAMIC_MENU_FIRST);
                        if (comparatorTyp != null) {
                            String expression = comparatorTyp.toExpression(model.getColumnNames()[columnNumber], stringValue);

                            FormatterApi<?>  formatter = TableProperties.getColumnFormatter(TableActivity.this.modelLoaded, columnNumber);
                            TableModelColumnFilter filter = TableModelColumnFilter.create(columnNumber, formatter,
                                    expression);
                            filterList.add(filter);
                            TableActivity.this.updateTableView(TableModelUtils.filter(TableActivity.this.modelLoaded, filterList));
                        }
                    }
                    return true;
                }
            });

            // int menuRes = R.menu.cell_popup_numeric;
            // popup.getMenuInflater().inflate(menuRes, popup.getMenu());
            popup.show();
            return true;
        } // if (formatter != null)
        return false;
    } // onCellLongClick

    @NonNull
    private PopupMenu createOnCellLongClickMenu(TextView tv, FormatterApi<?> formatter, String stringValue) {
        List<ComparatorTyp> allowed = formatter.getAllowedComparators();
        final TreeMap<Integer, String> menuDefinition = ComparatorTyp.createMenu(stringValue, allowed);

        // translate non symbol menu titles
        renameMenuItem(menuDefinition, ComparatorTyp.IS_NULL, getString(R.string.empty));
        renameMenuItem(menuDefinition, ComparatorTyp.IS_NOT_NULL, getString(R.string.non_empty));

        // convert into android menu
        PopupMenu popup = new PopupMenu(this, tv);
        Menu menu = popup.getMenu();
        for (Integer id : menuDefinition.keySet()) {
            ComparatorTyp comparatorTyp = ComparatorTyp.getComparatorTyp(id);
            if (comparatorTyp != null) {
                String title = comparatorTyp.toExpression("", stringValue);
                // groupId,itemId,order,title)
                menu.add(Menu.NONE, DYNAMIC_MENU_FIRST + id, 5, title);
            }
        } // for (Integer id : menuDefinition.keySet())

        popup.getMenuInflater().inflate(R.menu.cell_popup_menu_common,
                popup.getMenu());

        return popup;
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
