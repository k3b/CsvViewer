package de.k3b.android.csvviewer.tableView;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jspecify.annotations.NonNull;

import de.k3b.android.csvviewer.R;

public class TableHelper {
    @NonNull
    public static TextView createTextView(Context context, Object text, int widthInChars) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        tv.setPadding(16, 16, 16, 16);
        tv.setGravity(Gravity.LEFT);
        tv.setBackgroundResource(R.drawable.table_cell);
        tv.setText(text == null ? "" : text.toString());
        if (widthInChars > 0) {
            /** todo how to convert from chars to pixels
            int widthInPixel = widthInChars * 20;
            tv.setWidth(widthInPixel);
             */
        }

        return tv;
    }

}
