package item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import state.GameManager;
import util.GraphicsTools;
import util.MathTools;
import util.Vector;

public class Coin extends Item {
	
	public static ArrayList<BufferedImage> animation;
	public static int frameInterval = 5;
	public int animationFrame;
	
	public Coin(Vector pos) {
		super(new Vector(pos.x, pos.y - (cushion * 2)), new Vector((Math.random() - 0.5) * 0.1, -(Math.random() * 0.2) - 0.2), 0.8, 0.8);
		
		this.frictionInAir = false;
		
		this.animationFrame = 0;
		
		this.autoPickup = true;
		this.purchaseable = false;
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
			//if the player is close enough, start moving towards player
			double dist = MathTools.dist(this.pos.x, this.pos.y, GameManager.player.pos.x, GameManager.player.pos.y);
			if(dist <= 5) {
				Vector toPlayer = new Vector(GameManager.player.pos.x - this.pos.x, GameManager.player.pos.y - this.pos.y);
				toPlayer.setMagnitude(0.1);
				if(toPlayer.y < 0) {
					this.pos.y -= this.cushion * 2;
				}
				this.vel.addVector(toPlayer);
				this.gravity = false;
			}
			else {
				this.gravity = true;
			}
			
			this.move(map);
		}
		
	}

	@Override
	public void draw(Graphics g) {
		
		this.drawSprite(animation.get(animationFrame / Coin.frameInterval), g);
	}

	@Override
	public void onPickup() {
		GameManager.gold ++;
	}

}
