package de.k3b.android.csvviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;

import de.k3b.csvviewer.lib.data.formatter.IntegerFormatter;
import de.k3b.csvviewer.lib.data.formatter.ObjectFormatter;
import de.k3b.csvviewer.lib.data.formatter.StringFormatter;

/**
 * dynamically builds a form from:
 *
 * String[] tableHeader → shown as labels (TextView)
 * Object[] tableRow → editable values (EditText).
 *
 */
public class TableRowEditorActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "data";

    /**
     * Data to be transfered between calling activity and {@link TableRowEditorActivity}
     */
    private static class Data implements Serializable {
        @NonNull
        final String[] labels;
        @NonNull
        final Object[] row;
        @NonNull
        final ObjectFormatter[] formatters;

        Data(@NonNull String[] labels, @NonNull Object[] row, @NonNull ObjectFormatter[] formatters) {
            this.labels = labels;
            this.row = row;
            this.formatters = formatters;
        }

        Data(@NonNull Data tableDefinition, @NonNull Object[] row) {
            this(tableDefinition.labels, row, tableDefinition.formatters);
        }
    }

    public static void startActivity(@NonNull AppCompatActivity parent, int requestCode,
                              @NonNull String[] headers, @NonNull Object[] row,
                              @NonNull ObjectFormatter[] rowFormatters) {

        Intent intent = new Intent(parent, TableRowEditorActivity.class);

        /*
        String[] headers = {"Name", "Age", "City"};
        Object[] row = {"John", 30, "Berlin"};
        */

        Data tableDefinition = new Data(headers, row, rowFormatters);
        intent.putExtra(EXTRA_DATA, tableDefinition);

        parent.startActivityForResult(intent, requestCode);
    }

    private static class TestActivity extends AppCompatActivity {

        private static final int REQUEST_CODE = 100;

        private void test() {
            String[] headers = {"Name", "Age", "City"};
            Object[] row = {"John", 30, "Berlin"};
            ObjectFormatter stringFormatter = new ObjectFormatter(new StringFormatter(true, 20));
            ObjectFormatter intFormatter = new ObjectFormatter(new IntegerFormatter(false));
            ObjectFormatter[] rowFormatters = {stringFormatter,intFormatter,stringFormatter};

            TableRowEditorActivity.startActivity(this, REQUEST_CODE, headers, row, rowFormatters);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                Object[] row = TableRowEditorActivity.getResult(data);
                // Use row
            }
        }
    }

    public static @NonNull Object[] getResult(@NonNull Intent intent) {
        Data data = (Data) intent.getSerializableExtra(EXTRA_DATA);

        assert data != null;
        return data.row;
    }

    private EditText[] editTexts;
    Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        Data data = (Data) intent.getSerializableExtra(EXTRA_DATA);

        int columnCount = data.row.length;
        editTexts = new EditText[columnCount];

        // Root ScrollView (for long forms)
        ScrollView scrollView = new ScrollView(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16);
        container.setPadding(padding, padding, padding, padding);

        scrollView.addView(container);

        if (data.labels != null && data.row != null) {

            for (int i = 0; i < data.labels.length; i++) {

                // Label
                TextView label = new TextView(this);
                label.setText(data.labels[i]);
                label.setTextSize(16f);
                container.addView(label);

                // EditText
                EditText editText = new EditText(this);
                if (i < data.row.length && data.row[i] != null) {
                    editText.setText(data.formatters[i].format(data.row[i]));
                }

                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                container.addView(editText);
                editTexts[i]  = editText;
            }
        }

        // Save Button
        Button saveButton = new Button(this);
        saveButton.setText("Save");

        saveButton.setOnClickListener(this::onSave);

        container.addView(saveButton);

        setContentView(scrollView);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }

    private void onSave(View v) {
        Object[] row = new Object[editTexts.length];

        for (int i = 0; i < editTexts.length; i++) {
            String stringValue = editTexts[i].getText().toString();
            row[i] = data.formatters[i].parse(stringValue);
        }

        // Return result
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, new Data(data, row));
        setResult(RESULT_OK, intent);
        finish();
    }
}