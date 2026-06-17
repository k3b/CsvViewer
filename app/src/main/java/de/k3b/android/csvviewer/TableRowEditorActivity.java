/*
 * Copyright (c) 2026 by k3b.
 *
 * This file is part of https://github.com/k3b/CsvViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.csvviewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
@NullMarked // all elements that have no (Non)Null-annotation are assumted to be NonNull
public class TableRowEditorActivity extends AppCompatActivity {
    /** intent extra parameter to put into */
    private static final String EXTRA_DATA = "data";

    private static final String TYPE_DATE = Date.class.getSimpleName();
    private static final List<String> TYPE_NUMBERS = Arrays.asList(Integer.class.getSimpleName(), Long.class.getSimpleName());

    /**
     * Data to be transfered between calling activity and {@link TableRowEditorActivity}
     *
     * note: @Nullable Object @NonNull [] row; means row is non null but its elements can be null
     */
    private static class Data implements Serializable {
        final String[]  labels;
        
        // row cannot be null but can contain null elements
        final @Nullable Object[] row;
        final @Nullable ObjectFormatter [] formatters;

        Data(String[] labels,
             @Nullable Object[] row,
             @Nullable ObjectFormatter[] formatters) {
            assert row.length == labels.length ;
            assert row.length == formatters.length ;

            this.labels = labels;
            this.row = row;
            this.formatters = formatters;

        }

        Data(Data tableDefinition, @Nullable Object[] row) {
            this(tableDefinition.labels, row, tableDefinition.formatters);
        }
    }

    public static void startActivity(AppCompatActivity parent, int requestCode,
                                     String[] labels,
                                     @Nullable Object[] row,
                                     @Nullable ObjectFormatter[] formatters) {

        Intent intent = new Intent(parent, TableRowEditorActivity.class);

        Data data = new Data(labels, row, formatters);
        intent.putExtra(EXTRA_DATA, data);

        parent.startActivityForResult(intent, requestCode);
    }

    private static class TestActivity extends AppCompatActivity {

        private static final int REQUEST_CODE = 100;

        private void test() {
            String[] headers = {"Name", "Age", "City"};
            Object[] row = {"John", 30, "Berlin"};
            ObjectFormatter stringFormatter = new ObjectFormatter(new StringFormatter(true, 20));
            ObjectFormatter intFormatter = new ObjectFormatter(new IntegerFormatter(false));
            ObjectFormatter[] rowFormatters = {null,intFormatter,stringFormatter};

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

    public static  @Nullable Object[] getResult(Intent intent) {
        Data data = (Data) intent.getSerializableExtra(EXTRA_DATA);

        assert data != null;
        return data.row;
    }

    private @Nullable TextView @Nullable [] valueViews = null;
    private @Nullable  Data data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Data data = (Data) intent.getSerializableExtra(EXTRA_DATA);

        assert data != null;
        int columnCount = data.row.length;
        valueViews = new EditText[columnCount];

        // Root ScrollView (for long forms)
        ScrollView scrollView = new ScrollView(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16);
        container.setPadding(padding, padding, padding, padding);

        scrollView.addView(container);

        for (int i = 0; i < data.labels.length; i++) {

            // Label
            TextView label = new TextView(this);
            label.setText(data.labels[i]);
            label.setTextSize(16f);
            container.addView(label);

            // EditText
            TextView editText = createValueView(data.row[i], data.formatters[i]);
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            container.addView(editText);
            valueViews[i]  = editText;
        }

        // Save Button
        Button saveButton = new Button(this);
        saveButton.setText("Save");

        saveButton.setOnClickListener(this::onSave);

        container.addView(saveButton);

        setContentView(scrollView);
    }

    private TextView createValueView(@Nullable Object nativeValue, @Nullable ObjectFormatter formatter) {
        TextView valueView;
        String stringValue;
        if (formatter == null) {
            // no formatter means readonly
            valueView = new TextView(this);
            stringValue = nativeValue == null ? "" : nativeValue.toString();
        } else {
            valueView = new EditText(this);
            stringValue = formatter.format(nativeValue);
            String elementClassName = formatter.getElementClassName();
            if (TYPE_NUMBERS.contains(elementClassName)) {
                valueView.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (TYPE_DATE.equals(elementClassName)) {
                valueView.setInputType(InputType.TYPE_CLASS_DATETIME);
            }
        }
        valueView.setText(stringValue);

        return valueView;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }

    private void onSave(View v) {
        Object[] row = new Object[valueViews.length];

        for (int i = 0; i < valueViews.length; i++) {
            row[i] = getNativeValue(valueViews[i].getText().toString(), data.formatters[i], data.row[i]);
        }

        // Return result
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, new Data(data, row));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Nullable
    private static Object getNativeValue(@Nullable String stringValue, @Nullable ObjectFormatter formatter, @Nullable Object oldNativeValue) {
        Object nativeValue = null;
        if (formatter != null) {
            if (stringValue != null && !stringValue.isEmpty() ) {
                nativeValue = formatter.parse(stringValue);
            }
            if (nativeValue == null && !formatter.isNullable()) {
                nativeValue = oldNativeValue;
            }
        } else {
            nativeValue = oldNativeValue;
        }
        return nativeValue;
    }
}