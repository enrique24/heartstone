package gameObjects;

import util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CardPointNumber extends AbstractGameObject{
	
	private TextureRegion regNumber;
	

	public CardPointNumber(TextureRegion regNumber) {
		init(regNumber);
	}
	
	private void init(TextureRegion regNumber){
		this.regNumber = regNumber;
		dimension.set(Constants.CARD_HIT_NUMBER_WIDTH, Constants.CARD_HIT_NUMBER_HEIGHT);
		bounds.set(0,0,dimension.x,dimension.y);
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg=null;
		reg=regNumber;
		batch.draw(reg.getTexture(),
				position.x, position.y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
		
	}
	public void render(SpriteBatch batch,float cardPositionX, float cardPositionY) {
		TextureRegion reg=null;
		reg=regNumber;
		batch.draw(reg.getTexture(),
				cardPositionX+Constants.CARD_HIT_NUMBER_X, cardPositionY+Constants.CARD_HIT_NUMBER_Y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
		
	}

	public TextureRegion getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(TextureRegion regNumber) {
		this.regNumber = regNumber;
	}

}
