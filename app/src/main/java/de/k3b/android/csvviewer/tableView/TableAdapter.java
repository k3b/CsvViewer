package de.k3b.android.csvviewer.tableView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.k3b.android.csvviewer.R;
import de.k3b.csvviewer.lib.data.TableModelApi;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.RowViewHolder> {

    private final TableModelApi model;

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
        holder.bind(model, position);
    }

    @Override
    public int getItemCount() {
        return model.getRowCount();
    }

    static class RowViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rowContainer;

        RowViewHolder(View itemView) {
            super(itemView);
            rowContainer = itemView.findViewById(R.id.rowContainer);
        }

        void bind(TableModelApi model, int rowIndex) {
            rowContainer.removeAllViews();

            Object[] row = model.getRow(rowIndex);

            for (int i = 0; i < row.length; i++) {
                Object cell = row[i];
                TextView tv = TableHelper.createTextView(itemView.getContext(), cell, model.getColumnWidth(i));

                rowContainer.addView(tv);
            }
        }
    }
}
