package sk.upjs.vma.kryciemena;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sk.upjs.vma.kryciemena.gameLogic.Card;
import sk.upjs.vma.kryciemena.gameLogic.Game;

public class GameplayCooperativeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String GAME = "game";
    private static final String MILLI_LEFT = "millileft";
    private static final String GUESSED_WORD_ORDER = "gussedWordOrder";
    private static final String TIMER_ON = "timerOn";
    private static final String SWITCH_GAMEPLAY = "switch";

    private Game game;
    private GridView gridView;
    private TextView wordCountTextView;
    private TextView timer;
    private CountDownTimer countDownTimer;
    private int wordCount;
    private int guessedWordOrder;
    private long milliLeft;
    private long timerLength;
    private boolean timerOn;
    private MediaPlayer endGameMediaPlayer;
    private MediaPlayer timerMediaPlayer;
    private boolean musicOn;
    private boolean soundsOn;

    private boolean switch_gameplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay_cooperative);

        gridView = findViewById(R.id.cooperativeCardsGridView);
        TextView indiciaTextView = findViewById(R.id.cooperativeIndiciaTextView);
        wordCountTextView = findViewById(R.id.cooperativeWordCountTextView);
        timer = findViewById(R.id.countdownTimer);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String time = preferences.getString("prefTimer", SettingsActivity.TIMER_DEFAULT);
        timerLength = Long.parseLong(time);

        musicOn = preferences.getBoolean(getString(R.string.pref_music_key), true);
        soundsOn = preferences.getBoolean(getString(R.string.pref_sounds), true);
        if (soundsOn) {
            endGameMediaPlayer = MediaPlayer.create(this, R.raw.applause);
            timerMediaPlayer = MediaPlayer.create(this, R.raw.timer);
        }
        timer.setText(getFormattedTimeForTimer(timerLength));
        Button endTurnButton = findViewById(R.id.cooperativeEndTurnButton);

        timerOn = false;
        guessedWordOrder = 0;
        game = (Game) getIntent().getExtras().get(MainMenuActivity.GAME);

        if (savedInstanceState != null) {
//            Log.d("ON CREATE", "onCreate: ON CREATE BUNDLE");
            game = (Game) savedInstanceState.get(GAME);
            guessedWordOrder = savedInstanceState.getInt(GUESSED_WORD_ORDER);
            milliLeft = savedInstanceState.getLong(MILLI_LEFT);
            timerOn = savedInstanceState.getBoolean(TIMER_ON);
            switch_gameplay = savedInstanceState.getBoolean(SWITCH_GAMEPLAY);

//            Log.d("ON CREATE", "onCreate: milli: " + milliLeft);
//            Log.d("ON CREATE", "onCreate: wordOrder: " + guessedWordOrder);
//            Log.d("ON CREATE", "onCreate: timerON: " + timerOn);
        }

        if (game.getTeamTurn() == 0) {
            endTurnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
//            indiciaTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
//            wordCountTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
            timer.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team0));
        } else {
            endTurnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
//            indiciaTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
//            wordCountTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
            timer.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_team1));
        }

        resetGridViewAdapter(new CooperativeViewBinder(), gridView);
        gridView.setOnItemClickListener(this);

        String indicia = getIntent().getExtras().getString(GameplayAdministrativeActivity.INDICIA);
        indiciaTextView.setText(indicia);

        wordCount = getIntent().getExtras().getInt(GameplayAdministrativeActivity.WORD_COUNT);
        wordCountTextView.setText(guessedWordOrder + "/" + wordCount);
    }

    private void resetGridViewAdapter(SimpleAdapter.ViewBinder viewBinder, GridView localGridView) {
        String[] from = {KrycieMenaContract.Word.WORD};
        int[] to = {R.id.cardTextView};
        SimpleAdapter adapter = new SimpleAdapter(this, getDataForAdapter(), R.layout.card, from, to);
        adapter.setViewBinder(viewBinder);
        localGridView.setAdapter(adapter);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        Log.d("SAVE BUNDLE", "onSaveInstanceState: SAVE BUNDLE");
//        Log.d("SAVE BUNDLE", "onSaveInstanceState: milli: " + milliLeft);
//        Log.d("SAVE BUNDLE", "onSaveInstanceState: wordOrder: " + guessedWordOrder);
//        Log.d("SAVE BUNDLE", "onSaveInstanceState: timerON:  " + countDownTimer.toString() + ", " + timerOn);

        outState.putSerializable(GAME, game);
        outState.putSerializable(MILLI_LEFT, milliLeft);
        outState.putSerializable(GUESSED_WORD_ORDER, guessedWordOrder);
        outState.putSerializable(TIMER_ON, timerOn);
        outState.putSerializable(SWITCH_GAMEPLAY, switch_gameplay);
    }

    public void cooperativeEndTurnButtonOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.end_turn)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.do_you_want_to_end_your_turn)))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchToGameplayAdministrativeActivity();
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
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }

        if (!game.checkTheCard(position)) {
            Toast.makeText(this, R.string.you_have_already_guessed_this_word, Toast.LENGTH_SHORT).show();
            return;
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.guess)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.do_you_want_to_guess_the_word) + " " + game.getCards()[position].getWord() + "?"))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cardClickHandle(position);
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

    private void cardClickHandle(int position) {

        if (game.checkTheKiller(position)) {
            // END THE GAME
            game.cardClick(position);
            Toast.makeText(this, R.string.found_killer, Toast.LENGTH_SHORT).show();

            String title;
            if (game.getTeamTurn() == 0) {
                title = getString(R.string.blue_team_won);
            } else {
                title = getString(R.string.red_team_won);
            }

            endOfGame(title);
            return;
        }

        if (game.cardClick(position)) {
            if (game.checkTheWinnerOfGame()) {
                // END THE GAME
                String title;
                if (game.getTeamTurn() == 0) {
                    title = getString(R.string.red_team_won);
                } else {
                    title = getString(R.string.blue_team_won);
                }

                endOfGame(title);
                return;
            }

            if (soundsOn) {
                MediaPlayer.create(this, R.raw.correct_answer).start();
            }

            guessedWordOrder++;
            wordCountTextView.setText(guessedWordOrder + "/" + wordCount);
            if (guessedWordOrder == wordCount) {
                Toast.makeText(this, getString(R.string.good_job_you_guessed_all) + " " + wordCount + " " + getString(R.string.words_right), Toast.LENGTH_LONG).show();
                switchToGameplayAdministrativeActivity();
            } else {
                Toast.makeText(this, R.string.guessed_right, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (soundsOn) {
                MediaPlayer.create(this, R.raw.cow_wrong_answer).start();
            }
            Toast.makeText(this, R.string.guessed_wrong, Toast.LENGTH_LONG).show();
            switchToGameplayAdministrativeActivity();
        }

        // RESET ADAPTER
        resetGridViewAdapter(new CooperativeViewBinder(), gridView);
    }

    private void switchToGameplayAdministrativeActivity() {
        if (timerOn) {
            timerPause();
            if (soundsOn && timerMediaPlayer.isPlaying()) {
                timerMediaPlayer.pause();
            }
        }

        switch_gameplay = true;

        String title;
        if (game.getTeamTurn() == 1) {
            title = getString(R.string.red_team_turn);
        } else {
            title = getString(R.string.blue_team_turn);
        }
        new android.app.AlertDialog.Builder(GameplayCooperativeActivity.this)
                .setTitle(title)
                .setView(TextViewProvider.getTextViewForAlertDialog(this, getString(R.string.pass_the_device_to_your_opponent_team_super_agent)))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(GameplayCooperativeActivity.this, GameplayAdministrativeActivity.class);
                        game.changeTeamTurn();
                        intent.putExtra(MainMenuActivity.GAME, game);
                        startActivity(intent);
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Intent intent = new Intent(GameplayCooperativeActivity.this, GameplayAdministrativeActivity.class);
                        game.changeTeamTurn();
                        intent.putExtra(MainMenuActivity.GAME, game);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void endOfGame(String title) {
        if (timerOn) {
            timerPause();
            if (soundsOn && milliLeft < 11000) {
                timerMediaPlayer.pause();
            }
        }
        if (soundsOn) {
            endGameMediaPlayer.start();
        }

        WindowManager wm = (WindowManager) getSystemService(this.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels - 50;

        GridView alertDialogGridView = new GridView(this);
        alertDialogGridView.setNumColumns(5);
        alertDialogGridView.setColumnWidth(30);
        alertDialogGridView.setHorizontalSpacing(2);
        alertDialogGridView.setVerticalSpacing(2);
        resetGridViewAdapter(new AdministrativeViewBinder(), alertDialogGridView);
        new android.app.AlertDialog.Builder(this)

                .setTitle(title)
                .setView(alertDialogGridView).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .show()
                .getWindow().setLayout(width, height);


    }

    public void timerOnClick(View view) {
        if (soundsOn) {
            MediaPlayer.create(this, R.raw.button_click).start();
        }
        if (timerOn) {
            Toast.makeText(this, R.string.timer_is_already_on, Toast.LENGTH_SHORT).show();
            return;
        }
        timerStart(timerLength);

        Toast.makeText(this, R.string.timer_is_on, Toast.LENGTH_SHORT).show();
    }

    private String getFormattedTimeForTimer(long millis) {
        int minutes = (int) millis / (60 * 1000);
        int seconds = (int) millis % (60 * 1000);
        seconds = seconds / 1000;
        String sec;
        if (seconds / 10 == 0) {
            sec = "0" + seconds;
        } else {
            sec = String.valueOf(seconds);
        }
        return minutes + ":" + sec;
    }

    private void timerStart(long miilis) {
        timerOn = true;
        countDownTimer = new CountDownTimer(miilis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (soundsOn) {
                    if (millisUntilFinished < 11000 && !timerMediaPlayer.isPlaying()) {
                        timerMediaPlayer.start();
                    }
                }
                milliLeft = millisUntilFinished;
                timer.setText(getFormattedTimeForTimer(millisUntilFinished));
//                Log.d("TIMER " + this.toString(), "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (soundsOn) {
                    if (timerMediaPlayer.isPlaying()) {
                        timerMediaPlayer.pause();
                        timerMediaPlayer.release();
                    }
                }
                timerOn = false;
                Toast.makeText(GameplayCooperativeActivity.this, R.string.time_out, Toast.LENGTH_LONG).show();
                switchToGameplayAdministrativeActivity();
            }
        }.start();
    }

    private void timerPause() {
        countDownTimer.cancel();
//        Log.d("TIMER " + countDownTimer.toString(), "timerPause: millis: " + milliLeft);
    }

    private void timerResume() {
        timerStart(milliLeft);
//        Log.d("TIMER " + countDownTimer.toString(), "timerResume: millis: " + milliLeft);
    }

    @Override
    protected void onPause() {
//        Log.d("PAUSE", "onPause: PAUSE");
        super.onPause();

        if (timerOn) {
            timerPause();
        }

        Log.d("COOP", "onPause: ");
        MusicManager.pause();

        if (soundsOn) {
            if (endGameMediaPlayer.isPlaying()) {
                endGameMediaPlayer.pause();
                endGameMediaPlayer.release();
            }
            if (milliLeft < 11000) {
                timerMediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d("RESUME", "onResume: RESUME");
        super.onResume();

        if (timerOn) {
            timerResume();
        }

        if (musicOn) {
            Log.d("COOP", "onResume: ");
            MusicManager.start(this, MusicManager.MUSIC_GAMEPLAY);
        }

        if (soundsOn) {
            endGameMediaPlayer = MediaPlayer.create(this, R.raw.applause);
            timerMediaPlayer = MediaPlayer.create(this, R.raw.timer);
        }

        if (switch_gameplay) {
            switchToGameplayAdministrativeActivity();
        }
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
}
