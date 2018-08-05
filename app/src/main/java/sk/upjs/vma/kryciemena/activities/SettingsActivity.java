package sk.upjs.vma.kryciemena.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import sk.upjs.vma.kryciemena.database.Category;
import sk.upjs.vma.kryciemena.managers.MusicManager;
import sk.upjs.vma.kryciemena.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MainMenuActivity mainMenuActivity;
    public Set<String> categoryDefault = new HashSet<>();
    public static final String TIMER_DEFAULT = String.valueOf(60000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
//
//        PreferenceScreen pScreen = getPreferenceManager().createPreferenceScreen(this);
//
//        if (pScreen == null) {
//            Log.d("PREF", "onCreate: SCREEN JE NULLL");
//        }
//        PreferenceCategory preferenceCategory1 = new PreferenceCategory(this);
//        preferenceCategory1.setTitle(R.string.pref_category_general);
//        if (preferenceCategory1 == null) {
//            Log.d("PREF", "onCreate: CATEG1 JE NULLL");
//        }
//
//        CheckBoxPreference checkBoxPreference1 = new CheckBoxPreference(this);
//        checkBoxPreference1.setKey(getString(R.string.pref_music_key));
//        checkBoxPreference1.setTitle(R.string.music_title);
//        checkBoxPreference1.setSummary(R.string.music_summary);
//        checkBoxPreference1.setDefaultValue(true);
//
//        if (checkBoxPreference1 == null) {
//            Log.d("PREF", "onCreate: CHECK1 JE NULLL");
//        }
//
//        preferenceCategory1.addPreference(checkBoxPreference1);
//
//        CheckBoxPreference checkBoxPreference2 = new CheckBoxPreference(this);
//        checkBoxPreference2.setKey(getString(R.string.pref_sounds));
//        checkBoxPreference2.setTitle(R.string.sounds);
//        checkBoxPreference2.setSummary(R.string.enable_sound_effects);
//        checkBoxPreference2.setDefaultValue(true);
//
//        preferenceCategory1.addPreference(checkBoxPreference2);
//
//        pScreen.addPreference(preferenceCategory1);
//
//        PreferenceCategory preferenceCategory2 = new PreferenceCategory(this);
//        preferenceCategory2.setTitle(R.string.pref_category_game);
//
//        ListPreference listPreference1 = new ListPreference(this);
//        listPreference1.setKey(getString(R.string.pref_timer_key));
//        listPreference1.setTitle(R.string.timer_title);
//        listPreference1.setSummary(R.string.timer_summary);
//        listPreference1.setEntries(R.array.timer_names);
//        listPreference1.setEntryValues(R.array.timer_values);
//        listPreference1.setDefaultValue(TIMER_DEFAULT);
//
//        preferenceCategory2.addPreference(listPreference1);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("game_settings_key");

        if (preferenceCategory == null) {
            Log.d("PREF", "onCreate: NUUUUUUUUUUUULLLL");
        } else {
            Log.d("PREF", "onCreate: OOOOOOKKKKKKK");
            Log.d("PREF", "onCreate: " + preferenceCategory.getKey());
        }



        ArrayList<Category> categories = (ArrayList<Category>) getIntent().getExtras().get(MainMenuActivity.CATEGORIES);

        MultiSelectListPreference listPreference2 = new MultiSelectListPreference(this);
        listPreference2.setTitle(R.string.category);
        listPreference2.setSummary(getString(R.string.choose_category));
        listPreference2.setKey(getString(R.string.pref_category));
        String[] entries = new String[categories.size()];
        String[] entryValues = new String[categories.size()];
        int i = 0;
        for (Category category: categories) {
            entries[i] = category.getCategory();
            entryValues[i] = String.valueOf(category.getId());
            categoryDefault.add(String.valueOf(category.getId()));
            i++;
        }
        Log.d("ENTRIES", "onCreate: " + Arrays.toString(entries));
        Log.d("ENTRY_VALUES", "onCreate: " + Arrays.toString(entryValues));
        listPreference2.setDefaultValue(categoryDefault);
        listPreference2.setEntries(entries);
        listPreference2.setEntryValues(entryValues);

        preferenceCategory.addPreference(listPreference2);

//        pScreen.addPreference(preferenceCategory2);
//        setPreferenceScreen(pScreen);

//        PreferenceManager.setDefaultValues(this, R.xml.activity_settings,
//                false);
//        initSummary(this.getPreferenceScreen());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
        if (key.equals(getString(R.string.pref_music_key))) {
            boolean musicOn = sharedPreferences.getBoolean(key, true);
            if (musicOn) {
                MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
            } else {
                MusicManager.pause();
            }
        }
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
//        if (p instanceof MultiSelectListPreference) {
//            EditTextPreference editTextPref = (EditTextPreference) p;
//            p.setSummary(editTextPref.getText());
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicOn = preferences.getBoolean("prefMusic", true);
        if (musicOn) {
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }

}
