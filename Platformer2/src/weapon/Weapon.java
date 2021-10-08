package weapon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Hitbox;
import game.Map;
import item.Item;
import player.Player;
import projectiles.Bullet;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public abstract class Weapon extends Item {
	
	//weapon elements;
	public static final int FIRE = 0;
	public static final int ICE = 1;
	public static final int ELECTRICITY = 2;
	public static final int POISON = 3;
	public static final int NATURE = 4;
	
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
		super(pos, new Vector(0, 0), 2, 2);
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
		HuntingRifle.loadAnimations();
		RocketLauncher.loadAnimations();
		
		Weapon.sprites = GraphicsTools.loadAnimation("/Textures/Weapons/weapon sprites.png", 32, 32);
	}
	
	public abstract ArrayList<Bullet> getBullets(Vector pos, Vector attackDir); //create the bullets that are fired from the attack
	
	public void attack(Vector pos, Vector attackDir) {
		ArrayList<Bullet> bullets = this.getBullets(pos, attackDir);
		for(Bullet b : bullets) {
			GameManager.projectiles.add(b);
		}
	}
	
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
		
		this.drawCostIndicator(g);
	}
	
	public void onDrop(Vector pos) {
		this.pos = new Vector(pos);
		this.pos.y -= 1;
		this.vel = new Vector(Math.random() * 0.3 - 0.15, -0.2);
		GameManager.items.add(this);
	}
	
	public void onPickup() {
		if(this.purchaseable) {
			if(GameManager.gold >= this.itemCost) {
				GameManager.gold -= this.itemCost;
				GameManager.player.swapWeapon(this);
				this.purchaseable = false;
			}
		}
		else {
			GameManager.player.swapWeapon(this);
		}
	}

}
