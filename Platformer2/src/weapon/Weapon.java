package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Hitbox;
import game.Map;
import item.Item;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Weapon extends Item {
	
	//rarities:
	//common	#ffffff	white	
	//uncommon	#37d700	green	
	//rare		#003ad7	blue
	//epic		#9f00d7	purple
	//legendary	#d7a600	gold
	
	
	//sprites, no animations for now
	public static ArrayList<BufferedImage> sprites;
	
	public int id;
	public int attackStaminaCost = 30;
	public int attackDelay = 20;
	
	public Weapon(Vector pos) {
		this.pos = new Vector(pos);
		this.width = 2;
		this.height = 2;
		this.envHitbox = new Hitbox(width, height);
	}
	
	public static Weapon getWeapon(int id, Vector pos) {
		switch(id) {
		case 0:
			return new AirburstShotgun(pos);
			
		case 1:
			return new OK47(pos);
			
		case 2:
			return new AK47(pos);
			
		case 3:
			return new FamilyHeirloom(pos);
			
		case 4:
			return new HuntingRifle(pos);
			
		case 5:
			return new RocketLauncher(pos);
			
		default:
			return new FamilyHeirloom(pos);	
		}
	}
	
	public static void loadAnimations() {
		AirburstShotgun.loadAnimations();
		OK47.loadAnimations();
		AK47.loadAnimations();
		FamilyHeirloom.loadAnimations();
		
		RocketLauncher.loadAnimations();
		
		Weapon.sprites = GraphicsTools.loadAnimation("/Textures/Weapons/weapon sprites.png", 32, 32);
	}
	
	public abstract void attack(Vector pos, Vector attackDir);	//create the bullets that are fired from the attack
	
	public void tick(Map map) {
		this.move(map);
	}
	
	public void draw(Graphics g) {
		if(Weapon.sprites.size() > this.id) {
			this.drawSprite(Weapon.sprites.get(this.id), g);
		}
		else {
			this.drawHitboxes(g);
		}
	}
	
	public void onDrop(Vector pos) {
		this.pos = new Vector(pos);
		this.pos.y -= 1;
		this.vel = new Vector(Math.random() * 0.3 - 0.15, -0.2);
		GameManager.items.add(this);
	}
	
	public void onPickup() {}

}
