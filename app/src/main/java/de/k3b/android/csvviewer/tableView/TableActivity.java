package de.k3b.android.csvviewer.tableView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.k3b.android.csvviewer.R;
import de.k3b.android.csvviewer.util.AndroidModelHelper;
import de.k3b.android.csvviewer.util.IntentUtil;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelRowContainsFilter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;
import de.k3b.csvviewer.lib.data.model.TableProperties;


public class TableActivity extends AppCompatActivity {
    /** used for logging */
    private static final String TAG = TableActivity.class.getSimpleName();

    /** after this time of no activity containsFilter is executed */
    private static final int CONTAINS_FILTER_DELAY_MILLISECS = 500;

    /** used to persist state change of state member */
    private static final String KEY_INSTANCE_STATE = "STATE";

    /** Activity state saved/retored on screen rotation */
    private State state;

    /** one second after stopping entering text into contains-search-field the filter is applied to loaded csv */
    final private Handler executeDelayedContainsFilterHandler = new Handler(Looper.getMainLooper());

    /** State saved/retored on screen rotation */
    private static class State implements Serializable {
        List<Integer> sortOrder = new ArrayList<>();
        List<ITableModelRowFilter> filterList = new ArrayList<>();
        TableModelRowContainsFilter containsFilter = null;
        String containsFilterSeachText = null;

        String lastCsvSource;

        InMemoryTableModel createFilteredModel(InMemoryTableModel model) {
            model.sortBy(sortOrder);
            return TableModelUtils.filter(model, filterList);
        }
    }

    private RecyclerView recyclerView;
    private LinearLayout headerRow;

    /** last loaded csv data source for error message */
    private InMemoryTableModel modelFiltered;
    private InMemoryTableModel modelLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // savedInstanceState.getString();
        state = savedInstanceState == null ? new State() : (State) savedInstanceState.getSerializable(KEY_INSTANCE_STATE);
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

        getSupportActionBar().setTitle(model.getName());
        this.modelLoaded = model.createClone(model.getName());
        this.modelFiltered = state.createFilteredModel(modelLoaded);

        refilterTableView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_INSTANCE_STATE, this.state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.table_main_menu, menu);

        initSearchView(menu);

        final boolean result = super.onCreateOptionsMenu(menu);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_clear_filter) {
            onActionClearFilterList();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onActionClearFilterList() {
        getFilterList().clear();
        refilterTableView();
    }

    private @NonNull List<ITableModelRowFilter> getFilterList() {
        return state.filterList;
    }

    private void refilterTableView() {
        modelFiltered = TableModelUtils.filter(modelLoaded, getFilterList());
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
            if (state.sortOrder.contains(columnNumber)) text += " ^";
            else if (state.sortOrder.contains(negate(columnNumber))) text += " v";

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
        if (state.sortOrder.remove(descending)) {
            // descending -> nothing
            // removed from sorting
        } else if (state.sortOrder.remove(ascending)) {
                // ascending -> descending
                state.sortOrder.add(0,descending);
                dir ="v";
        } else {
            // nothing -> ascending
            state.sortOrder.add(0,ascending);
            dir ="^";
        }

        this.modelFiltered.sortBy(state.sortOrder);
        Log.i(TAG, modelFiltered.getColumnNames()[columnNumber] + dir + ": " + state.sortOrder);
        refilterTableView();
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

    private boolean onCellLongClick(TextView tv, @NonNull TableModelApi model, int rowIndex, int columnNumber) {
        FormatterApi<?> formatter = TableProperties.getColumnFormatter(model, columnNumber);
        if (formatter != null) {
            String stringValue = tv.getText().toString();
            PopupMenu popup = AndroidModelHelper.createOnCellLongClickMenu(TableActivity.this, tv, formatter, stringValue);
            popup.getMenuInflater().inflate(R.menu.cell_popup_menu_common,
                    popup.getMenu());

            // This activity implements OnMenuItemClickListener.
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    int menuItemId = menuItem.getItemId();
                    if (menuItemId == R.id.action_clear_filter) {
                        onActionClearFilterList();
                    } else {
                        ITableModelRowFilter filter = AndroidModelHelper.createFilterFromMenuClick(model, menuItemId, columnNumber, stringValue);
                        if (filter != null) {
                            getFilterList().add(filter);
                            refilterTableView();
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
            this.state.lastCsvSource = uri.toString();
            try (Reader csvReader = new InputStreamReader(getContentResolver().openInputStream(uri))) {
                try(Csv2TableModel parser = new Csv2TableModel(options)) {
                    DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
                    String name = documentFile != null ? documentFile.getName() : uri.toString();
                    return parser.toTableModel(name, csvReader);
                }
            }
        } else {
            this.state.lastCsvSource = "";
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

    final private Runnable executeDelayedSetContainsFilter = new Runnable() {
        @Override
        public void run() {
            List<ITableModelRowFilter> filterList = getFilterList();
            if (state.containsFilter != null) filterList.remove(state.containsFilter);

            state.containsFilter = null;
            if (state.containsFilterSeachText != null && !state.containsFilterSeachText.isEmpty()) {
                state.containsFilter = new TableModelRowContainsFilter(TableProperties.getColumnFormatters(modelLoaded), state.containsFilterSeachText);
                filterList.add(state.containsFilter);
            }
            refilterTableView();
        }
    };

    private void initSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        // searchView.setIconifiedByDefault(true);

        searchView.setQueryHint("Type here...");

        // Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submit
                // Log.i(TAG,"onQueryTextSubmit(" + query +")");
                // searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                // Cancel previous pending task
                executeDelayedContainsFilterHandler.removeCallbacks(executeDelayedSetContainsFilter);

                state.containsFilterSeachText = newText;
                executeDelayedContainsFilterHandler.postDelayed(executeDelayedSetContainsFilter, CONTAINS_FILTER_DELAY_MILLISECS);

                return false;
            }
        });
    }


}
