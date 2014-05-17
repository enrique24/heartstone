package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Abstracts the process of loading and saving all the game settings
 * @author Enrique Martín Arenal
 *
 */
public class GamePreferences {

	public static final String TAG = GamePreferences.class.getName();

	public static final GamePreferences instance = new GamePreferences();

	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public boolean showFpsCounter;
	private Preferences prefs;

	/**
	 *  singleton: prevent instantiation from other classes
	 */
	private GamePreferences() { 
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	public void load() {
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		volSound = MathUtils
				.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		volMusic = MathUtils
				.clamp(prefs.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
	}

	public void save() {
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.flush();
	}

}
