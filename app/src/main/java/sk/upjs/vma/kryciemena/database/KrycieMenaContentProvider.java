package sk.upjs.vma.kryciemena.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class KrycieMenaContentProvider extends ContentProvider {

    private DatabaseOpenHelper databaseOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
//        String id = uri.getLastPathSegment();
//        String[] whereArgs = {id};

        String table_name = getTableName(uri);

        int affectedRows;

        if (table_name.contains(KrycieMenaContract.Word.TABLENAME)) {
            affectedRows = db.delete(KrycieMenaContract.Word.TABLENAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(KrycieMenaContract.Word.CONTENT_URI, null);
        } else {
            affectedRows = db.delete(KrycieMenaContract.Category.TABLENAME, KrycieMenaContract.Category._ID + " = ?", selectionArgs);
            getContext().getContentResolver().notifyChange(KrycieMenaContract.Category.CONTENT_URI, null);
        }


        return affectedRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table_name = getTableName(uri);

        ContentValues cv = new ContentValues();     // nevieme naisto co vsetko nam pride z values okrem DESCRIPTION
        if (table_name.equals(KrycieMenaContract.Word.TABLENAME)) {
            cv.put(KrycieMenaContract.Word.CATEGORY_ID, values.getAsInteger(KrycieMenaContract.Word.CATEGORY_ID));
            cv.put(KrycieMenaContract.Word.WORD, values.getAsString(KrycieMenaContract.Word.WORD));
        } else {
            cv.put(KrycieMenaContract.Category.CATEGORY, values.getAsString(KrycieMenaContract.Category.CATEGORY));
        }

        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        long id = db.insert(table_name, null, cv);

        if (table_name.equals(KrycieMenaContract.Word.TABLENAME)) {
            getContext().getContentResolver().notifyChange(KrycieMenaContract.Word.CONTENT_URI, null);
            return Uri.withAppendedPath(KrycieMenaContract.Word.CONTENT_URI, String.valueOf(id));   // stare uri s IDckom na konci
        } else {
            getContext().getContentResolver().notifyChange(KrycieMenaContract.Category.CONTENT_URI, null);
            return Uri.withAppendedPath(KrycieMenaContract.Category.CONTENT_URI, String.valueOf(id));   // stare uri s IDckom na konci
        }
    }

    @Override
    public boolean onCreate() {
        databaseOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        String table_name = getTableName(uri);
        Cursor cursor = db.query(table_name, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static String getTableName(Uri uri){
        String value = uri.getPath();
        value = value.replace("/", "");//we need to remove '/'
        return value;
    }
}
