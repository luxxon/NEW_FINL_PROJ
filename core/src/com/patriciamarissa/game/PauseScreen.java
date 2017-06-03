package com.patriciamarissa.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class PauseScreen {
	
	private Batch batch ;
	private Texture img ;
	private Texture [] imgs ;
	private final int title, game, shop, controls, pause ;
	
	public PauseScreen (Batch batch) {
		title = 1 ;
		game = 2 ;
		shop = 3 ;
		controls = 4 ;
		pause = 6 ;
		imgs = new Texture [5] ;
		this.batch = batch ;
		
		imgs [0] = new Texture(Gdx.files.internal("menus/pause0.png"));
		imgs [1] = new Texture(Gdx.files.internal("menus/pause1.png"));
		imgs [2] = new Texture(Gdx.files.internal("menus/pause2.png"));
		imgs [3] = new Texture(Gdx.files.internal("menus/pause3.png"));
		imgs [4] = new Texture(Gdx.files.internal("menus/pause4.png"));
		
		img = imgs [0] ;
	}
	
	public void draw () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		batch.begin();
        batch.draw(img, 270, 250);
        batch.end();
	}
	
	public void updatePage () {
		// use mouse coordinates to figure out which img from the list to use
	}
	
	public void update () {
		updatePage () ;
		draw () ;
	}
	
	public int giveNextScreen () { // idk replace the keyboard commands with cursor stuff eventually
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			return game ;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.S)) {
			return shop ;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.C)) {
			return controls ;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.H)) {
			return title ;
		}
		else {
			return pause ;
		}
	}
}
