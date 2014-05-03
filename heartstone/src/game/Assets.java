package game;

import util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	public AssetsCards carta;
	public AssetCardHitPoints hitPoint;
	public AssetFonts fonts;

	public AssetSounds sounds;
	public AssetMusic music;

	// singleton: prevent instantiation from other classes
	private Assets() {
		assetManager = new AssetManager();
		init(assetManager);
	}

	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures())
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// create game resource objects
		carta = new AssetsCards(atlas);
		hitPoint = new AssetCardHitPoints(atlas);
		fonts = new AssetFonts();
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		// Bitmap fonts must be disposed manually
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
				(Exception) throwable);

	}

	public class AssetsCards {
		TextureAtlas atlas;

		public AssetsCards(TextureAtlas atlas) {
			this.atlas = atlas;
		}

		// Get the texture associated with a card name
		public AtlasRegion getCardRegion(String cardName) {
			return atlas.findRegion(cardName);
		}
	}

	public class AssetCardHitPoints {

		TextureAtlas atlas;

		public AssetCardHitPoints(TextureAtlas atlas) {
			this.atlas = atlas;

		}

		// Get the texture associated with a number hit point
		public AtlasRegion getNumberRegion(int number) {
			return atlas.findRegion(String.valueOf(number));
		}
	}

	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial15white.fnt"), true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial15white.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial15white.fnt"), true);
			// set font sizes
			defaultSmall.setScale(0.75f);
			defaultNormal.setScale(1.0f);
			defaultBig.setScale(1.5f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public class AssetSounds {
		public final Sound attack;
		public final Sound selectCard;
		public final Sound win;
		public final Sound dead_guy;
		public final Sound cant_move_card;
		public final Sound new_card;
		public final Sound pass_turn;
		public final Sound loose;
		public final Sound get_turn;

		public AssetSounds(AssetManager am) {
			// attack = am.get("sounds/ataque.wav", Sound.class);
			attack = Gdx.audio
					.newSound(Gdx.files.internal("sounds/ataque.wav"));
			selectCard = Gdx.audio.newSound(Gdx.files
					.internal("sounds/elegir_carta.wav"));
			win = Gdx.audio.newSound(Gdx.files.internal("sounds/ganar.wav"));
			dead_guy = Gdx.audio.newSound(Gdx.files
					.internal("sounds/muerto_tio.wav"));
			cant_move_card = Gdx.audio.newSound(Gdx.files
					.internal("sounds/no_poder_sacar_carta.wav"));
			new_card = Gdx.audio.newSound(Gdx.files
					.internal("sounds/nueva_carta.wav"));
			pass_turn = Gdx.audio.newSound(Gdx.files
					.internal("sounds/pasar_turno.wav"));
			loose = Gdx.audio.newSound(Gdx.files.internal("sounds/perder.wav"));
			get_turn = Gdx.audio.newSound(Gdx.files
					.internal("sounds/recibir_turno.wav"));
		}
	}

	public class AssetMusic {
		public final Music song01;

		public AssetMusic(AssetManager am) {
			song01=Gdx.audio.newMusic(Gdx.files
					.internal("music/Hearthstone Soundtrack   Main Title[1].mp3"));
			
		}
	}

}
