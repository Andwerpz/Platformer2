package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import projectiles.Bullet;
import projectiles.LargeExplosion;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class HuntingRifle extends Weapon {
	
	public static double bulletSize = 0.8;
	public static double bulletVel = 1;
	public static int bulletDamage = 10;
	public static double bulletSpread = 1;	//in degrees;
	public static double bulletVelSpread = 0.05;

	public HuntingRifle(Vector pos) {
		super(pos);
		
		this.attackStaminaCost = 10;
		this.attackDelay = 20;
		this.id = 4;
	}

	@Override
	public void attack(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		GameManager.projectiles.add(new HuntingRifle_Bullet(nextPos, attackDir));
	}
	
	public static void loadAnimations() {
		HuntingRifle_Bullet.animation = GraphicsTools.loadAnimation("/Textures/Bullets/shotgun pellet.png", 5, 5);
	}

}

class HuntingRifle_Bullet extends Bullet {
	
	public static ArrayList<BufferedImage> animation;

	public HuntingRifle_Bullet(Vector pos, Vector vel) {
		super(pos, vel, HuntingRifle.bulletSize, HuntingRifle.bulletSize, HuntingRifle.bulletDamage);
		this.vel.setMagnitude(HuntingRifle.bulletVel + Math.random() * HuntingRifle.bulletVelSpread);
		this.vel.rotateCounterClockwise(Math.random() * Math.toRadians(HuntingRifle.bulletSpread) - Math.toRadians(HuntingRifle.bulletSpread / 2d));
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(0), g);
	}
	
	@Override
	public void hit(){
		this.timeLeft = -1;
	}
	
}
