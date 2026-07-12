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

package de.k3b.android.csvviewer.tableView;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jspecify.annotations.NonNull;

import de.k3b.android.csvviewer.R;

public class GuiHelper {

    public static final int MAX_COLUMN_WIDTH = 40;

    @NonNull
    public static TextView createTextView(Context context, Object text, Integer widthInChars) {
        TextView textView = new TextView(context);

        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(Gravity.LEFT);

        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (widthInChars != null && widthInChars > 0) {
            if (widthInChars > MAX_COLUMN_WIDTH) widthInChars= MAX_COLUMN_WIDTH;
            width = (int) Math.ceil(widthInChars * textView.getPaint().measureText("O"))
                    + textView.getPaddingLeft()
                    + textView.getPaddingRight();
        }

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // should draw a box around used part of the cell
        // bug: shows that header and content do not have the same alignment.
        // bug: issue when multiline cell neighbours single line cell.
        textView.setBackgroundResource(R.drawable.table_cell);
        textView.setText(text == null ? "" : text.toString());

        return textView;
    }
}
