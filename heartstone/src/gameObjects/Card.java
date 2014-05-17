package gameObjects;

import game.Assets;
import util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents a card
 * @author Enrique Martín Arenal
 *
 */
public class Card extends AbstractGameObject{
	
	private TextureRegion regCard;
	private int attackPoints;
	private int defensePoints;
	private int crystalCost;
	/**
	 * Used to know whether the card is currently visible or not
	 */
	private boolean onTheTable;
	/**
	 * Used to know if the card has been already used during the turn
	 */
	private boolean used;
	/**
	 * Used to know if the card is currently selected or not
	 */
	private boolean selected;
	/**
	 * Object that represents the hitpoints of the card
	 */
	private CardPointNumber hitPoint;
	/**
	 * Used to know when to stop the dying animation
	 */
	private float alpha;
	private String cardName;
	
	
	
	public Card(TextureRegion regCard, float positionX,float positionY,int attackPoints, int defencePoints, int crystalCost,String cardName){
		init(regCard, positionX,positionY, attackPoints,  defencePoints,  crystalCost,cardName);
	}
	
	private void init(TextureRegion regCard, float positionX,float positionY,int attackPoints, int defencePoints, int crystalCost,String cardName){
		position.x=positionX;
		position.y=positionY;
		this.setCardName(cardName);
		this.attackPoints=attackPoints;
		this.defensePoints= defencePoints;
		this.crystalCost= crystalCost;
		dimension.set(Constants.CARD_WIDTH,Constants.CARD_HEIGHT);
		this.regCard=regCard;
		bounds.set(positionX,positionY,dimension.x,dimension.y);
		onTheTable=false;
		TextureRegion regNumber= Assets.instance.hitPoint.getNumberRegion(defensePoints);
		hitPoint= new CardPointNumber(regNumber);
		hitPoint.position.x=this.position.x+Constants.CARD_HIT_NUMBER_X;
		hitPoint.position.y=this.position.y+Constants.CARD_HIT_NUMBER_Y;
		alpha=1;
		
	}

	public CardPointNumber getHitPoint() {
		return hitPoint;
	}

	public void setHitPoint(CardPointNumber hitPoint) {
		this.hitPoint = hitPoint;
	}

	@Override
	public void render(SpriteBatch batch) {
		
		TextureRegion reg=null;
		reg=regCard;
		batch.draw(reg.getTexture(),
				position.x, position.y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
		
		hitPoint.render(batch,position.x,position.y);
	
		
		
	}

	public int getAttackPoints() {
		return attackPoints;
	}

	public void setAttackPoints(int attackPoints) {
		this.attackPoints = attackPoints;
	}

	public int getDefensePoints() {
		return defensePoints;
	}

	public void setDefensePoints(int defensePoints) {
		this.defensePoints = defensePoints;
	}

	public int getCrystalCost() {
		return crystalCost;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	public boolean isOnTheTable() {
		return onTheTable;
	}

	public void setOnTheTable(boolean onTheTable) {
		this.onTheTable = onTheTable;
		
	}


	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	
	
	

}
