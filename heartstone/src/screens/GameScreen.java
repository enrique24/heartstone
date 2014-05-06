package screens;

import java.io.IOException;

import util.AudioManager;
import util.Constants;
import util.Network;
import util.Stats;
import util.Network.ActionMessage;
import util.Network.RegisterName;
import game.Assets;
import game.WorldController;
import game.WorldRenderer;
import gameObjects.Player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

public class GameScreen extends AbstractGameScreen {

	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused, started = false;
	private Client clientSocket;
	private Listener listener;
	private boolean firstTurn = true;

	public GameScreen(Game game) {
		super(game);
		// TODO puede que sea mejor cambiar lo del constructo al show
	}

	public GameScreen(final Game game, Client client, Listener listen) {
		super(game);
		this.clientSocket = client;
		this.listener = listen;
		// Set Libgdx log level to DEBUG
		// TODO change to LOG_NONE at finish
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		// Initialize controller and renderer
		worldController = new WorldController(clientSocket, listener, game);
		worldRenderer = new WorldRenderer(worldController);
		// Game world is active on start
		paused = false;

	}

	@Override
	public void render(float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0, 0, 0, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();

	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);

	}

	@Override
	public void show() {
		util.GamePreferences.instance.load();
		worldController = new WorldController(clientSocket, listener, game);
		worldRenderer = new WorldRenderer(worldController);
		// worldController.startGame=started;
		clientSocket.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(clientSocket);

		listener = new Listener() {
			public void connected(Connection connection) {
				RegisterName registerName = new RegisterName();
				registerName.name = "Start";
				clientSocket.sendTCP(registerName);
			}

			public void received(Connection connection, Object object) {
				if (object instanceof Stats) {
					Stats card = (Stats) object;
					if (card.getCardAction().equals(Stats.CARD_ACTION_NEW_CARD)) {
						worldController.addCardToGame(card);

					} else if (card.getCardAction().equals(
							Stats.CARD_ACTION_NEW_ENEMY_CARD)) {
						worldController.addEnemyCardToGame(card);

						worldController.player
								.setEnemycardsOnHand(worldController.player
										.getEnemycardsOnHand() - 1);
						worldController.testSprites[Constants.GUI_POSITION_MANO_UNITS]
								.setRegion(Assets.instance.hitPoint
										.getNumberRegion(worldController.player
												.getEnemycardsOnHand()));

					} else if (card.getCardAction().equals(
							Stats.CARD_ACTION_ATTACKED_CARD)) {
						worldController.setAttackedCardForEnemyAttack(card);
					} else if (card.getCardAction().equals(
							Stats.CARD_ACTION_ATTACKING_CARD)) {
						worldController.setAnimatedCardForEnemyAttack(card,
								false);
					} else if (card.getCardAction().equals(
							Stats.CARD_ACTION_ATTACK_PLAYER)) {
						worldController.setAnimatedCardForEnemyAttack(card,
								true);
					} else if (card.getCardAction().equals(Stats.NO_MORE_CARDS)) {
						worldController.player
								.setHitPoints(worldController.player
										.getHitPoints() - 1);
					}

					return;
				}

				if (object instanceof ActionMessage) {
					ActionMessage receivedAction = (ActionMessage) object;
					if (receivedAction.action.equals(ActionMessage.START)) {
						// worldController.startGame = true;
						clientSocket.sendTCP(receivedAction);
						return;
					} else if (receivedAction.action
							.equals(ActionMessage.PASS_TURN)) {

						if (worldController.maxCrystals < 10) {
							worldController.maxCrystals++;
						}
						worldController.player
								.setCrystalsLeft(worldController.maxCrystals);
						worldController.player
								.setEnemyCrystalsLeft(worldController.maxCrystals);
						worldController.player.setYourTurn(true);

						worldController
								.setGuiNumbers(
										worldController.testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_TENS],
										worldController.testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_UNITS],
										worldController.player
												.getEnemyCrystalsLeft());
						worldController
								.setGuiNumbers(
										worldController.testSprites[Constants.GUI_POSITION_CRISTALES_TENS],
										worldController.testSprites[Constants.GUI_POSITION_CRISTALES_UNITS],
										worldController.player
												.getCrystalsLeft());

					} else if (receivedAction.action
							.equals(ActionMessage.DISCONNECT)) {
						// TODO avisar de que el otro se ha desconectado
						worldController.player.setEnemyHitPoints(0);
					}
					return;
				}

				if (object instanceof Player) {
					if (firstTurn) {
						worldController.player = (Player) object;
						worldController.maxCrystals = worldController.player
								.getCrystalsLeft();
						firstTurn = false;
						worldController.startGame = true;
						started = true;
						worldController.initGuiNumbers();
						worldController.player.setCardsLeft(22);
						worldController.player.setEnemyHitPoints(1);

					} else {
						Player receivedData = (Player) object;
						exchangePlayerData(receivedData, worldController.player);

					}
					return;

				}
			}

			public void disconnected(Connection connection) {
				game.setScreen(new MenuScreen(game));
				
			}
		};
		clientSocket.addListener(listener);
		new Thread("Connect") {
			public void run() {
				try {
					// clientSocket.connect(10000, "81.172.115.2",
					// Network.port);
					clientSocket.connect(10000, "192.168.1.12", Network.port);
				} catch (IOException ex) {
					ex.printStackTrace();
					worldController.message = "No se ha podido conectar con el servidor";
				}
			}
		}.start();
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
		clientSocket.stop();

	}

	@Override
	public void pause() {
		paused = true;

	}

	@Override
	public void resume() {

		super.resume();
		paused = false;
	}

	@Override
	public void dispose() {

		super.dispose();
		worldRenderer.dispose();
	}

	// Method used to exchange the data of one player to another
	public void exchangePlayerData(Player dataReceived, Player player) {
		player.setEnemyCardsOnTable(dataReceived.getCardsOntable());
		player.setEnemyCrystalsLeft(dataReceived.getCrystalsLeft());
		player.setEnemycardsLeft(dataReceived.getCardsLeft());
		player.setEnemycardsOnHand(dataReceived.getCardsOnHand());
		player.setYourTurn(!dataReceived.isYourTurn());
	}

}
