package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import util.GraphicsTools;
import util.Vector;

public class Coin extends Entity{
	
	public static ArrayList<BufferedImage> animationRotate;
	public static int frameInterval = 5;
	public int animationFrame;
	
	public Coin(Vector pos) {
		super();
		
		this.width = 0.8;
		this.height = 0.8;
		
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);	this.pos.y -= this.cushion * 2;
		this.vel = new Vector((Math.random() - 0.5) * 0.1, -(Math.random() * 0.2) - 0.2);
		
		this.frictionInAir = false;
		
		this.animationFrame = 0;
	}
	
	public static void loadTextures() {
		animationRotate = GraphicsTools.loadAnimation("/Textures/Coin/coin_rotate.png", 11, 11);
	}

	@Override
	public void tick(Map map) {
		this.animationFrame ++;
		if(this.animationFrame / Coin.frameInterval >= Coin.animationRotate.size()) {
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
		
		this.drawSprite(animationRotate.get(animationFrame / Coin.frameInterval), g);
	}

}
