package game;

import gameObjects.Card;
import util.Constants;
import util.GamePreferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
/**
 * Class that renders the images of the GameScreen
 * @author Enrique Martín Arenal
 *
 */
public class WorldRenderer implements Disposable {
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;
	//We need another camera to render the GUI, we have to do this to correctly render
	//the bitmap font that is 15 pixels high
	private OrthographicCamera cameraGUI;
	
	public WorldRenderer (WorldController worldController) { 
		this.worldController = worldController;
		init();
	}
	/**
	 * Initializes the elements of this class
	 */
	private void init () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
		Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
				cameraGUI.position.set(0, 0, 0);
				cameraGUI.setToOrtho(true); // flip y-axis
				cameraGUI.update();
	}
	/**
	 * Renders the game
	 */
	public void render () {
		if(worldController.startGame){
				renderObjects();
				renderGui(batch);
				if(worldController.gameEnded){
						worldController.cameraHelper.applyTo(camera);
					batch.setProjectionMatrix(camera.combined);
					batch.begin();
					worldController.endBackgroundImage.draw(batch, 1);
					worldController.endImage.draw(batch, 1);
					batch.end();
					batch.setProjectionMatrix(cameraGUI.combined);
					batch.begin();
					Assets.instance.fonts.defaultBig.draw(batch, worldController.message, cameraGUI.viewportWidth/4.7f+70, cameraGUI.viewportHeight/2+190);			
					batch.end();
				}
	
		
		}else{
			worldController.cameraHelper.applyTo(camera);
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			worldController.testSprites[0].draw(batch);
			
			batch.end();
			batch.setProjectionMatrix(cameraGUI.combined);
			batch.begin();
			Assets.instance.fonts.defaultBig.draw(batch, worldController.message, cameraGUI.viewportWidth/4.7f, cameraGUI.viewportHeight/2+20);			
			batch.end();
		}
		
	}
	/**
	 * Renders the game objects
	 */
	private void renderObjects() {
		
			worldController.cameraHelper.applyTo(camera);
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			for(Sprite sprite : worldController.testSprites) {
				if(sprite!=null){
					if(sprite.getTexture()!=null)
						sprite.draw(batch);
				}
			}
			if(worldController.player.isYourTurn()){
				worldController.buttonEnemyTurn.draw(batch);
				worldController.buttonPassTurn.draw(batch);	
				
			}else{
				worldController.buttonPassTurn.draw(batch);	
				worldController.buttonEnemyTurn.draw(batch);
				
			}
			for(Card carta:worldController.cardsHand)
				carta.render(batch);
			for(Card carta:worldController.cardsEnemy)
				carta.render(batch);
			for(Card carta:worldController.cardsTable)
				carta.render(batch);
			if(worldController.dyingCard){
				worldController.dyingParticles.draw(batch);
				worldController.dyingParticles2.draw(batch);
			}
			if(worldController.attackSpecialEffect){
				worldController.attackParticles.draw(batch);
				worldController.attackParticles2.draw(batch);
			}
			batch.end();
		
	
		
	}
	/**
	 * Handles the resizing of the game
	 * @param width
	 * @param height
	 */
	public void resize (int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) *
		width;
		camera.update();
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT
		/ (float)height) * (float)width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,
		cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}
	
	/**
	 * Frames per second counter
	 * @param batch
	 */
	private void renderGuiFpsCounter (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
		// 45 or more FPS show up in green
		fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
		// 30 or more FPS show up in yellow
		fpsFont.setColor(1, 1, 0, 1);
		} else {
		// less than 30 FPS show up in red
		fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
		}
	

	/**
	 * Renders the GUI , 
	 * @param batch
	 */
	private void renderGui (SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// draw FPS text (anchored to bottom right edge)
		if(GamePreferences.instance.showFpsCounter)
		renderGuiFpsCounter(batch);
		batch.end();
		}
	/**
	 * Dispose the elements needed
	 */
	@Override
	public void dispose () {
		batch.dispose();
	}

}
