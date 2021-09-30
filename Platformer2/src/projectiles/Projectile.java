package projectiles;

import java.awt.Graphics;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import util.Vector;

public abstract class Projectile extends Entity{
	
	//I think the name "Projectile" for this class is a little bit misleading. Use this if you want something to deal damage in the world to enemies.
	//they can be stationary hazards, or explosions. 
	
	public int timeLeft;	//how many ticks does this projectile have left. if this value is less than 0, then this projectile is going to be deleted.
	public int damage;	//amount of damage this projectile deals

	public Projectile(Vector pos, Vector vel, double width, double height, int damage) {
		this.pos = new Vector(pos);
		this.vel = new Vector(vel);
		
		this.envHitbox = new Hitbox(width, height);
		this.damage = damage;
		
		this.maxHorizontalSpeed = 1;
		this.maxVerticalSpeed = 1;
		
		this.width = width;
		this.height = height;
	}
	
	public abstract void tick(Map map);
	public abstract void draw(Graphics g);
	public abstract void hit();	//what does this projectile do on contact with something. does it explode? does it just die? does it spawn tons of particles?
	public abstract void timeOut();	//what does this projectile do when it reaches the end of its lifetime?
	
}
