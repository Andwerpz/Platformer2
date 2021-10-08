package enemy;

import java.awt.Color;
import java.awt.Graphics;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import main.MainPanel;
import melee.MeleeAttack;
import particles.DamageNumber;
import projectiles.Explosion;
import projectiles.Projectile;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Enemy extends Entity{
	
	public static final int SLIME = 1;
	public static final int GOBLIN = 2;

	public boolean immune = false;
	public boolean immuneTimeLeft;
	
	public boolean damageOnContact = false;	//if the player touches this env hitbox, then it gets damaged
	public int contactDamage;
	
	public int health;
	public int maxHealth;

	public double maxHealthBarWidth = 3;
	public double healthBarWidth = 3;
	
	public double healthBarHeight = 0.3;
	
	public boolean onFire = false;	//if is on fire, then enemy will take damage every second proportional to the players current fire buff level
	public boolean poisoned = false;	//if is poisoned, then enemy will take percentage max health damage. If damage is less than 1, then it is rounded up to 1.
	public boolean slowed = false;	//if slowed, then friction will be increased for the enemy, and other movement related things will be slowed.
	public boolean frozen = false;	//if frozen, then the enemy will stop being ticked. Damage will still register though.
	
	public static Enemy getEnemy(int type, Vector pos) {
		switch(type) {
		case SLIME:
			return new Slime(pos);
			
		case GOBLIN:
			return new Goblin(pos);
		}
		
		return null;
	}
	
	public static void loadTextures() {
		Slime.animationIdle = GraphicsTools.loadAnimation("/Textures/Slime/slime_idle.png", 13, 13);
	}

	//returns true if the attack hit it
	public boolean hit(Projectile p) {
		boolean attackHit = false;
		
		if(this.envHitbox.collision(this.pos, p.envHitbox, p.pos)) {
			
			boolean crit = Math.random() < GameManager.player.critChance;
			int damage = crit? p.damage * GameManager.player.critMultiplier : p.damage;
			
			Vector impulseVector = new Vector(0, 0);
			
			if(p instanceof Explosion) {
				impulseVector = new Vector(this.pos.x - p.pos.x, this.pos.y - p.pos.y);
				impulseVector.setMagnitude(0.5);
			}
			
			else {
				Vector attackVector = new Vector(p.vel);
				double xImpulse = attackVector.x > 0? 0.2 : -0.2;
				impulseVector = new Vector(xImpulse, 0.2);
			}
			
			this.vel.addVector(impulseVector);
			this.pos.y -= this.cushion * 2;
			attackHit = true;
			
			this.health -= damage;
			
			//add a new damage number particle
			GameManager.particles.add(new DamageNumber(damage, this.pos, crit));
			
			//sets up immune frames
			//maybe enemies don't need immunity frames.
			
		}
		
		return attackHit;
	}
	
	public void drawHealthBar(Graphics g) {
		
		this.healthBarWidth += (((double) this.health / (double) this.maxHealth) * this.maxHealthBarWidth - this.healthBarWidth) * 0.1; 
		
		int healthBarWidth = (int) (this.healthBarWidth * GameManager.tileSize);
		int healthBarHeight = (int) (this.healthBarHeight * GameManager.tileSize);
		
		int red = (int) (255d - 255d * ((double) (this.health - (double)this.maxHealth / 2d) / ((double) this.maxHealth / 2d)));
		int green = 0;
		
		if((double) this.health > (double) this.maxHealth / 2d) {
			green = 255;
		}
		else {
			green = (int) (255d * ((double) this.health / ((double) this.maxHealth / 2d)));
		}
		
		red = Math.max(red, 0);
		green = Math.max(green, 0);
		
		red = Math.min(red, 255);
		green = Math.min(green, 255);
		
		if(green == 0) {
			green = 255;
		}
		
		//System.out.println(red + " " + green);
		
		g.setColor(new Color(red, green, 0));
		
		g.fillRect((int) ((this.pos.x) * GameManager.tileSize - (this.maxHealthBarWidth * GameManager.tileSize) / 2 - GameManager.cameraOffset.x + MainPanel.WIDTH / 2), 
				(int) ((this.pos.y - this.height / 2 - 0.5) * GameManager.tileSize - healthBarHeight - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2), 
				healthBarWidth, healthBarHeight);
		
//		g.drawRect((int) ((this.pos.x) * GameManager.tileSize - healthBarWidth / 2 - GameManager.cameraOffset.x + MainPanel.WIDTH / 2), 
//				(int) ((this.pos.y - this.height / 2 - 0.5) * GameManager.tileSize - healthBarHeight - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2), 
//				healthBarWidth, healthBarHeight);
		
		
	}

}
