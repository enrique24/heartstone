package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
/**
 * Centralized point of control over the game's audio playback.
 * @author Enrique Martín Arenal
 *
 */
public class AudioManager {

	public static final AudioManager instance = new AudioManager();

	private Music playingMusic;

	/**
	 *  singleton: prevent instantiation from other classes
	 */
	private AudioManager() {
	}
	/**
	 * Plays a sound taking into account the game preferences
	 * @param sound the sound that is going to be played
	 */
	public void play(Sound sound) {
		play(sound, 1);
	}
	/**
	 * Plays a sound taking into account the game preferences
	 * @param sound the sound that is going to be played
	 * @param volume the volume of the sound
	 */
	public void play(Sound sound, float volume) {
		play(sound, volume, 1);
	}
	/**
	 * Plays a sound taking into account the game preferences
	 * @param sound the sound that is going to be played
	 * @param volume the volume of the sound
	 * @param pitch the pitch of the sound
	 */
	public void play(Sound sound, float volume, float pitch) {
		play(sound, volume, pitch, 0);
	}
	/**
	 * Plays a sound taking into account the game preferences
	 * @param sound the sound that is going to be played
	 * @param volume the volume of the sound
	 * @param pitch the pitch of the sound
	 * @param pan Negative pan values will play the sound only on the left audio channel
	 * whereas positive pan values will achieve the opposite.
	 */
	public void play(Sound sound, float volume, float pitch, float pan) {
		if (!GamePreferences.instance.sound)
			return;
		sound.play(GamePreferences.instance.volSound * volume, pitch, pan);
	}
	/**
	 * Plays a music taking into account the game preferences
	 * @param music
	 */
	public void play(Music music) {
		playingMusic = music;
		if (GamePreferences.instance.music) {
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	public void stopMusic() {
		if (playingMusic != null)
			playingMusic.stop();
	}

	public Music getPlayingMusic() {
		return playingMusic;
	}

	/**
	 * Stops/Starts the music when the settings are updated
	 */
	public void onSettingsUpdated() {
		if (playingMusic == null)
			return;
		playingMusic.setVolume(GamePreferences.instance.volMusic);
		if (GamePreferences.instance.music) {
			if (!playingMusic.isPlaying())
				playingMusic.play();
		} else {
			playingMusic.pause();
		}
	}

}
