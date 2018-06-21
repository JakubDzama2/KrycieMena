package sk.upjs.vma.kryciemena.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.upjs.vma.kryciemena.KrycieMenaContract;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context) {
        super(context, KrycieMenaContract.Word.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable());

        insertSampleEntry(db, "strom");
        insertSampleEntry(db, "otec");
        insertSampleEntry(db, "tulipán");
        insertSampleEntry(db, "kukučka");
        insertSampleEntry(db, "posteľ");
        insertSampleEntry(db, "Amerika");
        insertSampleEntry(db, "škola");
        insertSampleEntry(db, "Slovensko");
        insertSampleEntry(db, "okno");
        insertSampleEntry(db, "syr");
        insertSampleEntry(db, "počítač");
        insertSampleEntry(db, "ruža");
        insertSampleEntry(db, "hodiny");
        insertSampleEntry(db, "ruka");
        insertSampleEntry(db, "kábel");
        insertSampleEntry(db, "obraz");
        insertSampleEntry(db, "cukor");
        insertSampleEntry(db, "sandále");
        insertSampleEntry(db, "hlava");
        insertSampleEntry(db, "voda");
        insertSampleEntry(db, "med");
        insertSampleEntry(db, "brat");
        insertSampleEntry(db, "skriňa");
        insertSampleEntry(db, "nôž");
        insertSampleEntry(db, "vrch");
        insertSampleEntry(db, "Cristiano Ronaldo");
    }

    private void insertSampleEntry(SQLiteDatabase db, String word) {
        ContentValues values = new ContentValues();
        values.put(KrycieMenaContract.Word.WORD, word);
        db.insert(KrycieMenaContract.Word.TABLENAME, null, values);
    }

    private String createTable() {
        String sqlTemplate = "CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                "%s TEXT" +
                ")";

        return String.format(sqlTemplate,
                KrycieMenaContract.Word.TABLENAME,
                KrycieMenaContract.Word._ID,
                KrycieMenaContract.Word.WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
