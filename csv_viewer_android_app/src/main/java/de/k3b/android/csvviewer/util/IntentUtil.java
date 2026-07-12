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

package de.k3b.android.csvviewer.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class IntentUtil {
    /** get uri from intent: View/Edit/SENDTO(uri=DATA) or SEND(uri=EXTRA_STREAM)  */
    public static Uri getUri(Intent intent) {
        Uri uri = null;
        if (intent != null) {
            uri = intent.getData();

            if (uri == null) {
                String extra = Intent.EXTRA_STREAM;
                Object extraValue = getExtra(intent, extra);
                if (extraValue != null) {
                    uri = Uri.parse(extraValue.toString());
                }
            }
        }
        return uri;
    }

    @Nullable
    public static Object getExtra(Intent intent, String extra) {
        Bundle extras = (intent == null) ? null : intent.getExtras();
        return (extras == null) ? null : extras.get(extra);
    }


}
