package sk.upjs.vma.kryciemena.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import sk.upjs.vma.kryciemena.KrycieMenaContract;

public class KrycieMenaContentProvider extends ContentProvider {

    private DatabaseOpenHelper databaseOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        String id = uri.getLastPathSegment();
        String[] whereArgs = {id};
        int affectedRows = db.delete(KrycieMenaContract.Word.TABLENAME, KrycieMenaContract.Word._ID + " = ?", whereArgs);

        getContext().getContentResolver().notifyChange(KrycieMenaContract.Word.CONTENT_URI, null);

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
        ContentValues cv = new ContentValues();     // nevieme naisto co vsetko nam pride z values okrem DESCRIPTION
        cv.put(KrycieMenaContract.Word.WORD, values.getAsString(KrycieMenaContract.Word.WORD));

        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        long id = db.insert(KrycieMenaContract.Word.TABLENAME, null, cv);

        getContext().getContentResolver().notifyChange(KrycieMenaContract.Word.CONTENT_URI, null);

        return Uri.withAppendedPath(KrycieMenaContract.Word.CONTENT_URI, String.valueOf(id));   // stare uri s IDckom na konci
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
        Cursor cursor = db.query(KrycieMenaContract.Word.TABLENAME, null, null, null, null, null, KrycieMenaContract.Word.WORD);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
