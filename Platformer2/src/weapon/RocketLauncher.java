package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import game.Map;
import particles.Smoke;
import projectiles.Bullet;
import projectiles.LargeExplosion;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class RocketLauncher extends Weapon {
	
	public static double bulletSize = 0.5;
	public static double bulletStartVel = 0.1;
	public static double bulletMaxVel = 0.8;
	public static int bulletDamage = 20;
	public static int explosionDamage = 10;
	public static double bulletSpread = 2;	//in degrees;
	public static double bulletVelSpread = 0.03;
	
	public RocketLauncher(Vector pos) {
		super(pos);
		// TODO Auto-generated constructor stub
		
		this.id = 5;
	}
	
	public static void loadAnimations() {
		RocketLauncher_Rocket.animation = GraphicsTools.loadAnimation("/Textures/Bullets/pixel missile small.png", 32, 15);
	}

	@Override
	public ArrayList<Bullet> getBullets(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		
		return new ArrayList<Bullet>(Arrays.asList(new RocketLauncher_Rocket(pos, attackDir)));
	}

}

class RocketLauncher_Rocket extends Bullet {
	
	public static ArrayList<BufferedImage> animation;
	
	public static double smokeVel = 0.15;

	public RocketLauncher_Rocket(Vector pos, Vector vel) {
		super(pos, vel, RocketLauncher.bulletSize, RocketLauncher.bulletSize, RocketLauncher.bulletDamage);
		this.vel.setMagnitude(RocketLauncher.bulletStartVel + Math.random() * RocketLauncher.bulletVelSpread);
		this.vel.rotateCounterClockwise(Math.random() * Math.toRadians(RocketLauncher.bulletSpread) - Math.toRadians(RocketLauncher.bulletSpread / 2d));
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawPointAtSprite(animation.get(0), g, this.vel, 1.1, 0.5);
	}
	
	@Override
	public void hit(){
		this.timeLeft = -1;
		GameManager.projectiles.add(new LargeExplosion(this.pos, RocketLauncher.explosionDamage));
	}
	
	@Override
	public void timeOut() {
		GameManager.projectiles.add(new LargeExplosion(this.pos, RocketLauncher.explosionDamage));
	}
	
	@Override
	public void tick(Map map) {
		if(this.vel.getMagnitude() < RocketLauncher.bulletMaxVel) {
			this.vel.setMagnitude(this.vel.getMagnitude() + 0.01);
		}
		this.move(map);
		if(this.envCollision) {
			this.hit();
		}
		this.timeLeft --;
		
		Vector smokePos = new Vector(this.pos);
		Vector smokeOffset = new Vector(this.vel);
		smokeOffset.setMagnitude(-0.6);
		smokePos.addVector(smokeOffset);
		GameManager.particles.add(new Smoke(smokePos, new Vector(Math.random() * smokeVel - smokeVel / 2d, Math.random() * smokeVel - smokeVel / 2d), Math.random() * 0.3 + 0.3));
	}
	
}
