package de.k3b.android.csvviewer.tableView;

import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jspecify.annotations.NonNull;

import de.k3b.android.csvviewer.R;

public class GuiHelper {
    @NonNull
    public static TextView createTextView(Context context, Object text, int widthInChars, int columnNumber) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(Gravity.LEFT);
        // should draw a box around used part of the cell
        // bug: shows that header and content do not have the same alignment.
        // bug: issue when multiline cell neighbours single line cell.
        textView.setBackgroundResource(R.drawable.table_cell);
        textView.setText(text == null ? "" : text.toString());
        new StringBuilder("-",20);
        textView.setWidth(2 * (columnNumber+1)); // GuiHelper.getWithInPixel(tv, text));

        if (widthInChars > 0) {
            /** todo how to convert from chars to pixels
            int widthInPixel = widthInChars * 20;
            textView.setWidth(widthInPixel);
             */
        }

        return textView;
    }

    public static int getWithInPixel(TextView textView, String text) {
        Paint paint = textView.getPaint();

        // Width in pixels
        float widthPx = paint.measureText(text);
        return (int) widthPx;
    }
}
