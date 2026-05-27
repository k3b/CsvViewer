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
            width = (int) Math.ceil(widthInChars * textView.getPaint().measureText("h"))
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
