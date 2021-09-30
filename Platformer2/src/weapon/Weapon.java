package weapon;

import java.awt.Graphics;

import entities.Hitbox;
import game.Map;
import item.Item;
import state.GameManager;
import util.Vector;

public abstract class Weapon extends Item {
	
	//put all the animations here
	
	//rarities:
	//common	#ffffff	white	
	//uncommon	#37d700	green	
	//rare		#003ad7	blue
	//epic		#9f00d7	purple
	//legendary	#d7a600	gold
	
	//just do the rarity color outline when the player is close enough to pick up the weapon
	
	public int attackStaminaCost = 30;
	public int attackDelay = 20;
	
	public Weapon(Vector pos) {
		this.pos = new Vector(pos);
		this.width = 2;
		this.height = 2;
		this.envHitbox = new Hitbox(width, height);
	}
	
	public static void loadAnimations() {
		AirburstShotgun.loadAnimations();
		OK47.loadAnimations();
	}
	
	public abstract void attack(Vector pos, Vector attackDir);	//create the bullets that are fired from the attack
	
	public void tick(Map map) {
		this.move(map);
	}
	
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}
	
	public void onDrop(Vector pos) {
		this.pos = new Vector(pos);
		this.pos.y -= 1;
		this.vel = new Vector(Math.random() * 0.3 - 0.15, -0.2);
		GameManager.items.add(this);
	}

}
