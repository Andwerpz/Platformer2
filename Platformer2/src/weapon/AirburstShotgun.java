package weapon;

import java.awt.Graphics;

import game.Map;
import projectiles.Bullet;
import state.GameManager;
import util.Vector;

public class AirburstShotgun extends Weapon {
	
	public static double slugVel = 0.3;
	public static double bulletVel = 0.6;
	
	public static int slugDamage = 8;
	public static int bulletDamage = 4;
	
	public static double slugSize = 0.9;
	public static double bulletSize = 0.3;
	
	public AirburstShotgun(Vector pos) {
		super(pos);
		this.attackStaminaCost = 30;
	}

	@Override
	public void attack(Vector pos, Vector attackDir) {
		
		attackDir.setMagnitude(slugVel);
		
		GameManager.projectiles.add(new AirburstShotgun_Slug(pos, attackDir, slugDamage));
		
	}

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}

}

class AirburstShotgun_Slug extends Bullet {
	
	//this bullet is the one that will initially fire out of the gun
	
	public int numNewBullets = 5;
	public double spreadDegrees = 6;

	public AirburstShotgun_Slug(Vector pos, Vector vel, int damage) {
		super(pos, vel, AirburstShotgun.slugSize, AirburstShotgun.slugSize, damage);
		this.timeLeft = 20;
		
		this.dieOnHit = true;
		this.frictionInAir = false;
		this.explodeOnHit = false;
		this.newProjOnHit = false;
	}
	
	public void timeOut() {
		//spawn new bullets
		for(int i = 0; i < numNewBullets; i++) {
			Vector vec = new Vector(vel);
			vec.setMagnitude(AirburstShotgun.bulletVel + Math.random() * 0.05);
			vec.rotateCounterClockwise(Math.toRadians((Math.random() * spreadDegrees) - spreadDegrees / 2d));
			GameManager.projectiles.add(new AirburstShotgun_Bullet(this.pos, vec, AirburstShotgun.bulletDamage));
		}
	}
	
}

class AirburstShotgun_Bullet extends Bullet {
	
	//this bullet is the one that will come from the explosion of the slug

	public AirburstShotgun_Bullet(Vector pos, Vector vel, int damage) {
		super(pos, vel, AirburstShotgun.bulletSize, AirburstShotgun.bulletSize, damage);
		
		this.dieOnHit = true;
		this.frictionInAir = false;
		this.explodeOnHit = false;
		this.newProjOnHit = false;
	}
	
}