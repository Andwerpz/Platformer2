package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import particles.Smoke;
import projectiles.Bullet;
import projectiles.LargeExplosion;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class RocketLauncher extends Weapon {
	
	public static double bulletSize = 0.5;
	public static double bulletVel = 0.3;
	public static int bulletDamage = 20;
	public static int explosionDamage = 10;
	public static double bulletSpread = 2;	//in degrees;
	public static double bulletVelSpread = 0.03;
	
	public RocketLauncher(Vector pos) {
		super(pos);
		// TODO Auto-generated constructor stub
		
		this.id = 5;
	}

	@Override
	public void attack(Vector pos, Vector attackDir) {
		Vector nextPos = new Vector(pos);
		attackDir.setMagnitude(2.5);
		nextPos.addVector(attackDir);
		GameManager.projectiles.add(new RocketLauncher_Rocket(pos, attackDir));
	}
	
	public static void loadAnimations() {
		RocketLauncher_Rocket.animation = GraphicsTools.loadAnimation("/Textures/Bullets/pixel missile small.png", 32, 15);
	}

}

class RocketLauncher_Rocket extends Bullet {
	
	public static ArrayList<BufferedImage> animation;
	
	public static double smokeVel = 0.15;

	public RocketLauncher_Rocket(Vector pos, Vector vel) {
		super(pos, vel, RocketLauncher.bulletSize, RocketLauncher.bulletSize, RocketLauncher.bulletDamage);
		this.vel.setMagnitude(RocketLauncher.bulletVel + Math.random() * RocketLauncher.bulletVelSpread);
		this.vel.rotateCounterClockwise(Math.random() * Math.toRadians(RocketLauncher.bulletSpread) - Math.toRadians(RocketLauncher.bulletSpread / 2d));
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawPointAtSprite(animation.get(0), g, this.vel, 2.2, 1);
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
		this.move(map);
		if(this.envCollision) {
			this.hit();
		}
		this.timeLeft --;
		GameManager.particles.add(new Smoke(this.pos, new Vector(Math.random() * smokeVel - smokeVel / 2d, Math.random() * smokeVel - smokeVel / 2d), Math.random() * 0.3 + 0.3));
	}
	
}
