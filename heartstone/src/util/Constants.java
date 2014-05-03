package util;

import com.badlogic.gdx.math.Vector2;

public class Constants {
	// Visible game world is 16 meters wide
	public static final float VIEWPORT_WIDTH = 16.0f;
	// Visible game world is 12 meters tall
	public static final float VIEWPORT_HEIGHT = 12.0f;
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	// Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.pack";
	// Cards width
	public static final float CARD_WIDTH = 2.8f;
	// Cards height
	public static final float CARD_HEIGHT = 4.1f;
	// Player hit points at the start of a game
	public static final int HIT_POINTS = 30;
	// Card hit points number width
	public static final float CARD_HIT_NUMBER_WIDTH = 0.529f;
	// Card hit points number height
	public static final float CARD_HIT_NUMBER_HEIGHT = 0.6613f;
	// Card hit points number x, relative to the card x
	public static final float CARD_HIT_NUMBER_X = 2.05f;
	// Card hit points number x, relative to the card y
	public static final float CARD_HIT_NUMBER_Y = 1.2f;
	// Card positions on screen
	public static final Vector2[] CARD_POSITION_HAND = {
			new Vector2(-8.8f, -6), new Vector2(-5.9f, -6),
			new Vector2(-3.0f, -6), new Vector2(-0.2f, -6),
			new Vector2(2.6f, -6) };
	public static final Vector2[] CARD_POSITION_TABLE = {
			new Vector2(-8.8f, -1.9f), new Vector2(-5.9f, -1.9f),
			new Vector2(-3.0f, -1.9f), new Vector2(-0.2f, -1.9f),
			new Vector2(2.6f, -1.9f) };
	public static final Vector2[] CARD_POSITION_OPPONENT = {
			new Vector2(-8.8f, 2.2f), new Vector2(-5.9f, 2.2f),
			new Vector2(-3.0f, 2.2f), new Vector2(-0.2f, 2.2f),
			new Vector2(2.6f, 2.2f) };
	// Player and enemy 'positions' on screen
	public static final Vector2 PLAYER_BOUNDS = new Vector2(5.4f, -6);
	public static final Vector2 ENEMY_PLAYER_BOUNDS = new Vector2(5.4f, 2.2f);

	// Game preferences file
		public static final String PREFERENCES = "hstone.prefs";
		public static final String TEXTURE_ATLAS_UI = "images/canyonbunny-ui.pack";
		public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";

		// Location of description file for skins
		public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
		public static final String SKIN_HS_UI = "images/canyonbunny-ui.json";

}
