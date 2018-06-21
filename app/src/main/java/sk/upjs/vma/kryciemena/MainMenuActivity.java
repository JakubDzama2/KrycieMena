package sk.upjs.vma.kryciemena;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sk.upjs.vma.kryciemena.gameLogic.Game;

public class MainMenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_WORD_ID = 3;
    private static final int LOADER_CATEGORY_ID = 4;
    public static final String GAME = "game";
    public static final String CATEGORIES = "categories";
    private List<String> words;
    private ArrayList<Category> categories;
    private boolean musicOn;
    private boolean soundsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getLoaderManager().initLoader(LOADER_WORD_ID, Bundle.EMPTY, this);
        getLoaderManager().initLoader(LOADER_CATEGORY_ID, Bundle.EMPTY, this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);
    }

    public void newGameOnClick(View view) {

        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        if (words.size() < 25) {
            Toast.makeText(this, R.string.low_number_of_words_in_database, Toast.LENGTH_LONG).show();
            return;
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.red_team_turn)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.pass_the_device_to_red_team_super_agent)))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainMenuActivity.this, GameplayAdministrativeActivity.class);
                        Game game = new Game();
                        game.generateRandomGamePlan(words);
                        intent.putExtra(GAME, game);
                        startActivity(intent);
                        MusicManager.release();
                    }
                }).show();
    }

    public void settingsOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(CATEGORIES, categories);
        startActivity(intent);
    }

    public void wordDatabaseOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        Intent intent = new Intent(this, WordDatabaseActivity2.class);
        startActivity(intent);
    }

    public void aboutOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_WORD_ID) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String category = preferences.getString(getString(R.string.pref_category), SettingsActivity.CATEGORY_DEFAULT);
            Log.d("CATEGORY", "onCreateLoader: " + category);
            if (category.equals(SettingsActivity.CATEGORY_DEFAULT)) {
                CursorLoader cursorLoader = new CursorLoader(this);
                cursorLoader.setUri(KrycieMenaContract.Word.CONTENT_URI);
                return cursorLoader;
            } else {
                CursorLoader cursorLoader = new CursorLoader(
                        this,
                        KrycieMenaContract.Word.CONTENT_URI,
                        null,
                        KrycieMenaContract.Word.CATEGORY_ID + " = ?",
                        new String[]{category},
                        KrycieMenaContract.Word.WORD);

                return cursorLoader;
            }
        }
        if (id == LOADER_CATEGORY_ID) {
            CursorLoader cursorLoader = new CursorLoader(this);
            cursorLoader.setUri(KrycieMenaContract.Category.CONTENT_URI);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_WORD_ID) {
            words = new ArrayList<>();
            while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndex(KrycieMenaContract.Word._ID));
                String word = cursor.getString(cursor.getColumnIndex(KrycieMenaContract.Word.WORD));
                words.add(word);
            }

            for (String word : words) {
                Log.d("CLICK", "newGameOnClick: " + word);
//            Toast.makeText(this, word, Toast.LENGTH_LONG).show();
            }
        }
        if (loader.getId() == LOADER_CATEGORY_ID) {
            categories = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(KrycieMenaContract.Category._ID));
                String category = cursor.getString(cursor.getColumnIndex(KrycieMenaContract.Category.CATEGORY));
                categories.add(new Category(id, category));
                Log.d("CATEG", "onLoadFinished: id: " + id + "category: " + category);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_WORD_ID) {
            words = null;
        } else {
            categories = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MAIN", "onStop: ");
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(LOADER_WORD_ID, Bundle.EMPTY, this);
        getLoaderManager().restartLoader(LOADER_CATEGORY_ID, Bundle.EMPTY, this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);

        if (musicOn) {
            Log.d("MAIN", "onStart: ");
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }
}
