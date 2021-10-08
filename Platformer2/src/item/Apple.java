package item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Hitbox;
import game.Map;
import player.Player;
import state.GameManager;
import util.Vector;

public class Apple extends Item {
	
	public static ArrayList<BufferedImage> animation;
	public static int frameInterval = 1;
	public int animationFrame;
	
	public Apple(Vector pos) {
		super(pos, new Vector(0, 0), 1, 1);
		
		this.autoPickup = true;
		this.purchaseable = false;
	}

	@Override
	public void onPickup() {
		GameManager.player.health += 5;
	}

	@Override
	public void tick(Map map) {
		this.animationFrame ++;
		if(this.animationFrame / Coin.frameInterval >= Coin.animation.size()) {
			this.animationFrame = 0;
		}
		
		if(this.outOfBounds(map)) {
			this.pos = new Vector(Math.random() * map.map[0].length, 1);
			this.vel = new Vector(0, 0);
		}
		else {
			this.move(map);
		}
	}

	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(0), g);
	}

}
