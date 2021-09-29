package melee;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Hitbox;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.MathTools;
import util.Point;
import util.Vector;

public class MeleeAttack {
	
	//many hitboxes, each with an offset vector. all hitboxes should be applied at once
	
	//when we do a melee attack, first figure out at which angle from the entity was the attack performed.
	//then rotate all the offset vectors by that angle, then you check if anything was hit by the attack
	
	public boolean attacking = false;
	public int attackTimeLeft;
	
	public Hitbox[] activeHitboxes;
	
	public ArrayList<BufferedImage> activeAnimation;
	public int activeFrame;
	public Vector activeAnimationOffset;
	
	public Vector activeAttackVector;
	
	
	public static int frameInterval = 2;
	
	public static ArrayList<BufferedImage> slashAnimation;
	public static Vector slashOffset = new Vector(1.2, -0.2);
	public static double slashImageWidth = 3;
	public static double slashImageHeight = 3;
	public static int slashTime = 1;
	
	public MeleeAttack() {
		this.attackTimeLeft = 0;
		
		this.activeHitboxes = new Hitbox[] {};
		this.activeAnimation = new ArrayList<BufferedImage>();
		this.activeAnimationOffset = new Vector(0, 0);
		
		this.activeFrame = 1;
	}
	
	public void tick() {
		if(this.attacking) {
			this.attackTimeLeft --;
			if(this.attackTimeLeft < 0) {
				this.attacking = false;
			}
		}
		if(this.activeFrame / frameInterval < this.activeAnimation.size()) {
			this.activeFrame ++;
		}
		
	}
	
	public void draw(Graphics g, Vector pos) {     

		//draw sprite
		if(this.activeFrame / frameInterval < this.activeAnimation.size()) {
			
			double imageWidth = (GameManager.tileSize * MeleeAttack.slashImageWidth);
			double imageHeight = (GameManager.tileSize * MeleeAttack.slashImageHeight);
			
			g.drawImage(this.activeAnimation.get(activeFrame / frameInterval), 
							(int) ((pos.x + this.activeAnimationOffset.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2) - (int) imageWidth / 2,
							(int) ((pos.y + this.activeAnimationOffset.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2) - (int) imageHeight / 2,
							(int) (imageWidth), (int) (imageHeight), null);
		}
		
		//this.drawHitboxes(g, pos);
		
	}
	
	public void drawHitboxes(Graphics g, Vector pos) {
		if(this.attacking) {
			//draw hitboxes
			for(int j = 0; j < activeHitboxes.length; j++) {
				for(int i = 0; i < 4; i++) {
					Point a = activeHitboxes[j].corners[i]; 
					Point b = activeHitboxes[j].corners[(i + 1) % 4];
					//System.out.println(a.x + " " + a.y);
					g.setColor(Color.black);
					g.drawLine(
							(int) ((a.x + pos.x + activeHitboxes[j].offset.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2), 
							(int) ((a.y + pos.y + activeHitboxes[j].offset.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2), 
							(int) ((b.x + pos.x + activeHitboxes[j].offset.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2), 
							(int) ((b.y + pos.y + activeHitboxes[j].offset.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
			}
			
			
		}
	}
	
	public static void loadTextures() {
		
		int width = 52;	//w and h of one frame of the sprite
		int height = 48;
		
		BufferedImage a = GraphicsTools.loadImage("/Textures/MeleeAttacks/sword_slash.png");
		
		int animationLength = a.getHeight() / height;
		slashAnimation = new ArrayList<BufferedImage>();
		
		for(int i = 0; i < animationLength; i++) {
			BufferedImage next = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = next.getGraphics();
			g.drawImage(a, 0, -(i * height), null);
			slashAnimation.add(next);
		}
		System.out.println(slashAnimation.size() + " FRAMES LOADED FOR SWORD SLASH");
		
	}
	
	public void Slash(Vector pos, Point mouse) {
		
		this.attacking = true;
		this.attackTimeLeft = slashTime;
		this.activeFrame = 0;
		
		Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
		
		this.activeAttackVector = new Vector(center, new Point(mouse.x, mouse.y));
		this.activeAttackVector.normalize();	this.activeAttackVector.setMagnitude(0.5);
		
		//System.out.println(toMouse.x + " " + toMouse.y);
		
		double rads = Math.atan2(activeAttackVector.y, activeAttackVector.x);
		
		//System.out.println(rads);
		
		Hitbox[] attack = new Hitbox[6];
		
		attack[0] = new Hitbox(0.5, 0.5);	attack[0].offset = new Vector(1.8, 0.8);
		attack[1] = new Hitbox(0.5, 0.5);	attack[1].offset = new Vector(1.9, 0.4);
		attack[2] = new Hitbox(0.5, 0.5);	attack[2].offset = new Vector(2, -0.0);
		attack[3] = new Hitbox(0.5, 0.5);	attack[3].offset = new Vector(1.9, -0.4);
		attack[4] = new Hitbox(0.5, 0.5);	attack[4].offset = new Vector(1.8, -0.8);
		
		attack[5] = new Hitbox(1, 1.5);	attack[5].offset = new Vector(1, -0.0);
		
		for(int i = 0; i < attack.length; i++) {
			attack[i].offset.rotateCounterClockwise(rads);
		}
		
		ArrayList<BufferedImage> animation = new ArrayList<BufferedImage>();
		for(BufferedImage b : slashAnimation) {
			animation.add(GraphicsTools.rotateImageByDegrees(b, Math.toDegrees(rads - Math.PI)));
		}
		
		this.activeHitboxes = attack;
		this.activeAnimation = animation;
		this.activeAnimationOffset = new Vector(slashOffset);	this.activeAnimationOffset.rotateCounterClockwise(rads);
		
	}
	
	
	
}
