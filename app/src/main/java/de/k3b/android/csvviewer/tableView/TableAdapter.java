package de.k3b.android.csvviewer.tableView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import de.k3b.android.csvviewer.R;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.model.TableProperties;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.RowViewHolder> {
    @FunctionalInterface
    public interface LongCellClickListener {
        boolean onCellLongClick(TextView tv, @NonNull TableModelApi model, int rowIndex, int columnNumber);
    }

    private final TableModelApi model;
    private TableAdapter.LongCellClickListener longCellClickListener = null;

    public TableAdapter(TableModelApi model) {
        this.model = model;
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_row, parent, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        holder.bind(model, position, longCellClickListener);
    }

    @Override
    public int getItemCount() {
        return model.getRowCount();
    }

    public void registerLongCellClickEvent(LongCellClickListener eventHandler) {
        this.longCellClickListener = eventHandler;
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rowContainer;

        RowViewHolder(View itemView) {
            super(itemView);
            rowContainer = itemView.findViewById(R.id.rowContainer);
        }

        void bind(@NonNull final TableModelApi model, final int rowIndex,
                  final LongCellClickListener longCellClickListener) {
            rowContainer.removeAllViews();

            Object[] row = model.getRow(rowIndex);

            for (int columnNumber = 0; columnNumber < row.length; columnNumber++) {
                Object cell = row[columnNumber];
                FormatterApi<?> formatter = TableProperties.getColumnFormatter(model, columnNumber);
                if (formatter != null) cell = formatter.formatObject(cell);
                final TextView tv = GuiHelper.createTextView(itemView.getContext(), cell, model.getColumnMaxWidth(columnNumber));

                if (longCellClickListener != null) {
                    final int finalColumnNumber = columnNumber;
                    tv.setOnLongClickListener(view -> longCellClickListener.onCellLongClick(tv, model, rowIndex, finalColumnNumber));
                } else {
                    tv.setOnLongClickListener(null);
                }
                rowContainer.addView(tv);
            }
        }

    }
}
