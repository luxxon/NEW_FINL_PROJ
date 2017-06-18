package com.patriciamarissa.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/* OVERALL TO DO LIST
 * FIX WHATEVER IS HAPPENING TO MONEY AND ENEMY GENERATION no
 * There's a problem with platform generation for me SAME OVER HERE BUT I GOT THE PLATFORM OVERLAP ISSUE AGAIN
 * FIX THAT STUPID ISSUE WITH ENEMY BOUNDARIES BUT HONESTLY IS IT EVEN NOTICABLE AT THIS POINT what?
 * COMMENTS yes maybe latr
 * THAT SHOULD BE IT? IDK ADD TO THIS AS YOU PLEASE no
 */

public class Main extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	
	Player player;
	Background background, background2;
	Floor floor, floor2;
	ArrayList <Platform> platforms;
	ArrayList <Hole> holes;
	ArrayList <Enemy> enemies;
	
	Shop shop;
	TitleScreen title ;
	CreditsScreen credits ;
	LoseScreen lose ;
	ControlsScreen control ;
	Story story;
	PauseScreen pause ;
	
	ShapeRenderer rend;
	Random rand;
	
	Music gameMusic;
	Sound moneySound;
		
	Texture lifeImg;
	Texture [] nums;
	
	int titleNum, gameNum, shopNum, controlsNum, creditsNum, pauseNum, loseNum, storyNum, ynNum;
	int page;
	int score, speed, money;
	long speedtimer;
	boolean isMoving;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		player = new Player(batch, 10, speed);
		background = new Background(batch, 0, 3430, 600, speed);
		background2 = new Background(batch, 3430, 3430, 600, speed);
		floor = new Floor(batch, 0, 3408, 100, speed);
		floor2 = new Floor(batch, 3408, 3408, 100, speed);
		platforms = new ArrayList <Platform> () ;
		enemies = new ArrayList <Enemy> () ;
		
		shop = new Shop(batch) ;
		title = new TitleScreen (batch) ;
		pause = new PauseScreen (batch) ;
		lose = new LoseScreen (batch) ;
		control = new ControlsScreen (batch) ;
		credits = new CreditsScreen (batch) ;
		
		rend = new ShapeRenderer ();
		rand = new Random (System.currentTimeMillis());
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/main game music.mp3"));
		moneySound = Gdx.audio.newSound(Gdx.files.internal("sounds/money1.wav"));

		lifeImg = new Texture(Gdx.files.internal("sprites/lifeImg.png"));
		nums = new Texture[10];
		for(int i = 0; i < nums.length; i++){
		    String fileName = "text/" + i + ".png";
		    nums[i] = new Texture(Gdx.files.internal(fileName));
		}
		
		titleNum = 1 ;
		gameNum = 2 ;
		shopNum = 3 ;
		controlsNum = 4 ;
		creditsNum = 5 ;
		pauseNum = 6 ;
		loseNum = 7 ;
		storyNum = 8;
		ynNum = 9;
		page = titleNum;
		
		score = 0;
		speed = 2; //speed on screen moving backwards
		money = 0;
		isMoving = false;

		createHoles();
		createPlatforms();
		//makeEnemies () ;
		runTimer () ;
	}
	
	public void runTimer () { // THANKS FOR THIS SIR!
		Timer.schedule (new Task () { 
			@Override public void run () {
				System.out.println ("SPEEDUP") ;
				increaseSpeed (1) ;
				}
		} , 10, 10) ; // first is delay to starting in seconds, second is time in between each tick in seconds
	}
	
	public void createPlatforms() {
		int platNum = 4; 
		platforms = new ArrayList<Platform>();
		platforms.add(new Platform(batch, speed, 200, 300, player.getMoneyMult(), player.deactivateFire));
		for(int i = 0; i < platNum; i++) {	
			platforms.add(new Platform(batch, speed, 200, platforms.get(i).getX() + platforms.get(i).getWidth(), player.getMoneyMult(), player.deactivateFire));
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
		platforms.add(new Platform(batch, speed, 320, 500, player.getMoneyMult(), player.deactivateFire));
		for(int i = 0; i < platNum; i++) {	
			platforms.add(new Platform(batch, speed, 320, platforms.get(i).getX() + platforms.get(i).getWidth(), player.getMoneyMult(), player.deactivateFire));
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
		platforms.add(new Platform(batch, speed, 440, 800, player.getMoneyMult(), player.deactivateFire));
		for(int i = 0; i < platNum; i++) {	
			platforms.add(new Platform(batch, speed, 440, platforms.get(i).getX() + platforms.get(i).getWidth(), player.getMoneyMult(), player.deactivateFire));
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
	}
	
	public void createHoles() {
		holes = new ArrayList<Hole>();
		int holeNum = 2; 
		holes.add(new Hole(batch, speed, 500));
		for(int i = 0; i < holeNum; i++) {	
			holes.add(new Hole(batch, speed, holes.get(i).getX() + holes.get(i).getWidth()));
		}
	}
	
	/*public void makeEnemies () {
		// Batch batch, int t, int x, int y, int s
		for (int i = 0 ; i < 4 ; i ++) {
			int p = rand.nextInt (platforms.size ()) ;
			int type = rand.nextInt (4) ;
			Platform plat = platforms.get (p) ;
			if (type == 2 && plat.getWidth () < 110) { // only make it a golem if the platform is large enough for the feet.
				type = 0 ;
			}
			if (type != 3) {
				enemies.add (new Enemy (batch, type, plat.getX () + plat.getWidth (), plat.getY () + plat.getHeight () - 1, speed, plat, holes)) ;
			}
			else { // lion is always on floor
				enemies.add (new Enemy (batch, type, plat.getX () + plat.getWidth (), 100, speed, plat, holes)) ;
			}
		}
	}*/
	
	public void makeEnemy (Platform plat) {
		System.out.println ("bam") ;
		int type = rand.nextInt (4) ;
		if (type == 2 && plat.getWidth () < 110) { // only make it a golem if the platform is large enough for the feet.
			type = 0 ;
		}
		if (type != 3) {
			enemies.add (new Enemy (batch, type, plat.getX () + plat.getWidth (), plat.getY () + plat.getHeight () - 1, speed, plat, holes)) ;
		}
		else { // lion is always on floor
			// CHANGE THE X SO IT AVOIDS THE HOLES
			enemies.add (new Enemy (batch, type, plat.getX () + plat.getWidth (), 100, speed, plat, holes)) ;
		}
	}
	
	public void move() {
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			player.moveRight();
			if (page == gameNum) {
				isMoving = true;
			}
		}
		else if(Gdx.input.isKeyPressed(Keys.LEFT)){
			player.moveLeft();
			if (page == gameNum) {
				isMoving = true;
			}	
		}
		else {
			player.endSpriteCycle();
		}
		
		if(Gdx.input.isKeyPressed(Keys.UP)){
			player.jump();
			if (page == gameNum) {
				isMoving = true;
			}	
		}
		else if(Gdx.input.isKeyPressed(Keys.DOWN) && !player.isJumping()){ 
			player.setGroundLvl(100);		
		}

		if (isMoving) {
			player.move();
		}
	}
	
	public void updatePage() {
		if (page == titleNum) {
			gameMusic.pause () ;
			startMenu();
		}
		else if (page == gameNum) {
			gameMusic.pause () ;
			playGame(); 
		}
		else if (page == loseNum) {
			gameMusic.pause () ;
			loseScreen ();	
		}
		else if (page == pauseNum) {
			gameMusic.pause () ;
			pauseMenu();
		}
		else if (page == shopNum) {
			gameMusic.pause () ;
			shopMenu () ;
		}
		else if (page == ynNum) {
			gameMusic.pause () ;
			areYouSureMenu();
		}
		else if (page == controlsNum) {
			gameMusic.pause () ;
			controlScreen () ;
		}
		else if (page == creditsNum) {
			gameMusic.pause () ;
			creditsScreen () ;
		}
		else if (page == storyNum) {
			gameMusic.pause () ;
			storyPage();
		}
	}
	
	public void update() {
		if (player.dying()) {
			if (player.getLives() > 0) {
				if (player.getDyingSpeed() == 0) {
					for (Hole h: holes) {
						if (h.collide(100, 100, player.getWidth(), player.getHeight())) {
							System.out.println("HOLE COLLISION");
							h.randPosition(1000);
							break;
						}
					}
					for (Enemy e: enemies) {
						if (e.collide(player) == true) { // if enemy collides, enemy dies. by blazing.
							e.loseHp(420);
						}
					}
				}
			}
			else {
				reset(true, true) ;
				page = loseNum ;
			}
			return;
		}
		
		updatePage();		
	}
	
	public void playGame() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			page = pauseNum;
		}
		
		gameMusic.play();
		boolean isOnPlatform = false;
		for (int i = 0; i < platforms.size(); i++) {
			if (player.getY () + player.getHeight () >= 600) {
				player.stopJump () ;
			}
			if (platforms.get(i).collideTop(player)) {
				isOnPlatform = true;
				if (platforms.get(i).moneyCollision(player)) {
					money += player.getMoneyMult () + 1;
					moneySound.play();
				}
				if (platforms.get(i).fireCollision(player) && player.deactivateFire == false) {
					player.die();
				}
				if (!player.isJumping()) {
					player.setGroundLvl(platforms.get(i).getY() + platforms.get(i).getHeight());
				}	
			}
			
		}
		if (!isOnPlatform && !player.isJumping()) {
			player.setGroundLvl(100);
		}
		
		if (player.getX() < 0 || player.getY() <= 0) {
			player.die();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE) && player.getLaserStrength() > 0){
			player.shoot () ;
		}
		
		if (player.deactivateHoles == false) {
			for (int i = 0; i < holes.size(); i++) {
				if (holes.get(i).collide(player)) {
					player.setGroundLvl(0);
					player.setInHole(holes.get(i));
				}
				
			}
		}
		
		for (int i = 0 ; i < enemies.size () ; i++) { // removing enemies that have gone off the left or finished dying
			if (enemies.get(i).getX () + enemies.get(i).getWidth () <= 0 || enemies.get(i).isDead () == true) {
				enemies.remove(i) ;
			}
		}
		
		if (isMoving) {
			movePlatforms();
			moveHoles();
			moveEnemies () ;
			background.move();
			background2.move();
			floor.move();
			floor2.move();
			updateLasers () ;
			score += speed/2;
		}
		move();
		for (Enemy e : enemies) {
			if (e.collide(player) && !e.dying) {
				player.die () ;
			}
		}
	}
	
	public void increaseSpeed (int s) {
		speed += s;
		player.setSpeed(player.getSpeed() + s);
		player.setMoveSpeed(speed);
		floor.setMoveSpeed(speed);
		floor2.setMoveSpeed(speed); //ignore that gap in the floor its not important
		for (Platform p : platforms) {
			p.setMoveSpeed(speed);
		}
		for (Hole h : holes) {
			h.setMoveSpeed(speed);
		}
		for (Enemy e : enemies) {
			e.setSpeed(speed);
		}
	}
	
	public void drawFloor () {
		rend.begin (ShapeType.Filled) ;
		rend.setColor (0, 0, 0, 255) ; // TEMP VALUES 255, 175, 229, 255
		rend.rect (0, 0, 1200, 100) ;
		rend.end () ;
	}
	

	@Override
	public void render () {
		try{
			Thread.sleep(33);
				
		}
		catch(InterruptedException  ex) {
			Thread.currentThread().interrupt();
		}
		
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		background.draw();
		background2.draw();
		floor.draw();
		floor2.draw();
		drawPlatforms();
		if (player.deactivateHoles == false) {
			drawHoles();
		}
		drawEnemies () ;
		player.draw();
		drawNum(900, 40, score - score%10);
		drawNum(50, 550, money);
		drawLives();
		update();
		
	}
	
	public void startMenu() {
		page = title.updatePage () ;
		if (page == gameNum) {
			reset(true, false);
		}
	}
	
	public void pauseMenu() {
		pause.update () ;
		page = pause.giveNextScreen () ;
	}
	
	public void areYouSureMenu() {
		page = pause.areYouSure();
		if (page == controlsNum || page == titleNum || page == shopNum) {
			reset (true, true) ;
		}
	}
	
	public void shopMenu () {
		shop.update (money) ;
		page = shop.giveNextScreen () ;
		if (page == gameNum) {
			shop.updatePlayersUpgrades(player.getPowers());
			reset(true, false);
		}
	}
	
	public void controlScreen () {
		control.update () ;
		page = control.giveNextScreen () ;
	}
	
	public void creditsScreen () {
		credits.update () ;
		page = credits.giveNextScreen () ;
	}
	
	public void loseScreen() {
		lose.update () ;
		page = lose.giveNextScreen () ;
		if (page == gameNum) {
			reset(true, true);
		}
	}
	
	public void storyPage() {
		if (story == null) {
			story = new Story(batch);
		}
		story.update();
		page = story.giveNextScreen();
		
	}
	
	public void drawPlatforms() {
		for (int i = 0; i < platforms.size(); i++) {
			platforms.get(i).draw();
		}
	}
	
	public void drawHoles() {
		for (int i = 0; i < holes.size(); i++) {
			holes.get(i).draw();
		}
	}
	
	public void movePlatforms() {
		for (int i = 0; i < platforms.size(); i++) {
			platforms.get(i).move();
			if (platforms.get(i).getX() < 0 - platforms.get(i).getWidth ()) { // went off screen to left
				int height = platforms.get(i).getY() ;
				int index = i - 1 ;
				if (i == 0) {
					index = platforms.size () - 1 ;
				}
				Platform newplat = new Platform(batch, speed, height, platforms.get(index).getX() + platforms.get(index).getWidth(), player.getMoneyMult(), player.deactivateFire) ;
				platforms.set(i, newplat) ;
				platforms.get(i).createMoney (rand.nextInt (5)) ;
				int chance = rand.nextInt (2) ; // 1 in 4 chance an enemy will generate
				if (chance == 1) {
					makeEnemy (platforms.get(i)) ;
				}
			}
		}
	}
	
	public void moveHoles() {
		for (int i = 0; i < holes.size(); i++) {
			holes.get(i).move();
		}
	}
	
	public void drawEnemies () {
		for (Enemy e : enemies) {
			e.draw () ;
		}
	}
	
	public void moveEnemies () {
		for (Enemy e : enemies) {
			e.moveWithPlat () ;
			if (!e.dying) {
				e.move () ;
			}
			else {
				e.animateDeath () ;
			}
		}
	}
	
	public void generateCourse() {
		randomizeHoles();
		enemies.clear () ;
		randomizePlatforms();
	}
	
	public void randomizePlatforms() {
		platforms.get(0).randPosition(0, 1);
		for (int i = 1; i < 4; i++) {
			platforms.get(i).randPosition(platforms.get(i-1).getX() + platforms.get(i-1).getWidth(), 1);
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
		platforms.get(4).randPosition(0, 2);
		for (int i = 5; i < 8; i++) {
			platforms.get(i).randPosition(platforms.get(i-1).getX() + platforms.get(i-1).getWidth(), 2);
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
		platforms.get(8).randPosition(0, 3);
		for (int i = 9; i < 12; i++) {
			platforms.get(i).randPosition(platforms.get(i-1).getX() + platforms.get(i-1).getWidth(), 3);
			int chance = rand.nextInt (2) ;
			if (chance == 1) {
				makeEnemy (platforms.get(i)) ;
			}
		}
		for (int i = 0 ; i < platforms.size () ; i++) {
			platforms.get(i).setFireStatus(player.deactivateFire);
		}
	}
	
	public void randomizeHoles() {
		holes.get(0).randPosition(0);
		for (int i = 1; i < holes.size(); i++) {
			holes.get(i).randPosition(holes.get(i-1).getX() + holes.get(i-1).getWidth());
		}
	}
	
	public void updateLasers () {
		for (int i = 0 ; i < enemies.size () ; i++) {
			if (enemies.get(i).getType () == 3) { // a golem
				ArrayList <Laser> elasers = enemies.get(i).getLasers () ;
				if (elasers.size () > 0) {
					for (int j = 0 ; j < elasers.size () ; j++) {
						elasers.get(j).move () ;
						elasers.get(j).draw () ;
						if (elasers.get(j).collide (player)) {
							elasers.get(j).doDamage (player) ;
							enemies.get(i).removeLaser (elasers.get(j)) ;
						}
						if (enemies.get(i).getX () + enemies.get(i).getSprite ().getWidth () <= 0) {
							enemies.get(i).removeLaser (elasers.get(j)) ;
						}
					}
				}
			}
		}
		ArrayList <Laser> plasers = player.getLasers () ;
		if (plasers.size () > 0) {
			for (int j = 0 ; j < plasers.size () ; j++) {
				Laser currentlas = plasers.get(j);
				currentlas.move () ;
				currentlas.draw () ;
				if (enemies.size () > 0) {
					for (int i = 0 ; i < enemies.size () ; i++) {
						if (currentlas.collide (enemies.get(i)) && enemies.get(i).dying == false && enemies.get(i).isDead() == false) {
							currentlas.doDamage (enemies.get(i)) ;
							player.removeLaser (currentlas) ;
						}
					}
				}
				if (currentlas != null && currentlas.getX () >= 1200) {
					player.removeLaser (currentlas) ;
				}
			}
		}
	}
	
	public void reset(boolean gameOver, boolean resetUps) {
		background.setX(0);
		background2.setX(3430);
		floor.setX(0);
		floor2.setX(3408);
		player.reset();
		player.draw();
		if (gameOver) {
			lose.updateCoins (money) ;
			shop.updateCoins(money) ;
			if ((score - score%10) > lose.getHighScore ()) {
				lose.updateHighScore(score - score%10);
			}
			player.resetLives();
			if (resetUps) {
				player.resetOneTimeUps();
			}
			shop.updateBoughtList(player.getPowers());
			score = 0;
			enemies.clear () ;
			for (int i = 0 ; i < platforms.size () ; i++) {
				platforms.get(i).updateMoneySprites (player.getMoneyMult()) ;
			}
		}
		else {
			page = gameNum;
		}
		generateCourse(); //change platform and hole positions after death
		isMoving = false;
    	resetSpeed();
	}
	
	public void resetSpeed() {
		player.setSpeed(10);
		speed = 2;
    	
    	floor.setMoveSpeed(speed);
    	floor2.setMoveSpeed(speed);
    	player.setMoveSpeed(speed);
    	for (Platform p : platforms) {
			p.setMoveSpeed(speed);
		}

    	for (Hole h : holes) {
			h.setMoveSpeed(speed);
		}
		for (Enemy e : enemies) {
			e.setSpeed(speed);
		}
	}
	
	public void drawLives() {
		batch.begin();
		for(int i = 0; i < player.getLives() - 1; i ++) {
        	batch.draw(lifeImg, i * 45 + 18, 10);
        }
        batch.end();
	}
	
	public void drawNum(int xDisplace, int y, int num) { //i was lazy ill make it nicer jk
		//draws numbers
		batch.begin();
		for(int i = 0; i < Integer.toString(num).length(); i++) {
			batch.draw(nums[Integer.parseInt(Integer.toString(num).substring(i, i + 1))], i * 20 + xDisplace, y);
        }
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose () ;
		rend.dispose () ;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
