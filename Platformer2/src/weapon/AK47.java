package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import projectiles.Bullet;
import projectiles.LargeExplosion;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class AK47 extends Weapon {
	
	public static double bulletSize = 0.6;
	public static double bulletVel = 0.8;
	public static int bulletDamage = 4;
	public static double bulletSpread = 5;	//in degrees;
	public static double bulletVelSpread = 0.1;

	public AK47(Vector pos) {
		super(pos);
		
		this.attackStaminaCost = 2;
		this.attackDelay = 4;
		this.id = 2;
	}
	
	public static void loadAnimations() {
		AK47_Bullet.animation = GraphicsTools.loadAnimation("/Textures/Bullets/shotgun pellet.png", 5, 5);
	}

	@Override
	public ArrayList<Bullet> getBullets(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		
		return new ArrayList<Bullet>(Arrays.asList(new AK47_Bullet(nextPos, attackDir)));
	}

}

class AK47_Bullet extends Bullet {
	
	public static ArrayList<BufferedImage> animation;

	public AK47_Bullet(Vector pos, Vector vel) {
		super(pos, vel, AK47.bulletSize, AK47.bulletSize, AK47.bulletDamage);
		this.vel.setMagnitude(AK47.bulletVel + Math.random() * AK47.bulletVelSpread);
		this.vel.rotateCounterClockwise(Math.random() * Math.toRadians(AK47.bulletSpread) - Math.toRadians(AK47.bulletSpread / 2d));
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
