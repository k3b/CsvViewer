package de.k3b.android.csvviewer.util;

import android.content.Context;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.TreeMap;

import de.k3b.android.csvviewer.R;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableFilterFactory;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.model.TableProperties;

/** Android specific extensions for {@link TableModelApi} */
public class AndroidModelHelper {
    public static final int DYNAMIC_MENU_FIRST = 32411; // View.generateViewId();

    private static void renameMenuItem(@NonNull TreeMap<Integer, String> menuDefintion,
                                       @NonNull ComparatorTyp typ, @Nullable String newMenuText) {
        if (menuDefintion.get(typ.getMenuOffset()) != null) {
            menuDefintion.put(typ.getMenuOffset(), newMenuText);
        }
    }

    /** translates android independent menu {@link ComparatorTyp#createMenu(String, List)} to Android specific {@link PopupMenu} */
    @NonNull
    public static PopupMenu createOnCellLongClickMenu(Context context, TextView tv, FormatterApi<?> formatter, String stringValue) {
        List<ComparatorTyp> allowed = formatter.getAllowedComparators();
        final TreeMap<Integer, String> menuDefinition = ComparatorTyp.createMenu(stringValue, allowed);

        // translate non symbol menu titles
        renameMenuItem(menuDefinition, ComparatorTyp.IS_NULL, context.getString(R.string.empty));
        renameMenuItem(menuDefinition, ComparatorTyp.IS_NOT_NULL, context.getString(R.string.non_empty));

        // convert into android menu
        PopupMenu popup = new PopupMenu(context, tv);
        Menu menu = popup.getMenu();
        for (Integer id : menuDefinition.keySet()) {
            ComparatorTyp comparatorTyp = ComparatorTyp.getComparatorTyp(id);
            if (comparatorTyp != null) {
                String title = comparatorTyp.toExpression("", stringValue);
                // groupId,itemId,order,title)
                menu.add(Menu.NONE, DYNAMIC_MENU_FIRST + id, 5, title);
            }
        } // for (Integer id : menuDefinition.keySet())

        return popup;
    }

    /** translates Android specific manu-click from {@link PopupMenu} to {@link ITableModelRowFilter} */
    public static ITableModelRowFilter createFilterFromMenuClick(@NonNull TableModelApi model, int menuItemId, int columnNumber, String stringValue) {
        ITableModelRowFilter filter = null;
        ComparatorTyp comparatorTyp = ComparatorTyp.getComparatorTyp(menuItemId - DYNAMIC_MENU_FIRST);
        if (comparatorTyp != null) {
            String expression = comparatorTyp.toExpression(model.getColumnNames()[columnNumber], stringValue);

            FormatterApi<?>  formatter = TableProperties.getColumnFormatter(model, columnNumber);
            filter = TableFilterFactory.create(columnNumber, formatter,
                    expression);
        }
        return filter;
    }
}
