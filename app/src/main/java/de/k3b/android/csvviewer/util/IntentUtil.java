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
