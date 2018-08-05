package sk.upjs.vma.kryciemena.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import sk.upjs.vma.kryciemena.database.KrycieMenaContract;
import sk.upjs.vma.kryciemena.managers.MusicManager;
import sk.upjs.vma.kryciemena.customAdapters.MySimpleCursorTreeAdapter;
import sk.upjs.vma.kryciemena.R;
import sk.upjs.vma.kryciemena.viewProviders.TextViewProvider;

public class WordDatabaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String LOG_TAG = getClass().getSimpleName().toString();

    private static final String[] WORD_PROJECTION = new String[] {
            KrycieMenaContract.Word._ID,
            KrycieMenaContract.Word.CATEGORY_ID,
            KrycieMenaContract.Word.WORD };

    private static final String[] CATEGORY_PROJECTION = new String[] {
            KrycieMenaContract.Category._ID,
            KrycieMenaContract.Category.CATEGORY };

    private static final int LOADER_ID = -1;
    MySimpleCursorTreeAdapter adapter;
    private boolean musicOn;
    private boolean soundsOn;

    private int category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_database2);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);

        final ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.wordsExpandableListView);

        adapter = new MySimpleCursorTreeAdapter(this,
                R.layout.category,
                R.layout.word,
                new String[] { KrycieMenaContract.Category.CATEGORY },
                new int[] { R.id.categoryTextView },
                new String[] { KrycieMenaContract.Word.WORD },
                new int[] { R.id.wordTextView }) {

            @Override
            public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                final View resultView = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
                ImageButton deleteButton = (ImageButton) resultView.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (soundsOn) {
                            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
                        }

                        final long id = adapter.getChildId(groupPosition, childPosition);
                        TextView textView = (TextView) resultView.findViewById(R.id.wordTextView);

                        new android.app.AlertDialog.Builder(WordDatabaseActivity.this)
                                .setTitle(R.string.delete)
                                .setView(TextViewProvider.getTextViewForAlertDialog(WordDatabaseActivity.this, getString(R.string.do_you_want_to_delete_word) + " " + textView.getText().toString()))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteWord(KrycieMenaContract.Word._ID, id);
//                                        int groupId = adapter.getPosToIdGroupMap().get(groupPosition);
//                                        Loader<Cursor> loader = getLoaderManager().getLoader(groupId);
//                                        if (loader != null && !loader.isReset()) {
//                                            getLoaderManager().restartLoader(groupId, null, WordDatabaseActivity.this);
//                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });
                return resultView;
            }

            @Override
            public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
                final View resultView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                final TextView textView = (TextView) resultView.findViewById(R.id.categoryTextView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (soundsOn) {
                            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
                        }
                        if (isExpanded) {
                            expandableListView.collapseGroup(groupPosition);
                        } else {
                            expandableListView.expandGroup(groupPosition, true);
                        }
                    }
                });

                ImageButton deleteButton = (ImageButton) resultView.findViewById(R.id.deleteCategoryButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (soundsOn) {
                            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
                        }

                        final long id = adapter.getGroupId(groupPosition);

                        new android.app.AlertDialog.Builder(WordDatabaseActivity.this)
                                .setTitle(R.string.delete)
                                .setView(TextViewProvider.getTextViewForAlertDialog(WordDatabaseActivity.this, getString(R.string.do_you_want_to_delete_category) + " " + textView.getText().toString()))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteCategory(id);
//                                        int groupId = adapter.getPosToIdGroupMap().get(groupPosition);
//                                        Loader<Cursor> loader = getLoaderManager().getLoader(groupId);
//                                        if (loader != null && !loader.isReset()) {
//                                            getLoaderManager().restartLoader(groupId, null, WordDatabaseActivity.this);
//                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });

                ImageButton addButton = (ImageButton) resultView.findViewById(R.id.addWordButton);
                addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (soundsOn) {
                            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
                        }

                        final long id = adapter.getGroupId(groupPosition);
                        final EditText editText = new EditText(WordDatabaseActivity.this);
                        new AlertDialog.Builder(WordDatabaseActivity.this)
                                .setTitle(R.string.add_new_word)
                                .setView(editText)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        saveWord(id, editText.getText().toString());
//                                Loader<Cursor> loader = getLoaderManager().getLoader((int) info.id);
//                                if (loader != null && !loader.isReset()) {
//                                    getLoaderManager().restartLoader(loader.getId(), null, WordDatabaseActivity.this);
//                                }
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                });

                return resultView;
            }

        };

        expandableListView.setAdapter(adapter);

        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_ID);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

//        registerForContextMenu(expandableListView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        if (id != LOADER_ID) {
            // child cursor
            Uri wordsUri = KrycieMenaContract.Word.CONTENT_URI;
            String selection = "("
                    + KrycieMenaContract.Word.CATEGORY_ID
                    + " = ? )";

            String[] selectionArgs = new String[] { String.valueOf(id) };

            String sortOrder = KrycieMenaContract.Word.WORD;

            cl = new CursorLoader(this, wordsUri, WORD_PROJECTION,
                    selection, selectionArgs, null);
        } else {
            // group cursor
            Uri categoryUri = KrycieMenaContract.Category.CONTENT_URI;
            String selection = "((" + KrycieMenaContract.Category.CATEGORY
                    + " NOTNULL) AND ("
                    + KrycieMenaContract.Category.CATEGORY + " != '' ))";

            cl = new CursorLoader(this, categoryUri, CATEGORY_PROJECTION,
                    selection, null, null);
        }

        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Setting the new cursor onLoadFinished. (Old cursor would be closed
        // automatically)
        int id = loader.getId();
        Log.d(LOG_TAG, "onLoadFinished() for loader_id " + id);
        if (id != LOADER_ID) {
            // child cursor
            if (!data.isClosed()) {
                Log.d(LOG_TAG, "data.getCount() " + data.getCount());

                HashMap<Integer, Integer> groupMap = adapter.getGroupMap();
                try {
                    int groupPos = groupMap.get(id);
                    Log.d(LOG_TAG, "onLoadFinished() for groupPos " + groupPos);
                    data.setNotificationUri(getContentResolver(), KrycieMenaContract.Word.CONTENT_URI);
                    adapter.setChildrenCursor(groupPos, data);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG,
                            "Adapter expired, try again on the next query: "
                                    + e.getMessage());
                }
            }
        } else {
            data.setNotificationUri(getContentResolver(), KrycieMenaContract.Category.CONTENT_URI);
            adapter.setGroupCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Called just before the cursor is about to be closed.
        int id = loader.getId();
        Log.d(LOG_TAG, "onLoaderReset() for loader_id " + id);
        if (id != LOADER_ID) {
            // child cursor
            try {
                adapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
                Log.w(LOG_TAG, "Adapter expired, try again on the next query: "
                        + e.getMessage());
            }
        } else {
            adapter.setGroupCursor(null);
        }
    }

    private boolean deleteWord(String column, long id) {
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(WordDatabaseActivity.this, R.string.delete_successful, Toast.LENGTH_LONG).show();
            }
        };
        Uri uri = Uri.withAppendedPath(KrycieMenaContract.Word.CONTENT_URI, String.valueOf(id));

        asyncQueryHandler.startDelete(0, null, uri, column + " = ?", new String[]{String.valueOf(id)});
        return true;
    }

    private boolean deleteCategory(long id) {
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(WordDatabaseActivity.this, R.string.delete_successful, Toast.LENGTH_LONG).show();
            }
        };
        Uri uri = Uri.withAppendedPath(KrycieMenaContract.Category.CONTENT_URI, String.valueOf(id));
        asyncQueryHandler.startDelete(0, null, uri, KrycieMenaContract.Category._ID + " = ?", new String[]{String.valueOf(id)});
        deleteWord(KrycieMenaContract.Word.CATEGORY_ID, id);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.word_database_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addCategory) {
            if (soundsOn) {
                MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
            }

            final EditText editText = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.new_category)
                    .setView(editText)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveCategory(editText.getText().toString());
//                            Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_ID);
//                            if (loader != null && !loader.isReset()) {
//                                getLoaderManager().restartLoader(LOADER_ID, null, WordDatabaseActivity.this);
//                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCategory(String category) {
        ContentValues values = new ContentValues();
        values.put(KrycieMenaContract.Category.CATEGORY, category);

        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Toast.makeText(WordDatabaseActivity.this, R.string.category_successfully_added, Toast.LENGTH_LONG).show();
            }
        };
        asyncQueryHandler.startInsert(0, null, KrycieMenaContract.Category.CONTENT_URI, values);
    }

    private void saveWord(long idCategory, String word) {
        ContentValues values = new ContentValues();
        values.put(KrycieMenaContract.Word.CATEGORY_ID, idCategory);
        values.put(KrycieMenaContract.Word.WORD, word);

        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Toast.makeText(WordDatabaseActivity.this, R.string.word_successfully_added, Toast.LENGTH_LONG).show();
            }
        };
        asyncQueryHandler.startInsert(0, null, KrycieMenaContract.Word.CONTENT_URI, values);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (soundsOn) {
            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
        }
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("WORD", "onStop: ");
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicOn) {
            Log.d("WORD", "onStart: ");
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }
}
