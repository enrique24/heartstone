package com.me.hs;

import java.io.IOException;
import java.net.Socket;

import util.Network;
import util.Network.ActionMessage;
import util.Network.RegisterName;
import util.Stats;
import game.Assets;
import game.WorldController;
import game.WorldRenderer;
import gameObjects.Player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

public class HStone implements ApplicationListener {

	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	private Client clientSocket;
	private Listener listener;
	private boolean firstTurn=true;

	public HStone(Client clientSocket, Listener listener) {
		super();
		this.clientSocket = clientSocket;
		this.listener = listener;
	}

	@Override
	public void create() {

		// Set Libgdx log level to DEBUG
		// TODO change to LOG_NONE at finish
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		// Initialize controller and renderer
		worldController = new WorldController(clientSocket, listener,null);
		worldRenderer = new WorldRenderer(worldController);
		// Game world is active on start
		paused = false;
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
					if(card.getCardAction().equals(Stats.CARD_ACTION_NEW_CARD)){
						worldController.addCardToGame(card);
					}else if(card.getCardAction().equals(Stats.CARD_ACTION_NEW_ENEMY_CARD)){
						worldController.addEnemyCardToGame(card);
						worldController.player.setEnemycardsOnHand(worldController.player.getEnemycardsOnHand()-1);
					}else if(card.getCardAction().equals(Stats.CARD_ACTION_ATTACKED_CARD)){
						worldController.setAttackedCardForEnemyAttack(card);
					}else if(card.getCardAction().equals(Stats.CARD_ACTION_ATTACKING_CARD)){
						worldController.setAnimatedCardForEnemyAttack(card,false);				
					}else if(card.getCardAction().equals(Stats.CARD_ACTION_ATTACK_PLAYER)){
						worldController.setAnimatedCardForEnemyAttack(card,true);
					}
					
					return;
				}

				if (object instanceof ActionMessage) {
					ActionMessage receivedAction = (ActionMessage) object;
					if (receivedAction.action.equals(ActionMessage.START)) {
						//worldController.startGame = true;
						clientSocket.sendTCP(receivedAction);
						return;
					} else if (receivedAction.action
							.equals(ActionMessage.PASS_TURN)) {
						
						if(worldController.maxCrystals<10 ){
							worldController.maxCrystals++;
						}
						worldController.player.setCrystalsLeft(worldController.maxCrystals);
						worldController.player.setEnemyCrystalsLeft(worldController.maxCrystals);
						worldController.player.setYourTurn(true);
						worldController.resetUsed();
						
							
					}
					return;
				}

				if (object instanceof Player) {
					if(firstTurn){
						worldController.player=(Player) object;
						worldController.maxCrystals=worldController.player.getCrystalsLeft();
						firstTurn=false;
						worldController.startGame = true;
					
					}else{
						Player receivedData= (Player) object;
						exchangePlayerData(receivedData, worldController.player);
						
					}
					return;
				
				}
			}

			public void disconnected(Connection connection) {
				// TODO: volver a la pantalla inicial al desconectar
			}
		};
		clientSocket.addListener(listener);
		new Thread("Connect") {
			public void run() {
				try {
				//clientSocket.connect(10000, "81.172.115.2", Network.port);
					clientSocket.connect(10000, "192.168.1.12", Network.port);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}

	@Override
	public void dispose() {
		worldRenderer.dispose();
		Assets.instance.dispose();
	}

	// Game loop
	@Override
	public void render() {
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
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		Assets.instance.init(new AssetManager());
		paused = false;
	}
	
    //Method used to exchange the data of one player to another
    public void exchangePlayerData(Player dataReceived,Player player){
    	player.setEnemyCardsOnTable(dataReceived.getCardsOntable());
    	player.setEnemyCrystalsLeft(dataReceived.getCrystalsLeft());
    	player.setEnemycardsLeft(dataReceived.getCardsLeft());
    	player.setEnemycardsOnHand(dataReceived.getCardsOnHand());
    	player.setYourTurn(!dataReceived.isYourTurn());
    }
	
}
