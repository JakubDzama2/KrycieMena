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
import java.util.HashMap;
import java.util.List;

import sk.upjs.vma.kryciemena.gameLogic.Game;

public class MainMenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    public static final String GAME = "game";
    private List<String> words;
    private boolean musicOn;
    private boolean soundsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);

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
        startActivity(intent);
    }

    public void wordDatabaseOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

       Intent intent = new Intent(this, WordDatabaseActivity.class);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID) {
            throw new IllegalStateException("Invalid Loader ID: " + i);
        }
        CursorLoader cursorLoader = new CursorLoader(this);
        cursorLoader.setUri(KrycieMenaContract.Word.CONTENT_URI);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.setNotificationUri(getContentResolver(), KrycieMenaContract.Word.CONTENT_URI);

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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        words = null;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);

        if (musicOn) {
            Log.d("MAIN", "onStart: ");
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }
}
