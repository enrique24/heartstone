package util;


public class Stats {
	private int crystalCost;
	private int attackPower;
	private int hitpoints;
	private String cardName;
	private String cardAction;

	public static String CARD_ACTION_NEW_ENEMY_CARD="c1";
	public static String CARD_ACTION_NEW_CARD="c2";
	public static String CARD_ACTION_ATTACKED_CARD="c3";
	public static String CARD_ACTION_ATTACKING_CARD="c4";
	public static String CARD_ACTION_ATTACK_PLAYER="c5";
	public static String NO_MORE_CARDS="c6";
	
	
	
	public Stats(){
		
	}
	public Stats(int crystalCost, int attackPower, int hitpoints,
			String cardName) {
		this.crystalCost = crystalCost;
		this.attackPower = attackPower;
		this.hitpoints = hitpoints;
		this.cardName = cardName;
	}
	public int getCrystalCost() {
		return crystalCost;
	}
	public void setCrystalCost(int crystalCost) {
		this.crystalCost = crystalCost;
	}
	public int getAttackPower() {
		return attackPower;
	}
	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}
	public int getHitpoints() {
		return hitpoints;
	}
	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardAction() {
		return cardAction;
	}
	public void setCardAction(String cardAction) {
		this.cardAction = cardAction;
	}

	

}
