package sk.upjs.vma.kryciemena.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import sk.upjs.vma.kryciemena.managers.MusicManager;
import sk.upjs.vma.kryciemena.R;

public class AboutActivity extends AppCompatActivity {

    private static final String URL_PDF = "http://www.mindok.cz/userfiles/files/pravidla/8595558302239_50.pdf";
    private boolean musicOn;
    private boolean soundsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);
    }

    public void rulesButtonOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PDF));
        startActivity(browserIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicOn) {
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }
}
