package sk.upjs.vma.kryciemena;

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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WordDatabaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 4;
    SimpleCursorAdapter adapter;
    private boolean musicOn;
    private boolean soundsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_database);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);

        ListView listView = findViewById(R.id.wordsListView);

        String[] from = {KrycieMenaContract.Word.WORD};
        int[] to = {R.id.wordTextView};

        adapter = new SimpleCursorAdapter(this, R.layout.word, null, from, to, 0) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                final View row = super.getView(position, convertView, parent);

                ImageButton deleteButton = (ImageButton) row.findViewById(R.id.deleteButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (soundsOn) {
                            MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
                        }
                        final long id = adapter.getItemId(position);

                        TextView textView = (TextView) row.findViewById(R.id.wordTextView);

                        new android.app.AlertDialog.Builder(WordDatabaseActivity.this)
                                .setTitle(R.string.delete)
                                .setView(TextViewProvider.getTextViewForAlertDialog(WordDatabaseActivity.this, getString(R.string.do_you_want_to_delete_word) + " " + textView.getText().toString()))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteWord(id);
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

            return row;
            }
        };
        listView.setAdapter(adapter);

        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID) {
            throw new IllegalStateException("Invalid Loader with ID: " + i);
        }
        CursorLoader cursorLoader = new CursorLoader(this);
        cursorLoader.setUri(KrycieMenaContract.Word.CONTENT_URI);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        cursor.setNotificationUri(getContentResolver(), KrycieMenaContract.Word.CONTENT_URI);   // refresh view dat po zmene...
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private boolean deleteWord(long id) {
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Toast.makeText(WordDatabaseActivity.this, R.string.delete_successful, Toast.LENGTH_LONG).show();
            }
        };
        Uri uri = Uri.withAppendedPath(KrycieMenaContract.Word.CONTENT_URI, String.valueOf(id));
        asyncQueryHandler.startDelete(0, null, uri, null, null);
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
        if (item.getItemId() == R.id.addWord) {

            if (soundsOn) {
                MediaPlayer.create(WordDatabaseActivity.this, R.raw.button_click).start();
            }

            final EditText editText = new EditText(this);
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.add_new_word)
                    .setView(editText)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveWord(editText.getText().toString());
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

    private void saveWord(String word) {
        ContentValues values = new ContentValues();
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
