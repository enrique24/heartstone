package com.me.hs;



import java.io.IOException;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

public class Main {

	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;
	static Client client;
	static Listener listener = null;

	public static void main(String[] args) throws IOException {

		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 4096;
			settings.maxHeight = 4096;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/images",
					"../heartstone-android/assets/images", "heartStone.pack");
			TexturePacker.process(settings, "assets-raw/images-ui",
					"../heartstone-android/assets/images",
					"heartStone-ui.pack");
		}
		final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "heartstone";
		cfg.useGL20 = false;
		cfg.width = 640;
		cfg.height = 400;
		client = new Client();

		// new LwjglApplication(new HStone(client,listener), cfg);
		new LwjglApplication(new HSMain(), cfg);
	}

}
