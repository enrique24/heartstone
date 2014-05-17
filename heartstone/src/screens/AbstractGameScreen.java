

package screens;

import game.Assets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Abstract class used to inherit all the functionalities of the Screen interface
 * @author Enrique Martín Arenal
 *
 */
public abstract class AbstractGameScreen implements Screen {

	protected Game game;

	public AbstractGameScreen (Game game) {
		this.game = game;
	}

	/**
	 * Renders the screen
	 */
	public abstract void render (float deltaTime);

	/**
	 * Resizes the screen
	 */
	public abstract void resize (int width, int height);

	/**
	 * OnCreate method
	 */
	public abstract void show ();

	/**
	 * Dispose method
	 */
	public abstract void hide ();

	public abstract void pause ();

	public void resume () {
		Assets.instance.init(new AssetManager());
	}

	public void dispose () {
		Assets.instance.dispose();
	}

}
