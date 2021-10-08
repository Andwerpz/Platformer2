package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import main.MainPanel;
import projectiles.Bullet;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class FamilyHeirloom extends Weapon{
	
	public static double bulletSize = 0.6;
	public static double bulletVel = 0.8;
	public static int bulletDamage = 5;
	public static double bulletSpread = 6;

	public FamilyHeirloom(Vector pos) {
		super(pos);
		
		this.attackStaminaCost = 5;
		this.attackDelay = 15;
		
		this.id = 3;
	}
	
	public static void loadAnimations() {
		FamilyHeirloom_Bullet.sprite = GraphicsTools.loadImage("/Textures/Bullets/bullet long small.png");
	}

	@Override
	public ArrayList<Bullet> getBullets(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		
		return new ArrayList<Bullet>(Arrays.asList(new FamilyHeirloom_Bullet(nextPos, attackDir)));
	}

}


class FamilyHeirloom_Bullet extends Bullet{
	
	public static BufferedImage sprite;

	public FamilyHeirloom_Bullet(Vector pos, Vector vel) {
		super(pos, vel, FamilyHeirloom.bulletSize, FamilyHeirloom.bulletSize, FamilyHeirloom.bulletDamage);
		
		this.vel.setMagnitude(FamilyHeirloom.bulletVel);
		this.vel.rotateCounterClockwise(Math.toRadians(Math.random() * FamilyHeirloom.bulletSpread - FamilyHeirloom.bulletSpread / 2d));
		
		this.dieOnHit = true;
	}
	
	@Override
	public void draw(Graphics g) {
		//System.out.println(this.width);
		this.drawPointAtSprite(FamilyHeirloom_Bullet.sprite, g, this.vel);
		//this.drawRotatedSprite(sprite, g, 1);
		//this.drawHitboxes(g);
	}
	
	
}