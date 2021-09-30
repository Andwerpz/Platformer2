package projectiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import state.GameManager;
import util.Vector;

public class Bullet extends Projectile{
	
	//this is for all traditional projectiles.
	//these settings will be configured by the weapon class that uses the projectile
	
	public boolean dieOnHit = true;	//does this projectile delete itself when hitting something
	
	public boolean newProjOnHit = false;	//makes a ring of new projectiles around this one when it hits something
	public int newProjAmt;	//how many new projectiles does this one make
	public int newProjShape;	//determines the formation that the new projectiles take up. TODO
	
	public boolean explodeOnHit = false;	//makes new projectile that is of explosion class
	
	public ArrayList<BufferedImage> animation;

	public Bullet(Vector pos, Vector vel, double width, double height, int damage) {
		super(pos, vel, width, height, damage);
		this.gravity = false;
		this.frictionInAir = false;
		this.timeLeft = 180;
	}
	
	@Override
	public void tick(Map map) {
		this.move(map);
		if(this.envCollision) {
			this.hit();
		}
		this.timeLeft --;
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}

	@Override
	public void hit() {
		if(this.dieOnHit) {
			this.timeLeft = -1;
		}
		
		if(this.newProjOnHit) {
			Vector vec = new Vector(0, 0.2);
			
			for(int i = 0; i < this.newProjAmt; i++) {
				GameManager.projectiles.add(new SmallBullet(this.pos, vec, 5));
				vec.rotateCounterClockwise(Math.PI * 2 / ((double) newProjAmt));
			}
		}
		
		if(this.explodeOnHit) {
			
		}
		
		
		
	}

}
