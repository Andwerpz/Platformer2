package weapon;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import main.MainPanel;
import projectiles.Bullet;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class AirburstShotgun extends Weapon {
	
	//uncommon
	
	public static ArrayList<BufferedImage> animation;
	public static ArrayList<BufferedImage> rarityAnimation;	//shows rarity color
	
	public static double slugVel = 0.3;
	public static double pelletVel = 0.8;
	
	public static int slugDamage = 8;
	public static int pelletDamage = 4;
	
	public static double slugSize = 0.9;
	public static double pelletSize = 0.3;
	
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
	
	public static void loadAnimations() {
		AirburstShotgun.animation = GraphicsTools.loadAnimation("/Textures/Weapons/airburst shotgun.png", 32, 32);
		AirburstShotgun.rarityAnimation = GraphicsTools.loadAnimation("/Textures/Weapons/airburst shotgun rarity.png", 32, 32);
		AirburstShotgun_Slug.animation = GraphicsTools.loadAnimation("/Textures/Bullets/shotgun slug.png", 10, 10);
		AirburstShotgun_Pellet.animation = GraphicsTools.loadAnimation("/Textures/Bullets/shotgun pellet.png", 5, 5);
	}

	@Override
	public void draw(Graphics g) {
		if(this.collision(GameManager.player)) {
			this.drawSprite(AirburstShotgun.rarityAnimation.get(0), g);
		}
		else {
			this.drawSprite(AirburstShotgun.animation.get(0), g);
		}
	}
	
}

class AirburstShotgun_Slug extends Bullet {
	
	public static ArrayList<BufferedImage> animation;
	
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
			vec.rotateCounterClockwise(Math.toRadians((Math.random() * spreadDegrees) - spreadDegrees / 2d));
			GameManager.projectiles.add(new AirburstShotgun_Pellet(this.pos, vec));
		}
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(0), g);
	}
	
}

class AirburstShotgun_Pellet extends Bullet {
	
	public static ArrayList<BufferedImage> animation;
	
	//this bullet is the one that will come from the explosion of the slug

	public AirburstShotgun_Pellet(Vector pos, Vector vel) {
		super(pos, vel, AirburstShotgun.pelletSize, AirburstShotgun.pelletSize, AirburstShotgun.pelletDamage);
		
		this.vel.setMagnitude(AirburstShotgun.pelletVel + Math.random() * 0.05);
		this.dieOnHit = true;
		this.frictionInAir = false;
		this.explodeOnHit = false;
		this.newProjOnHit = false;
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(0), g);
	}
	
}