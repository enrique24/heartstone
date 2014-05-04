package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import gameObjects.Card;
import gameObjects.CardPointNumber;
import gameObjects.Player;
import screens.MenuScreen;
import util.AudioManager;
import util.CameraHelper;
import util.Constants;
import util.Network;
import util.Network.RegisterName;
import util.Stats;
import util.Network.ActionMessage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.me.hs.HStone;

public class WorldController extends InputAdapter {
	//TODO controlar que al llegar a 0 la vida no pete al mostrar los numeros
	//TODO terminar las partidas
	//The current game
	public Game game;
	// The socket used to receive/send data from/to the server
	private Client clientSocket;
	public boolean startGame = false;
	// Sprites used to render some objects that appear in the game
	public Sprite[] testSprites;
	 ArrayList<Card> cardsHand = new ArrayList<Card>();
	 ArrayList<Card> cardsTable = new ArrayList<Card>();
	 ArrayList<Card> cardsEnemy = new ArrayList<Card>();
	public CameraHelper cameraHelper;
	public ArrayList<Stats> cardStats = new ArrayList<Stats>();
	public Player player;
	// A rectangle that represents the table zone of the player
	private Rectangle table;
	// A rectangle that represents the enemy player
	private Rectangle enemyPlayerBounds;
	// A rectangle that represents the player
	private Rectangle playerBounds;
	// boolean used to update the animation that brings a card to the table
	private boolean moveToTable = false;
	// boolean used to update an attack animation
	public boolean attackCard = false;
	// boolean used to perform an attack from an enemy animation
	public boolean attackCardEnemy = false;
	// boolean used to control the attack animation
	private boolean returningCard = false;
	// boolean used to start the animation of a card who dying
	public boolean dyingCard = false;
	// boolean used to start the animation of cards in the hand reordering
	public boolean reorderHandCards = false;
	// boolean used to start the animation of cards in the table reordering
	public boolean reorderTableCards = false;
	// boolean used to start the animation of enemy cards reordering
	public boolean reorderEnemyCards = false;
	// boolean used to start the animation of bringing a new card to the game
	public boolean addNewCard = false;
	//boolean used to start the animation of loosing a card if you have the max amount of card allowed on hand
	public boolean looseCard=false;
	//booleans used for playing sound purposes
	public boolean playMoveCardSound=true;
	public boolean playDieSound=true;
	public boolean playAttackSound=false;
	//TODO a lo mejor algun booleano para sonido mas
	public boolean disconnect=false;
	// Card that is going to have an animation
	Card animatedCard;
	// Card that is going to be attacked
	Card attackedCard,attackingCard;
	// position on the table of the attacking card
	int attackingCardPosition;
	// Vector used to translate the cards
	protected Vector2 v2Velocity = new Vector2();
	// Particles used to create a special effect when a card dies
	public ParticleEffect dyingParticles = new ParticleEffect();
	public ParticleEffect dyingParticles2 = new ParticleEffect();
	// Particles used to create a special effect when a card attacks
	public ParticleEffect attackParticles = new ParticleEffect();
	public ParticleEffect attackParticles2 = new ParticleEffect();
	// Boolean used to render the attack special effect
	public boolean attackSpecialEffect = false;
	//Sprites used to represent buttons to pass the turn to the other player
	Sprite buttonPassTurn;
	Sprite buttonEnemyTurn;
	//Rectangle that represents the button to pass turn
	Rectangle buttonTurn;
	public int maxCrystals=0;
	//Message to show to the player at the start
	public String message="Buscando enemigos contra los que enfrentarse";

	public WorldController(Client socket, Listener listener,Game game) {
		this.game=game;
		init();
		this.clientSocket = socket;
		new Thread("ww") {
			public void run() {
				try {

					while (true)
						clientSocket.update(NORM_PRIORITY);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
		
	

	}

	private void init() {
		initGameObjects();
		cameraHelper = new CameraHelper();

	}

	private void initGameObjects() {
		// Create new array for 21 sprites for diverse purposes, background, glow effect and the user gui
		player= new Player(false);
		testSprites = new Sprite[21];
		dyingParticles.load(Gdx.files.internal("particles/desaparecer.pfx"),
				Gdx.files.internal("particles"));
		dyingParticles.allowCompletion();
		dyingParticles2.load(Gdx.files.internal("particles/desaparecer.pfx"),
				Gdx.files.internal("particles"));
		dyingParticles2.allowCompletion();
		attackParticles.load(Gdx.files.internal("particles/cross1.pfx"),
				Gdx.files.internal("particles"));
		attackParticles2.load(Gdx.files.internal("particles/cross2.pfx"),
				Gdx.files.internal("particles"));

		//glow effect
		Sprite spr = new Sprite(Assets.instance.carta.getCardRegion("resplandor"));
		spr.setSize(3f, 4.3f);
		spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
		testSprites[1] = spr;
		testSprites[1].setPosition(11, 11);
		
		//Cards that represents the enemy and the player
		Sprite sprPlayer1= new Sprite(Assets.instance.carta.getCardRegion("jugador"));
		sprPlayer1.setSize(Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
		sprPlayer1.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH,Constants.CARD_POSITION_HAND[4].y );
		testSprites[2]=sprPlayer1;
		Sprite sprPlayer2= new Sprite(Assets.instance.carta.getCardRegion("jugador"));
		sprPlayer2.setSize(Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
		sprPlayer2.setPosition(Constants.CARD_POSITION_OPPONENT[4].x+Constants.CARD_WIDTH,Constants.CARD_POSITION_OPPONENT[4].y );
		testSprites[3]=sprPlayer2;
		
		// Background
		Sprite spr2 = new Sprite(
				Assets.instance.carta.getCardRegion("Backgroung"));
		spr2.setSize(Constants.VIEWPORT_WIDTH + 2, Constants.VIEWPORT_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(-9, -6);
		testSprites[0] = spr2;
		
		// Gui Sprites
		spr2= new Sprite(Assets.instance.carta.getCardRegion("tusalud"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_HAND[4].y+2.1f);
		testSprites[Constants.GUI_POSITION_TU_SALUD]=spr2;
		
		spr2= new Sprite(Assets.instance.carta.getCardRegion("susalud"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_OPPONENT[4].y+2.1f);
		testSprites[Constants.GUI_POSITION_SU_SALUD]=spr2;
		
		spr2= new Sprite(Assets.instance.carta.getCardRegion("cristales"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_HAND[4].y+2.1f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES]=spr2;
		
		spr2= new Sprite(Assets.instance.carta.getCardRegion("cristales"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_OPPONENT[4].y+2.1f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY]=spr2;
		
		spr2= new Sprite(Assets.instance.carta.getCardRegion("mazo"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_HAND[4].y+2.1f-((Constants.CARD_HIT_NUMBER_HEIGHT+0.2f)*2));
		testSprites[Constants.GUI_POSITION_MAZO]=spr2;
		
		spr2= new Sprite(Assets.instance.carta.getCardRegion("mano"));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH+1.4f,Constants.CARD_HIT_NUMBER_HEIGHT+0.2f);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f, Constants.CARD_POSITION_OPPONENT[4].y+2.1f-((Constants.CARD_HIT_NUMBER_HEIGHT+0.2f)*2));
		testSprites[Constants.GUI_POSITION_MANO]=spr2;
		
		
		
		
		
		
		
		
		table = new Rectangle(-9, -1.9f, Constants.CARD_WIDTH * 5,
				Constants.CARD_HEIGHT);
		playerBounds = new Rectangle(Constants.PLAYER_BOUNDS.x,
				Constants.PLAYER_BOUNDS.y, Constants.CARD_WIDTH * 5,
				Constants.CARD_HEIGHT);
		enemyPlayerBounds = new Rectangle(Constants.ENEMY_PLAYER_BOUNDS.x,
				Constants.ENEMY_PLAYER_BOUNDS.y, Constants.CARD_WIDTH * 5,
				Constants.CARD_HEIGHT);
		buttonEnemyTurn= new Sprite(Assets.instance.carta.getCardRegion("Boton (Turno Enemigo)"));
		buttonEnemyTurn.setSize(3.2f, 2.5f);
		buttonEnemyTurn.setOrigin(buttonEnemyTurn.getWidth()/2f, buttonEnemyTurn.getHeight()/2f);
		buttonEnemyTurn.setPosition(Constants.CARD_POSITION_TABLE[4].x+3, Constants.CARD_POSITION_TABLE[4].y+1);
		
		buttonPassTurn= new Sprite(Assets.instance.carta.getCardRegion("Boton (Pasar Turno)"));
		buttonPassTurn.setSize(3.2f, 2.5f);
		buttonPassTurn.setOrigin(buttonEnemyTurn.getWidth()/2f, buttonEnemyTurn.getHeight()/2f);
		buttonPassTurn.setPosition(Constants.CARD_POSITION_TABLE[4].x+3, Constants.CARD_POSITION_TABLE[4].y+1);
		buttonTurn= new Rectangle(Constants.CARD_POSITION_TABLE[4].x+3, Constants.CARD_POSITION_TABLE[4].y+1, 3.2f, 2.5f);
		
		
		

	}

	public void update(float deltaTime) {
		if (startGame) {
			updateBounds();
			moveCardToTable(deltaTime);
			reorderHandCardAnimation();
			looseCardAnimation();
			attackCardCommand();
			attackCardAnimation();
			cardDiesAnimation();
			reorderTableCardAnimation();
			reorderEnemyCardAnimation();
			selectCard();
			moveCardToTableAnimation(deltaTime);
			attackFromEnemyAnimation();
			passTurn();
			cameraHelper.update(deltaTime);
			dyingParticles.update(deltaTime);
			dyingParticles2.update(deltaTime);
			attackParticles.update(deltaTime);
			attackParticles2.update(deltaTime);

			setGuiNumbers(testSprites[Constants.GUI_POSITION_MAZO_TENS], testSprites[Constants.GUI_POSITION_MAZO_UNITS], player.getCardsLeft());

		}else{
			if(message.equals("No se ha podido conectar con el servidor")){
				if(Gdx.input.isTouched()){
					game.setScreen(new MenuScreen(game));
				}
			}
		}
		backToMenu();

	}

	// update all cards bounds to match its current position
	private void updateBounds() {
		for (Card card : cardsHand) {
			card.bounds.x = card.position.x;
			card.bounds.y = card.position.y;
		}
		for (Card card : cardsEnemy) {
			card.bounds.x = card.position.x;
			card.bounds.y = card.position.y;
		}
		for (Card card : cardsTable) {
			card.bounds.x = card.position.x;
			card.bounds.y = card.position.y;
		}
	}

	private void updateTestObjects(float deltaTime) {
		/*
		 * // Get current rotation from selected sprite float rotation =
		 * testSprites[selectedSprite].getRotation(); // Rotate sprite by 90
		 * degrees per second rotation += 90 * deltaTime; // Wrap around at 360
		 * degrees rotation %= 360; // Set new rotation value to selected sprite
		 * testSprites[selectedSprite].setRotation(rotation);
		 */

	}

	// Select the car only if is your turn , the card is touched and the
	// card has not been used during the current turn
	private void selectCard() {
		if (player.isYourTurn()) {
			if (Gdx.input.justTouched()) {
				// do not select cards while animations are happening
				if (!moveToTable && !attackCard && !dyingCard
						&& !attackCardEnemy && !reorderEnemyCards
						&& !reorderHandCards && !reorderTableCards) {
					// Check if a cards in the hand is touched an then select it
					for (Card carta : cardsHand) {
						if (!carta.isUsed()) {
							// The input coordinates are not in the same "units"
							// as
							// the rest of the game objects so we have to adjust
							// them
							if (carta.bounds.contains(
									(float) (Gdx.input.getX() - (Gdx.graphics
											.getWidth() / 2))
											/ (Gdx.graphics.getWidth() / 2)
											* (9),
									(float) (Gdx.input.getY() - (Gdx.graphics
											.getHeight() / 2))
											/ (Gdx.graphics.getHeight() / 2)
											* (6) * -1)) {
								testSprites[1].setPosition(carta.position.x,
										carta.position.y);
								AudioManager.instance.play(Assets.instance.sounds.selectCard);
								// Set as selected the card that has been
								// touched
								for (int i = 0; i < cardsHand.size(); i++) {
									if (cardsHand.get(i) == carta) {
										cardsHand.get(i).setSelected(true);
									} else {
										cardsHand.get(i).setSelected(false);
									}
								}
								for (int i = 0; i < cardsTable.size(); i++) {
									cardsTable.get(i).setSelected(false);
								}

								return;
							} else {
								testSprites[1].setPosition(11, 11);
								carta.setSelected(false);

							}
						}

					}
					// Check if a cards in the table is touched an then select
					// it
					for (Card carta : cardsTable) {
						if (!carta.isUsed()) {
							// The input coordinates are not in the same "units"
							// as
							// the rest of the game objects so we have to adjust
							// them
							if (carta.bounds.contains(
									(float) (Gdx.input.getX() - (Gdx.graphics
											.getWidth() / 2))
											/ (Gdx.graphics.getWidth() / 2)
											* (9),
									(float) (Gdx.input.getY() - (Gdx.graphics
											.getHeight() / 2))
											/ (Gdx.graphics.getHeight() / 2)
											* (6) * -1)) {
								testSprites[1].setPosition(carta.position.x,
										carta.position.y);
								AudioManager.instance.play(Assets.instance.sounds.selectCard);
								for (int i = 0; i < cardsTable.size(); i++) {
									if (cardsTable.get(i) == carta) {
										cardsTable.get(i).setSelected(true);
									} else {
										cardsTable.get(i).setSelected(false);
									}
								}
								for (int i = 0; i < cardsHand.size(); i++) {
									cardsHand.get(i).setSelected(false);
								}

								return;
							} else {
								testSprites[1].setPosition(11, 11);
								carta.setSelected(false);
							}
						}

					}
				}

			}
		}
	}

	// Drag a selected card on the hand to the table
	private void moveCardToTable(float deltaTime) {
		if (player.isYourTurn()) {
			if (Gdx.input.justTouched() && cardsTable.size()<5) {
				for (int i = 0; i < cardsHand.size(); i++) {
					if (cardsTable.size() > 0) {
						// if there is another card on the table set the
						// table size according to the number of free space
						table.set(
								Constants.CARD_POSITION_TABLE[cardsTable.size()].x,
								Constants.CARD_POSITION_TABLE[cardsTable.size() - 1].y,
								Constants.CARD_WIDTH * (5 - cardsTable.size()),
								Constants.CARD_HEIGHT);
					} else {
						table.set(-9, -1.9f, Constants.CARD_WIDTH * 5,
								Constants.CARD_HEIGHT);
					}
					if (cardsHand.get(i).isSelected()
							&& (cardsHand.get(i).getCrystalCost() <= player
									.getCrystalsLeft())) {

						if (table.contains(
								(float) (Gdx.input.getX() - (Gdx.graphics
										.getWidth() / 2))
										/ (Gdx.graphics.getWidth() / 2) * (9),
								(float) (Gdx.input.getY() - (Gdx.graphics
										.getHeight() / 2))
										/ (Gdx.graphics.getHeight() / 2)
										* (6)
										* -1)) {
							cardsHand.get(i).bounds.x = cardsHand.get(i).position.x;
							cardsHand.get(i).bounds.y = cardsHand.get(i).position.y;
							animatedCard = cardsHand.get(i);
							cardsHand.get(i).setUsed(true);
							cardsHand.get(i).setSelected(false);
							player.setCrystalsLeft(player.getCrystalsLeft()
									- cardsHand.get(i).getCrystalCost());
							player.setCardsOntable(player.getCardsOntable() + 1);
							player.setCardsOnHand(player.getCardsOnHand() - 1);
							setGuiNumbers(testSprites[Constants.GUI_POSITION_CRISTALES_TENS], testSprites[Constants.GUI_POSITION_CRISTALES_UNITS], player.getCrystalsLeft());
							testSprites[1].setPosition(11, 11);
							cardsTable.add(cardsHand.get(i));
							cardsHand.remove(i);
							moveToTable = true;
							reorderHandCards = true;
							AudioManager.instance.play(Assets.instance.sounds.new_card);
							//send data to server
							Stats sendCard=getStatsFromCard(animatedCard);
							sendCard.setCardAction(Stats.CARD_ACTION_NEW_ENEMY_CARD);
							clientSocket.sendTCP(sendCard);
							return;

						}
					}
					// checks if the player has tried to move a card without
					// enough crystals to do it
					if (cardsHand.get(i).isSelected()
							&& cardsHand.get(i).getCrystalCost() > player
									.getCrystalsLeft()) {
						if (table.contains(
								(float) (Gdx.input.getX() - (Gdx.graphics
										.getWidth() / 2))
										/ (Gdx.graphics.getWidth() / 2) * (9),
								(float) (Gdx.input.getY() - (Gdx.graphics
										.getHeight() / 2))
										/ (Gdx.graphics.getHeight() / 2)
										* (6)
										* -1)) {

						}
						
						
					}
				}
			}
		}
	}

	// Animation to move a card to the table
	private void moveCardToTableAnimation(float deltaTime) {
		if (moveToTable) {

			// The .set() is setting the distance from the starting position to
			// end position
			v2Velocity.set(
					Constants.CARD_POSITION_TABLE[cardsTable.size() - 1].x
							- animatedCard.position.x,
					Constants.CARD_POSITION_TABLE[cardsTable.size() - 1].y
							- animatedCard.position.y);

			v2Velocity.x *= 0.05f; // Set speed of the object
			v2Velocity.y *= 0.05f;
			animatedCard.position.add(v2Velocity);
			if (animatedCard.position.y > Constants.CARD_POSITION_TABLE[0].y - 0.02) {
				animatedCard.position.y = Constants.CARD_POSITION_TABLE[0].y;
				moveToTable = false;
				animatedCard.setSelected(false);
			}

		}
	}

	// Card performing an attack
	private void attackCardCommand() {
		if (player.isYourTurn() && cardsTable.size() > 0) {
			// checks which card in the table is selected , return if no card is
			// selected
			for (int i = 0; i < cardsTable.size(); i++) {
				if (cardsTable.get(i).isSelected()) {
					attackingCard = cardsTable.get(i);
					attackingCardPosition = i;
					break;
				}

				if (i == cardsTable.size() - 1)
					return;
			}
			if (Gdx.input.justTouched() && !attackCard) {
				// Check if an enemy card is touched while a card in the hand is
				// selected
				for (Card carta : cardsEnemy) {
					if (carta.bounds
							.contains(
									(float) (Gdx.input.getX() - (Gdx.graphics
											.getWidth() / 2))
											/ (Gdx.graphics.getWidth() / 2)
											* (9),
									(float) (Gdx.input.getY() - (Gdx.graphics
											.getHeight() / 2))
											/ (Gdx.graphics.getHeight() / 2)
											* (6) * -1)) {
						playAttackSound=true;
						attackCard = true;
						attackedCard = carta;
						Stats animatedStats= getStatsFromCard(attackingCard);
						Stats attackedStats= getStatsFromCard(attackedCard);
						animatedStats.setCardAction(Stats.CARD_ACTION_ATTACKING_CARD);
						attackedStats.setCardAction(Stats.CARD_ACTION_ATTACKED_CARD);
						clientSocket.sendTCP(attackedStats);
						clientSocket.sendTCP(animatedStats);
						return;
					}
				}
				if (enemyPlayerBounds
						.contains(
								(float) (Gdx.input.getX() - (Gdx.graphics
										.getWidth() / 2))
										/ (Gdx.graphics.getWidth() / 2) * (9),
								(float) (Gdx.input.getY() - (Gdx.graphics
										.getHeight() / 2))
										/ (Gdx.graphics.getHeight() / 2)
										* (6)
										* -1)) {
					attackCard = true;
					playAttackSound=true;
					attackedCard = new Card(null, enemyPlayerBounds.x,
							enemyPlayerBounds.y, 0, 100, 0,null);
					player.setEnemyHitPoints(player.getEnemyHitPoints()
							- attackingCard.getAttackPoints());
					setGuiNumbers(testSprites[Constants.GUI_POSITION_SU_SALUD_TENS], testSprites[Constants.GUI_POSITION_SU_SALUD_UNITS], player.getEnemyHitPoints());
					Stats animatedStats= getStatsFromCard(attackingCard);
					animatedStats.setCardAction(Stats.CARD_ACTION_ATTACK_PLAYER);
					clientSocket.sendTCP(animatedStats);

				}
			}
		}

	}

	// Method invoked to start an animation which shows an enemy attacking you
	public void attackFromEnemy(
			boolean playerAttacked) {
		if (playerAttacked) {
			this.attackedCard = new Card(null, playerBounds.x, playerBounds.y,
					0, 100, 0,null);
			player.setHitPoints(player.getHitPoints()
					- attackingCard.getAttackPoints());
			setGuiNumbers(testSprites[Constants.GUI_POSITION_TU_SALUD_TENS], testSprites[Constants.GUI_POSITION_TU_SALUD_UNITS], player.getHitPoints());
			
		}

		for (int i = 0; i < cardsEnemy.size(); i++) {
			if (cardsEnemy.get(i).equals(attackingCard)) {
				attackingCardPosition = i;
				break;
			}
		}
		attackCardEnemy = true;
		playAttackSound=true;
	}

	// The animation of an enemy card attacking
	public void attackFromEnemyAnimation() {
		if (attackCardEnemy) {
			// The card moves to the other card position and performs an attack.
			// then goes back to its previous position
			if (!returningCard) {
				// The .set() is setting the distance from the starting position
				// to
				// end position
				v2Velocity.set(
						(attackedCard.position.x - attackingCard.position.x),
						(attackedCard.position.y - attackingCard.position.y));

				v2Velocity.x *= 0.05f; // Set speed of the object
				v2Velocity.y *= 0.05f;
				attackingCard.position.add(v2Velocity);
				// attack special effect
				if (attackingCard.position.y < attackedCard.position.y + 0.2) {
					attackParticles.setPosition(attackingCard.position.x,
							attackingCard.position.y
									+ (Constants.CARD_HEIGHT / 2) + 1.5f);
					attackParticles2.setPosition(attackingCard.position.x
							+ Constants.CARD_WIDTH, attackingCard.position.y
							+ (Constants.CARD_HEIGHT / 2) + 1.5f);
					attackSpecialEffect = true;
					attackParticles.start();
					attackParticles2.start();
					if(playAttackSound){
						AudioManager.instance.play(Assets.instance.sounds.attack);
						playAttackSound=false;
					}
					
				}

				if (attackingCard.position.y < attackedCard.position.y + 0.1)
					returningCard = true;
			} else {
				v2Velocity.set(attackingCard.position.x
						- attackedCard.position.x, attackingCard.position.y
						- attackedCard.position.y);

				v2Velocity.x *= 0.05f; // Set speed of the object
				v2Velocity.y *= 0.05f;
				attackingCard.position.add(v2Velocity);
				if (attackingCard.position.y > Constants.CARD_POSITION_OPPONENT[0].y - 0.1) {
					attackingCard.position.y = Constants.CARD_POSITION_OPPONENT[attackingCardPosition].y;
					attackingCard.position.x = Constants.CARD_POSITION_OPPONENT[attackingCardPosition].x;
					attackCardEnemy = false;
					returningCard = false;
					attackSpecialEffect = false;
					attackParticles.allowCompletion();
					attackParticles2.allowCompletion();
					attacksLogic(attackingCard, attackedCard);
				}

			}

		}
	}

	// The animation of a card attacking
	private void attackCardAnimation() {
		if (attackCard) {
			testSprites[1].setPosition(15, 15);
			// The card moves to the other card position and performs an attack.
			// then goes back to its previous position
			if (!returningCard) {
				// The .set() is setting the distance from the starting position
				// to
				// end position
				v2Velocity.set(
						(attackedCard.position.x - attackingCard.position.x),
						(attackedCard.position.y - attackingCard.position.y));

				v2Velocity.x *= 0.05f; // Set speed of the object
				v2Velocity.y *= 0.05f;
				attackingCard.position.add(v2Velocity);
				// attack special effect
				if (attackingCard.position.y > attackedCard.position.y - 0.2) {
					attackParticles.setPosition(attackingCard.position.x,
							attackingCard.position.y
									+ (Constants.CARD_HEIGHT / 2) + 1.5f);
					attackParticles2.setPosition(attackingCard.position.x
							+ Constants.CARD_WIDTH, attackingCard.position.y
							+ (Constants.CARD_HEIGHT / 2) + 1.5f);
					attackSpecialEffect = true;
					attackParticles.start();
					attackParticles2.start();
					if(playAttackSound){
						AudioManager.instance.play(Assets.instance.sounds.attack);
						playAttackSound=false;
					}
				}
				if (attackingCard.position.y > attackedCard.position.y - 0.1)
					returningCard = true;
			} else {
				v2Velocity.set(attackingCard.position.x
						- attackedCard.position.x, attackingCard.position.y
						- attackedCard.position.y);

				v2Velocity.x *= 0.05f; // Set speed of the object
				v2Velocity.y *= 0.05f;
				attackingCard.position.add(v2Velocity);
				if (attackingCard.position.y < Constants.CARD_POSITION_TABLE[attackingCardPosition].y + 0.1) {
					attackingCard.position.y = Constants.CARD_POSITION_TABLE[attackingCardPosition].y;
					attackingCard.position.x = Constants.CARD_POSITION_TABLE[attackingCardPosition].x;
					attackCard = false;
					returningCard = false;
					attackingCard.setUsed(true);
					attackingCard.setSelected(false);
					attackSpecialEffect = false;
					attackParticles.allowCompletion();
					attackParticles2.allowCompletion();
					attacksLogic(attackingCard, attackedCard);
					// TODO: enviar datos al server
				}

			}

		}
	}

	// The logic behind an attack
	private void attacksLogic(Card yourCard, Card enemyCard) {
		// your card new stats
		TextureRegion regNumber;
		playDieSound=true;
		if (yourCard.getDefensePoints() - enemyCard.getAttackPoints() > 0) {
			yourCard.setDefensePoints(yourCard.getDefensePoints()
					- enemyCard.getAttackPoints());
			regNumber = Assets.instance.hitPoint.getNumberRegion(yourCard
					.getDefensePoints());
		} else {
			yourCard.setDefensePoints(0);
			regNumber = Assets.instance.hitPoint.getNumberRegion(0);
			dyingCard = true;
		}
		yourCard.setHitPoint(new CardPointNumber(regNumber));

		// the enemy card new stats
		if (enemyCard.getDefensePoints() - yourCard.getAttackPoints() > 0) {
			enemyCard.setDefensePoints(enemyCard.getDefensePoints()
					- yourCard.getAttackPoints());
			regNumber = Assets.instance.hitPoint.getNumberRegion(enemyCard
					.getDefensePoints());
		} else {
			enemyCard.setDefensePoints(0);
			regNumber = Assets.instance.hitPoint.getNumberRegion(0);
			dyingCard = true;
		}
		enemyCard.setHitPoint(new CardPointNumber(regNumber));
	}

	// Animation for a card that is going to die
	private void cardDiesAnimation() {
		if (dyingCard) {
			
			// only the attackingCard or the attacked card are the ones that can
			// die
			if (attackingCard.getDefensePoints() == 0) {
				dyingParticles.setPosition(attackingCard.position.x + 1.3f,
						attackingCard.position.y + 2.1f);
				dyingParticles.start();
				attackingCard.setAlpha(attackingCard.getAlpha() - 0.01f);
				if(playDieSound)
				AudioManager.instance.play(Assets.instance.sounds.dead_guy);
				playDieSound=false;
			}
			if (attackingCard.getAlpha() < 0) {
				cardsEnemy.remove(attackingCard);
				cardsTable.remove(attackingCard);
				dyingCard = false;
				if (cardsTable.contains(attackingCard)) {
					player.setCardsOntable(player.getCardsOntable() - 1);
				} else {
					player.setEnemyCardsOnTable(player.getEnemyCardsOnTable() - 1);
				}
				// TODO: mandar datos al server
				// start reordering cards after the card "disappears"
				reorderTableCards = true;
				reorderEnemyCards = true;
			}
			if (attackedCard.getDefensePoints() == 0) {
				dyingParticles2.setPosition(attackedCard.position.x + 1.3f,
						attackedCard.position.y + 2.1f);
				dyingParticles2.start();
				attackedCard.setAlpha(attackedCard.getAlpha() - 0.01f);
				if(playDieSound)
					AudioManager.instance.play(Assets.instance.sounds.dead_guy);
				playDieSound=false;
			}
			if (attackedCard.getAlpha() < 0) {
				cardsEnemy.remove(attackedCard);
				cardsTable.remove(attackedCard);
				dyingCard = false;
				if (cardsTable.contains(attackedCard)) {
					player.setCardsOntable(player.getCardsOntable() - 1);
				} else {
					player.setEnemyCardsOnTable(player.getEnemyCardsOnTable() - 1);
				}
				// TODO: mandar datos al server
				// start reordering cards after the card "disappears"
				reorderTableCards = true;
				reorderEnemyCards = true;
			}
			if (!dyingCard) {
				dyingParticles.allowCompletion();
				dyingParticles2.allowCompletion();
			}

		}
	}

	// animation for reordering cards in the hand , this is done to have an
	// easier time dealing with cards positions
	private void reorderHandCardAnimation() {
		if (reorderHandCards) {
			// if there is no cards there is no need to reorder them
			if (cardsHand.size() ==0 ) {
				reorderHandCards = false;
				return;
			}
			for (int i = 0; i < cardsHand.size(); i++) {
				if(i<5){
					if(playMoveCardSound)
						AudioManager.instance.play(Assets.instance.sounds.new_card);
					v2Velocity.set(
							Constants.CARD_POSITION_HAND[i].x
									- cardsHand.get(i).position.x,
							Constants.CARD_POSITION_HAND[i].y
									- cardsHand.get(i).position.y);

					v2Velocity.x *= 0.07f; // Set speed of the object
					v2Velocity.y *= 0.07f;
					cardsHand.get(i).position.add(v2Velocity);

					if (cardsHand.size() - 1 == i) {
						if (cardsHand.get(i).position.x < Constants.CARD_POSITION_HAND[i].x + 0.02) {
							cardsHand.get(i).position.x = Constants.CARD_POSITION_HAND[i].x;
							reorderHandCards = false;
							playMoveCardSound=true;
							return;
						}

					}
				}else{
					looseCard=true;
					reorderHandCards=false;
				}
				
			}
			playMoveCardSound=false;
		}
	}

	// animation for reordering cards in the table , this is done to have an
	// easier time dealing with cards positions
	private void reorderTableCardAnimation() {
		if (reorderTableCards) {
			// if there is no cards there is no need to reorder them
			if (cardsTable.size() == 0) {
				reorderTableCards = false;
				return;
			}
			for (int i = 0; i < cardsTable.size(); i++) {
				v2Velocity.set(
						Constants.CARD_POSITION_TABLE[i].x
								- cardsTable.get(i).position.x,
						Constants.CARD_POSITION_TABLE[i].y
								- cardsTable.get(i).position.y);

				v2Velocity.x *= 0.07f; // Set speed of the object
				v2Velocity.y *= 0.07f;
				cardsTable.get(i).position.add(v2Velocity);
				if (cardsTable.size() - 1 == i) {
					if (cardsTable.get(i).position.x < Constants.CARD_POSITION_TABLE[i].x + 0.02) {
						cardsTable.get(i).position.x = Constants.CARD_POSITION_TABLE[i].x;
						reorderTableCards = false;
					}
				}
			}
		}
	}

	// animation for reordering enemy cards , this is done to have an
	// easier time dealing with cards positions
	private void reorderEnemyCardAnimation() {
		if (reorderEnemyCards) {
			// if there is no cards there is no need to reorder them
			if (cardsEnemy.size() == 0) {
				reorderEnemyCards = false;
				return;
			}
			for (int i = 0; i < cardsEnemy.size(); i++) {
				if(playMoveCardSound)
					AudioManager.instance.play(Assets.instance.sounds.new_card);
				v2Velocity.set(
						Constants.CARD_POSITION_OPPONENT[i].x
								- cardsEnemy.get(i).position.x,
						Constants.CARD_POSITION_OPPONENT[i].y
								- cardsEnemy.get(i).position.y);

				v2Velocity.x *= 0.07f; // Set speed of the object
				v2Velocity.y *= 0.07f;
				cardsEnemy.get(i).position.add(v2Velocity);
				if (cardsEnemy.size() - 1 == i) {
					if (cardsEnemy.get(i).position.x < Constants.CARD_POSITION_OPPONENT[i].x + 0.02) {
						cardsEnemy.get(i).position.x = Constants.CARD_POSITION_OPPONENT[i].x;
						reorderEnemyCards = false;
						playMoveCardSound=true;
						return;
					}
				}
			}
			playMoveCardSound=false;
		}
	}

	// Creates a new card with his statistics given an places it in the correct
	// array
	public void addCardToGame(Stats stats) {
		// creates the card out of sight
		Vector2 position = new Vector2(15, 0);
		Card card = new Card(Assets.instance.carta.getCardRegion(stats
				.getCardName()), position.x, position.y,
				stats.getAttackPower(), stats.getHitpoints(),
				stats.getCrystalCost(),stats
				.getCardName());
		animatedCard = card;
	
		cardsHand.add(card);
		if(cardsHand.size()<6){
			player.setCardsLeft(player.getCardsLeft()-1);
			player.setCardsOnHand(player.getCardsOnHand()+1);
			reorderHandCards = true;
		}else{
			animatedCard=card;
			looseCard=true;
		}
	
		

	}

	public ArrayList<Card> getCardsHand() {
		return cardsHand;
	}

	public void setCardsHand(ArrayList<Card> cardsHand) {
		this.cardsHand = cardsHand;
	}

	public ArrayList<Card> getCardsTable() {
		return cardsTable;
	}

	public void setCardsTable(ArrayList<Card> cardsTable) {
		this.cardsTable = cardsTable;
	}

	public ArrayList<Card> getCardsEnemy() {
		return cardsEnemy;
	}

	public void setCardsEnemy(ArrayList<Card> cardsEnemy) {
		this.cardsEnemy = cardsEnemy;
	}

	//add an enemy card to the game card
	public void addEnemyCardToGame(Stats stats) {
		// creates the card out of sight
		Vector2 position = new Vector2(15, 0);
		Card card = new Card(Assets.instance.carta.getCardRegion(stats
				.getCardName()), position.x, position.y,
				stats.getAttackPower(), stats.getHitpoints(),
				stats.getCrystalCost(),stats
				.getCardName());
		animatedCard = card;
		cardsEnemy.add(card);
		reorderEnemyCards = true;
		player.setEnemyCrystalsLeft(player.getEnemyCrystalsLeft()-card.getCrystalCost());
		if(player.getEnemyCrystalsLeft()>0)
			setGuiNumbers(testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_TENS], testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_UNITS], player.getEnemyCrystalsLeft());
		else
			setGuiNumbers(testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_TENS], testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_UNITS], 0);
		
	}
	
	//get the Stats from a card
	public Stats getStatsFromCard(Card card){
		Stats cardStats= new Stats();
		cardStats.setAttackPower(card.getAttackPoints());
		cardStats.setCardName(card.getCardName());
		cardStats.setCrystalCost(card.getCrystalCost());
		cardStats.setHitpoints(card.getDefensePoints());
		cardStats.setCardName(card.getCardName());
		return cardStats;
	}
	
	//Pass turn
	public void passTurn(){
		if(player.isYourTurn()){
			// do not pass turns while animations are happening
			if (!moveToTable && !attackCard && !dyingCard
					&& !attackCardEnemy && !reorderEnemyCards
					&& !reorderHandCards && !reorderTableCards){
				if(Gdx.input.justTouched()){
					if (buttonTurn
							.contains(
									(float) (Gdx.input.getX() - (Gdx.graphics
											.getWidth() / 2))
											/ (Gdx.graphics.getWidth() / 2) * (9),
									(float) (Gdx.input.getY() - (Gdx.graphics
											.getHeight() / 2))
											/ (Gdx.graphics.getHeight() / 2)
											* (6)
											* -1)){
						
						player.setYourTurn(false);
						AudioManager.instance.play(Assets.instance.sounds.pass_turn);
						ActionMessage msg= new ActionMessage();
						msg.action=ActionMessage.PASS_TURN;
						clientSocket.sendTCP(msg);
						if(player.getEnemycardsOnHand()!=5){
							player.setEnemycardsOnHand(player.getEnemycardsOnHand()+1);		
							
						}
						resetUsed();
						testSprites[Constants.GUI_POSITION_MANO_UNITS].setRegion(Assets.instance.hitPoint.getNumberRegion(player.getEnemycardsOnHand()));
						
							
					}
				}
			}
	
		}
	}
	
	//animation for a loosed card
	private void looseCardAnimation(){
		if(looseCard){
			// The .set() is setting the distance from the starting position to
						// end position
						v2Velocity.set(
								-12f,
								Constants.CARD_POSITION_HAND[0].y
										- animatedCard.position.y);

						v2Velocity.x *= 0.05f; // Set speed of the object
						v2Velocity.y *= 0.05f;
						animatedCard.position.add(v2Velocity);
						if (animatedCard.position.x <-12+0.2f) {
							looseCard=false;
							cardsHand.remove(animatedCard);
						}
		}
	}
	
	//sets the used state of all the cards in the table to false
	public void resetUsed(){
		for(Card card:cardsTable){
			card.setUsed(false);
		}
	}
	
	//sets the animatedCard given the stats from the enemy card that is going to perform an attack
	public void setAnimatedCardForEnemyAttack(Stats stat,boolean attackEnemyPlayer){
		for(Card card: cardsEnemy){
			if(card.getCardName().equals(stat.getCardName())){
				attackingCard=card;
				attackFromEnemy(attackEnemyPlayer);
			}
		}
	}
	//sets the attackedCard given the stats from the server if the card that is going to receive an attack
	public void setAttackedCardForEnemyAttack(Stats stat){
		for(Card card: cardsTable){
			if(card.getCardName().equals(stat.getCardName())){
				attackedCard=card;
			}
		}
	}
	private void backToMenu () {
		//disconnects from server and goes back to the menu screen
		if(Gdx.input.isKeyPressed(Input.Keys.BACK)|| disconnect){
			clientSocket.stop();
			//game.setScreen(new MenuScreen(game));
		}
		
		
	}
	
	//Sets the correct number to display in the chosen part of the gui
	public void setGuiNumbers(Sprite tens, Sprite units, int stat){
		if(stat<10){
			tens.setRegion(Assets.instance.hitPoint.getNumberRegion(0));
			units.setRegion(Assets.instance.hitPoint.getNumberRegion(stat));
		}else{
			tens.setRegion(Assets.instance.hitPoint.getNumberRegion((int)(stat/10)));
			units.setRegion(Assets.instance.hitPoint.getNumberRegion(stat%10));
		}
	}
	
	//initializes the gui numbers
	public void initGuiNumbers(){
		//Tu salud
		Sprite spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(3));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f);
		testSprites[Constants.GUI_POSITION_TU_SALUD_TENS]=spr2;
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(0));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+(Constants.CARD_HIT_NUMBER_WIDTH*2)+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f);
		testSprites[Constants.GUI_POSITION_TU_SALUD_UNITS]=spr2;
		
		//Su salud
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(3));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_OPPONENT[4].y+2.2f);
		testSprites[Constants.GUI_POSITION_SU_SALUD_TENS]=spr2;
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(0));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+(Constants.CARD_HIT_NUMBER_WIDTH*2)+1.4f, Constants.CARD_POSITION_OPPONENT[4].y+2.2f);
		testSprites[Constants.GUI_POSITION_SU_SALUD_UNITS]=spr2;
		
		//Cristales enemy
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(0));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_OPPONENT[4].y+2.2f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_TENS]=spr2;
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(1));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+(Constants.CARD_HIT_NUMBER_WIDTH*2)+1.4f, Constants.CARD_POSITION_OPPONENT[4].y+2.2f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES_ENEMY_UNITS]=spr2;
		
		//Cristales
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(0));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES_TENS]=spr2;
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(1));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+(Constants.CARD_HIT_NUMBER_WIDTH*2)+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f-(Constants.CARD_HIT_NUMBER_HEIGHT+0.2f));
		testSprites[Constants.GUI_POSITION_CRISTALES_UNITS]=spr2;
		
		//mano
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(2));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_OPPONENT[4].y+2.2f-((Constants.CARD_HIT_NUMBER_HEIGHT+0.2f)*2));
		testSprites[Constants.GUI_POSITION_MANO_UNITS]=spr2;
		
		//mazo
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(2));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+Constants.CARD_HIT_NUMBER_WIDTH+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f-((Constants.CARD_HIT_NUMBER_HEIGHT+0.2f)*2));
		testSprites[Constants.GUI_POSITION_MAZO_TENS]=spr2;
		spr2= new Sprite(Assets.instance.hitPoint.getNumberRegion(2));
		spr2.setSize(Constants.CARD_HIT_NUMBER_WIDTH,Constants.CARD_HIT_NUMBER_HEIGHT);
		spr2.setOrigin(spr2.getWidth() / 2.0f, spr2.getHeight() / 2.0f);
		spr2.setPosition(Constants.CARD_POSITION_HAND[4].x+Constants.CARD_WIDTH+0.1f+(Constants.CARD_HIT_NUMBER_WIDTH*2)+1.4f, Constants.CARD_POSITION_HAND[4].y+2.2f-((Constants.CARD_HIT_NUMBER_HEIGHT+0.2f)*2));
		testSprites[Constants.GUI_POSITION_MAZO_UNITS]=spr2;
	
	}

}
