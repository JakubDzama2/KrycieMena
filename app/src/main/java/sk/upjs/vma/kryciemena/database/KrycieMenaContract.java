package sk.upjs.vma.kryciemena.database;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.TextView;

public interface KrycieMenaContract {

    String AUTHORITY = "sk.upjs.vma.kryciemena";

    interface Word extends BaseColumns {

        String DATABASE_NAME = "krycie_mena";
        String TABLENAME = "word";
        String WORD = "word";
        String CATEGORY_ID = "category_id";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLENAME)
                .build();
    }

    interface Category extends BaseColumns {

        String DATABASE_NAME = "krycie_mena";
        String TABLENAME = "category";
        String CATEGORY = "category";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLENAME)
                .build();
    }


}
