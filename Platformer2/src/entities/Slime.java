package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import game.GameManager;
import game.Map;
import main.MainPanel;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class Slime extends Enemy {
	
	public int moveCounter;
	public int moveInterval = 180;
	
	public static ArrayList<BufferedImage> animationIdle;
	public int idleFrame;
	public int idleInterval = 60;
	public int idleCounter;

	public Slime(Point pos) {
		super();
		
		this.width = 1.2;
		this.height = 1.2;
		
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);
		
		this.moveInterval += (int) ((Math.random() - 0.5d) * 50d);
		this.moveCounter = moveInterval;
		
		this.idleInterval += (int) ((Math.random() - 0.5d) * 10d);
		this.idleCounter = idleInterval;
		this.idleFrame = 0;
		
		this.frictionInAir = false;
		
		this.damageOnContact = true;
		this.contactDamage = 5;
		
		this.health = 10;
		this.maxHealth = 10;
	}
	
	public static void loadTextures() {
		
		int width = 13;	//w and h of the slime sprite
		int height = 13;
		
		BufferedImage idle = GraphicsTools.loadImage("/Textures/Slime/slime_idle.png");
		
		int animationLength = idle.getHeight() / height;
		animationIdle = new ArrayList<BufferedImage>();
		
		for(int i = 0; i < animationLength; i++) {
			BufferedImage next = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = next.getGraphics();
			g.drawImage(idle, 0, -(i * height), null);
			animationIdle.add(next);
		}
		
		
	}
	
	public void tick(Map map) {
		
		if(this.onGround) {
			this.moveCounter --;
			if(this.moveCounter == 0) {
				//do a jump
				
				double yVel = 0.3 + ((Math.random() - 0.5d) * 0.1);
				double xVel = 0.3 + ((Math.random() - 0.5d) * 0.1);
				
				if(Math.random() > 0.5) {
					this.vel.addVector(new Vector(xVel, -yVel));
				}
				else {
					this.vel.addVector(new Vector(-xVel, -yVel));
				}
				this.pos.y -= this.cushion * 2;
				this.moveCounter = moveInterval;
			}
		}
		
		this.idleCounter ++;
		if(this.idleCounter > this.idleInterval) {
			this.idleCounter = 0;
			this.idleFrame = (this.idleFrame + 1) % Slime.animationIdle.size();
		}
		
		if(this.outOfBounds(map)) {
			this.health = 0;
		}
		else {
			this.move(map);
		}
		
	}
	
	public void draw(Graphics g) {
		//this.drawHitboxes(g);
		
		
		
		this.drawSprite(Slime.animationIdle.get(idleFrame), g);
		
		if(this.health != this.maxHealth) {
			this.drawHealthBar(g);
		}
		
		
	}
	
}
