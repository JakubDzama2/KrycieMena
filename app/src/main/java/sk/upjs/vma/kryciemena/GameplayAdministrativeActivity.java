package sk.upjs.vma.kryciemena;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sk.upjs.vma.kryciemena.gameLogic.Card;
import sk.upjs.vma.kryciemena.gameLogic.Game;

public class GameplayAdministrativeActivity extends AppCompatActivity {

    public static final String INDICIA = "indicia";
    public static final String WORD_COUNT = "word_count";

    private Game game;
    private EditText editText;
    private Spinner wordCountSpinner;
    private boolean musicOn;
    private boolean soundsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay_administrative);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);

        game = (Game) getIntent().getExtras().get(MainMenuActivity.GAME);

        GridView gridView = findViewById(R.id.administrativeCardsGridView);
        editText = findViewById(R.id.indiciaEditText);
        wordCountSpinner = findViewById(R.id.wordCountSpinner);
        Button endTurnButton = findViewById(R.id.administrativeEndTurnButton);
        if (game.getTeamTurn() == 0) {
            endTurnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
//            wordCountSpinner.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
        } else {
            endTurnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
//            wordCountSpinner.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
        }

        Integer[] numbers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        ArrayAdapter<Integer> integerArrayAdapter = new ArrayAdapter<Integer>
                (this, R.layout.spinner_item, numbers);
        wordCountSpinner.setAdapter(integerArrayAdapter);

        String[] from = {KrycieMenaContract.Word.WORD};
        int[] to = {R.id.cardTextView};
        SimpleAdapter adapter = new SimpleAdapter(this, getDataForAdapter(), R.layout.card, from, to);
        adapter.setViewBinder(new AdministrativeViewBinder());
        gridView.setAdapter(adapter);
    }

    private List<HashMap<String, Card>> getDataForAdapter() {
        List<HashMap<String, Card>> result = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            HashMap<String, Card> map = new HashMap<>();
            map.put(KrycieMenaContract.Word.WORD, game.getCard(i));
            result.add(map);
        }
        return result;
    }

    public void administrativeEndTurnButtonClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.end_turn)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.do_you_want_to_end_your_turn)))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    switchToGameplayCooperativeActivity();
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

    private void switchToGameplayCooperativeActivity() {

        Intent intent = new Intent(this, GameplayCooperativeActivity.class);
        intent.putExtra(MainMenuActivity.GAME, game);

        String indicia = editText.getText().toString();
        intent.putExtra(INDICIA, indicia);

        String spinnerValue = wordCountSpinner.getSelectedItem().toString();
        int wordCount = Integer.parseInt(spinnerValue);
        intent.putExtra(WORD_COUNT, wordCount);

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.end_game)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.do_you_want_to_end_the_game)))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
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

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("ADM", "onPause: ");
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicOn) {
            Log.d("ADM", "onResume: ");
            MusicManager.start(this, MusicManager.MUSIC_GAMEPLAY);
        }
    }
}
