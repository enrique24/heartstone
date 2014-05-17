package gameObjects;


import util.Constants;

/**
 * Represents the actual state of a game
 * @author Enrique Martín Arenal
 *
 */
public class Player{

	private int hitPoints;
	private int cardsLeft;
	private int cardsOnHand;
	private int cardsOntable;
	private int crystalsLeft;
	private boolean yourTurn;
	private int enemyHitPoints;
	private int enemycardsLeft;
	private int enemycardsOnHand;
	private int enemyCardsOnTable;
	private int enemyCrystalsLeft;
	
	
	public Player(){
		
	}
	public Player(boolean yourTurn) {
		this.yourTurn = yourTurn;
		hitPoints=Constants.HIT_POINTS;
		enemycardsLeft=22;
		enemycardsOnHand=2;
		enemyCardsOnTable=0;
		enemyCrystalsLeft=0;
		enemyHitPoints=Constants.HIT_POINTS;
		cardsLeft=24;
		cardsOnHand=0;
		cardsOntable=0;
		crystalsLeft=1;
	}
	public int getHitPoints() {
		return hitPoints;
	}
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	public int getCardsLeft() {
		return cardsLeft;
	}
	public void setCardsLeft(int cardsLeft) {
		this.cardsLeft = cardsLeft;
	}
	public int getCardsOnHand() {
		return cardsOnHand;
	}
	public void setCardsOnHand(int cardsOnHand) {
		this.cardsOnHand = cardsOnHand;
	}
	public int getCardsOntable() {
		return cardsOntable;
	}
	public void setCardsOntable(int cardsOntable) {
		this.cardsOntable = cardsOntable;
	}
	public int getCrystalsLeft() {
		return crystalsLeft;
	}
	public void setCrystalsLeft(int crystalsLeft) {
		this.crystalsLeft = crystalsLeft;
	}
	public boolean isYourTurn() {
		return yourTurn;
	}
	public void setYourTurn(boolean yourTurn) {
		this.yourTurn = yourTurn;
	}
	public int getEnemyHitPoints() {
		return enemyHitPoints;
	}
	public void setEnemyHitPoints(int enemyHitPoints) {
		this.enemyHitPoints = enemyHitPoints;
	}
	public int getEnemycardsLeft() {
		return enemycardsLeft;
	}
	public void setEnemycardsLeft(int enemycardsLeft) {
		this.enemycardsLeft = enemycardsLeft;
	}
	public int getEnemycardsOnHand() {
		return enemycardsOnHand;
	}
	public void setEnemycardsOnHand(int enemycardsOnHand) {
		this.enemycardsOnHand = enemycardsOnHand;
	}
	public int getEnemyCardsOnTable() {
		return enemyCardsOnTable;
	}
	public void setEnemyCardsOnTable(int enemyCardsOnTable) {
		this.enemyCardsOnTable = enemyCardsOnTable;
	}
	public int getEnemyCrystalsLeft() {
		return enemyCrystalsLeft;
	}
	public void setEnemyCrystalsLeft(int enemyCrystalsLeft) {
		this.enemyCrystalsLeft = enemyCrystalsLeft;
	}
}
