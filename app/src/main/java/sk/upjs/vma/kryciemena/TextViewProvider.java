package sk.upjs.vma.kryciemena;

import android.content.Context;
import android.widget.TextView;

public class TextViewProvider {

        public static TextView getTextViewForAlertDialog(Context context, String message) {
            TextView textView = new TextView(context);
            textView.setTextSize(18);
            textView.setPadding(30, 0, 20, 0);
            textView.setText(message);
            return textView;
        }
    }