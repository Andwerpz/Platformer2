package weapon;

import java.awt.Graphics;

import game.Map;
import item.Item;
import util.Vector;

public abstract class Weapon extends Item {
	
	//put all the animations here
	
	public int attackStaminaCost = 30;
	public boolean inInventory = false;	//if not in the inventory, then we'll draw and tick it like normal
	
	public Weapon(Vector pos) {
		this.pos = new Vector(pos);
		this.width = 0.7;
		this.height = 0.7;
	}
	
	public abstract void attack(Vector pos, Vector attackDir);	//create the bullets that are fired from the attack
	
	public void tick(Map map) {
		if(!this.inInventory) {
			this.move(map);
		}
	}
	
	public void draw(Graphics g) {
		if(!this.inInventory) {
			this.drawHitboxes(g);
		}
	}

}
