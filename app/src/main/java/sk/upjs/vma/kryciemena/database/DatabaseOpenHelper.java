package sk.upjs.vma.kryciemena.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context) {
        super(context, KrycieMenaContract.Word.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + KrycieMenaContract.Word.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + KrycieMenaContract.Category.TABLENAME);

        db.execSQL(createCategoryTable());
        Log.d("DB", "onCreate: "+ createCategoryTable());
        db.execSQL(createWordTable());


        ContentValues cv = new ContentValues();
        cv.put(KrycieMenaContract.Category.CATEGORY, "Mix");
        long id = db.insert(KrycieMenaContract.Category.TABLENAME, null, cv);

        insertSampleEntry(db, id, "strom");
        insertSampleEntry(db, id, "otec");
        insertSampleEntry(db, id, "tulipán");
        insertSampleEntry(db, id, "kukučka");
        insertSampleEntry(db, id, "posteľ");
        insertSampleEntry(db, id, "Amerika");
        insertSampleEntry(db, id, "škola");
        insertSampleEntry(db, id, "Slovensko");
        insertSampleEntry(db, id, "okno");
        insertSampleEntry(db, id, "syr");
        insertSampleEntry(db, id, "počítač");
        insertSampleEntry(db, id, "ruža");
        insertSampleEntry(db, id, "hodiny");
        insertSampleEntry(db, id, "ruka");
        insertSampleEntry(db, id, "kábel");
        insertSampleEntry(db, id, "obraz");
        insertSampleEntry(db, id, "cukor");
        insertSampleEntry(db, id, "sandále");
        insertSampleEntry(db, id, "hlava");
        insertSampleEntry(db, id, "voda");
        insertSampleEntry(db, id, "med");
        insertSampleEntry(db, id, "brat");
        insertSampleEntry(db, id, "skriňa");
        insertSampleEntry(db, id, "nôž");
        insertSampleEntry(db, id, "vrch");
        insertSampleEntry(db, id, "Cristiano Ronaldo");
    }

    private void insertSampleEntry(SQLiteDatabase db, long id, String word) {
        ContentValues values = new ContentValues();
        values.put(KrycieMenaContract.Word.WORD, word);
        values.put(KrycieMenaContract.Word.CATEGORY_ID, id);
        db.insert(KrycieMenaContract.Word.TABLENAME, null, values);
    }

    private String createWordTable() {
        String sqlTemplate = "CREATE TABLE IF NOT EXISTS %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER, " +
                "%s TEXT" +
                ")";

        return String.format(sqlTemplate,
                KrycieMenaContract.Word.TABLENAME,
                KrycieMenaContract.Word._ID,
                KrycieMenaContract.Word.CATEGORY_ID,
                KrycieMenaContract.Word.WORD);
    }

    private String createCategoryTable() {
        String sqlTemplate = "CREATE TABLE IF NOT EXISTS %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT" +
                ")";

        return String.format(sqlTemplate,
                KrycieMenaContract.Category.TABLENAME,
                KrycieMenaContract.Category._ID,
                KrycieMenaContract.Category.CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KrycieMenaContract.Word.TABLENAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KrycieMenaContract.Category.TABLENAME);
    }
}
