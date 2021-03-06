package sk.upjs.vma.kryciemena.managers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import sk.upjs.vma.kryciemena.R;


/**
 * This class is used to play sounds across activities, like the
 * background music in our program. Can be extended to play several
 * different sounds.
 * This code was heavily based on the code found here:
 * http://www.rbgrn.net/content/307-light-racer-20-days-61-64-completion
 *
 */
public class MusicManager {

    private static final String TAG = "MusicManager";
	public static final int MUSIC_PREVIOUS = -1;
	public static final int MUSIC_BACKGROUND = 0;
	public static final int MUSIC_GAMEPLAY = 1;

    public static final int SOUND_BUTTON = 2;
    public static final int SOUND_TIMER = 3;
    public static final int SOUND_APPLAUSE = 4;
    public static final int SOUND_LETS_PLAY = 5;


	private static HashMap<Integer, MediaPlayer> players = new HashMap<Integer, MediaPlayer>();
	private static int currentMusic = -1;
	private static int previousMusic = -1;

	public static float getMusicVolume(Context context) {
		//could be updated based on user preferences
		return 0.3f;
	}

	public static void start(Context context, int music) {
		start(context, music, false);
	}

	public static void start(Context context, int music, boolean force) {
		if (!force && currentMusic > -1 || currentMusic == music) {
			// already playing some music and not forced to change
			// or already playing this particular music
			return;
		}
		if (music == MUSIC_PREVIOUS) {
			Log.d(TAG, "START: Using previous music [" + previousMusic + "]");
			music = previousMusic;
		}

		if (currentMusic != -1) {
			previousMusic = currentMusic;
			Log.d(TAG, "START: Previous music was [" + previousMusic + "]");
			// playing some other music, pause it and change
			pause();
		}

		currentMusic = music;
		Log.d(TAG, "START: Current music is now [" + currentMusic + "]");
		MediaPlayer mp = players.get(music);
		if (mp != null) {
			if (!mp.isPlaying()) {
				mp.start();
			}
		} else {
			if (music == MUSIC_BACKGROUND) {
				mp = MediaPlayer.create(context, R.raw.mission_impossible_theme);
			} else if(music == MUSIC_GAMEPLAY){
				mp = MediaPlayer.create(context, R.raw.the_pink_panther_theme);
			}else {
				Log.e(TAG, "unsupported music number - " + music);
				return;
			}

			players.put(music, mp);
			float volume = getMusicVolume(context);
			Log.d(TAG, "Setting music volume to " + volume);
			try {
			    mp.setVolume(volume, volume);
				mp.setLooping(true);
				mp.start();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public static void pause() {
		Collection<MediaPlayer> mps = players.values();
		for (MediaPlayer p : mps) {
			if (p.isPlaying()) {
				p.pause();
			}
		}
		// previousMusic should always be something valid
		if (currentMusic != -1) {
			previousMusic = currentMusic;
			Log.d(TAG, "PAUSE: Previous music was [" + previousMusic + "]");
		}
		currentMusic = -1;
		Log.d(TAG, "PAUSE: Current music is now [" + currentMusic + "]");
	}

	public static void updateVolumeFromPrefs(Context context) {
		try {
			float volume = getMusicVolume(context);
			Log.d(TAG, "Setting music volume to " + volume);
			Collection<MediaPlayer> mps = players.values();
			for (MediaPlayer p : mps) {
				p.setVolume(volume, volume);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public static void release() {
		Log.d(TAG, "Releasing media players");
		Collection<MediaPlayer> mps = players.values();
		for (MediaPlayer mp : mps) {
			try {
				if (mp != null) {
					if (mp.isPlaying()) {
						mp.stop();
					}
					mp.release();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		mps.clear();
		if (currentMusic != -1) {
			previousMusic = currentMusic;
			Log.d(TAG, "RELEASE: Previous music was [" + previousMusic + "]");
		}
		currentMusic = -1;
		Log.d(TAG, "RELEASE: Current music is now [" + currentMusic + "]");
	}


}