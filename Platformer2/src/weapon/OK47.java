package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import projectiles.Bullet;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class OK47 extends Weapon {
	
	public static double bulletSize = 0.4;
	public static double bulletVel = 0.6;
	public static int bulletDamage = 2;
	public static double bulletSpread = 10;	//in degrees;
	public static double bulletVelSpread = 0.2;

	public OK47(Vector pos) {
		super(pos);
		
		this.attackStaminaCost = 2;
		this.attackDelay = 6;
		this.id = 1;
	}

	@Override
	public void attack(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		GameManager.projectiles.add(new OK47_Bullet(nextPos, attackDir));
	}
	
	public static void loadAnimations() {
		OK47_Bullet.animation = GraphicsTools.loadAnimation("/Textures/Bullets/shotgun pellet.png", 5, 5);
	}

}

class OK47_Bullet extends Bullet {
	
	public static ArrayList<BufferedImage> animation;

	public OK47_Bullet(Vector pos, Vector vel) {
		super(pos, vel, OK47.bulletSize, OK47.bulletSize, OK47.bulletDamage);
		this.vel.setMagnitude(OK47.bulletVel + Math.random() * OK47.bulletVelSpread);
		this.vel.rotateCounterClockwise(Math.random() * Math.toRadians(OK47.bulletSpread) - Math.toRadians(OK47.bulletSpread / 2d));
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(0), g);
	}
	
}
