package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import item.Item;
import main.Main;
import main.MainPanel;
import melee.MeleeAttack;
import particles.DamageNumber;
import projectiles.Bullet;
import projectiles.SmallBullet;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;
import weapon.AK47;
import weapon.AirburstShotgun;
import weapon.FamilyHeirloom;
import weapon.OK47;
import weapon.RocketLauncher;
import weapon.Weapon;

public class Player extends Entity{
	
	public int baseHealthUpgrades;
	
	public static ArrayList<BufferedImage> animationIdle;
	public static int idleFrameInterval = 1;
	public int idleFrame = 0;
	
	public int health;
	public int maxHealth = 100;
	
	public int stamina;
	public int maxStamina = 100;
	public int staminaRegenDelay = 30;
	
	public boolean left = false;
	public boolean right = false;
	public boolean jump = false;
	
	public boolean immune = false;
	public int immuneTimeLeft;
	public int immuneTime = 30;
	
	public MeleeAttack ma;
	public int timeSinceLastAttack;
	
	public boolean mouseAttack = false;
	public boolean leftAttack = false;
	public boolean rightAttack = false;
	
	public boolean pickUpWeapon = false;
	
	public Weapon equippedWeapon = new RocketLauncher(new Vector(0, 0));
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	public Player(Vector pos) {
		super();
		
		this.width = 0.7;
		this.height = 1.5;
		this.envHitbox = new Hitbox(width, height);
		this.pos = new Vector(pos);
		
		this.ma = new MeleeAttack();
		
		this.immuneTimeLeft = 0;
		
		this.gravityAcceleration = 0.03;
		this.jumpVel = 0.2;
		
		this.health = maxHealth;
		this.stamina = maxStamina;
		
		this.timeSinceLastAttack = 0;
		
		this.baseHealthUpgrades = 0;
	}
	
	public static void loadTextures() {
		Player.animationIdle = GraphicsTools.loadAnimation("/Textures/Player/astolfo.png", 474, 520);
	}
	
	//takes in hitbox and position vector and checks whether the hitbox collides with the player
	//if yes, then calculate the x difference from the player to the position vector. 
	//The y vel change is constant, the direction of x is dependent on the x difference.
	public void hit(Hitbox h, Vector pos, int damage) {
		if(this.envHitbox.collision(this.pos, h, pos) && !this.immune) {
			
			this.vel.y = -0.2;
			this.vel.x = (this.pos.x - pos.x) < 0? -1 : 1;
			
			this.pos.y -= this.cushion * 2;
			
			this.immune = true;
			this.immuneTimeLeft = this.immuneTime;
			
			GameManager.particles.add(new DamageNumber(damage, this.pos));
			
			this.health -= damage;
			
		}
	}
	
	@Override
	public void tick(Map map) {
		
		this.timeSinceLastAttack ++;
		
		if(this.stamina < this.maxStamina && this.timeSinceLastAttack >= this.staminaRegenDelay) {
			this.stamina ++;
		}
		
		this.health = Math.min(this.health, this.maxHealth);
		
		if(this.jump) {
			this.gravityAcceleration = 0.01;
		}
		else {
			this.gravityAcceleration = 0.025;
		}
		
		if(this.immune) {
			this.immuneTimeLeft --;
			if(this.immuneTimeLeft < 0) {
				this.immune = false;
			}
		}
		
		this.attack();
		this.ma.tick();
		
		this.mouse = new java.awt.Point(mouse.x, mouse.y);

		if(this.left) {
			this.vel.x -= this.acceleration;
		}
		if(this.right) {
			this.vel.x += this.acceleration;
		}
		if(this.jump && this.onGround) {
			//System.out.println("JUMP");
			this.vel.y -= 0.5;
			this.pos.y -= this.cushion * 2;
		}
		this.move(map);
	}
	
	public void attack() {
		//equipped a gun
		if(this.equippedWeapon != null) {
			if(this.stamina >= this.equippedWeapon.attackStaminaCost && (this.mouseAttack || this.leftAttack || this.rightAttack) && this.timeSinceLastAttack >= this.equippedWeapon.attackDelay) {
				this.timeSinceLastAttack = 0;
				this.stamina -= this.equippedWeapon.attackStaminaCost;
				if(this.mouseAttack) {
					
					Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
					
					Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
					
					this.equippedWeapon.attack(this.pos, attackVector);
					//this.ma.Slash(pos, new Point(mouse.x, mouse.y));
				}
				else if(this.leftAttack) {
					this.equippedWeapon.attack(this.pos, new Vector(-1, 0));
					//this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 - 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				else {
					this.equippedWeapon.attack(this.pos, new Vector(1, 0));
					//this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 + 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				
			}
		}
		//do a melee attack
		else {
			if(this.stamina >= 10 && (this.mouseAttack || this.leftAttack || this.rightAttack) && this.timeSinceLastAttack >= 30) {
				this.timeSinceLastAttack = 0;
				this.stamina -= 10;
				if(this.mouseAttack) {
					
					Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
					
					Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
					
					//this.equippedWeapon.attack(this.pos, attackVector);
					this.ma.Slash(pos, new Point(mouse.x, mouse.y));
				}
				else if(this.leftAttack) {
					//this.equippedWeapon.attack(this.pos, new Vector(-1, 0));
					this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 - 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				else {
					//this.equippedWeapon.attack(this.pos, new Vector(1, 0));
					this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 + 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
				}
				
			}
		}
		
		
	}
	
	public void draw(Graphics g) {
		//ma.drawHitboxes(g, pos);
		ma.draw(g, pos);
		
		this.drawHitboxes(g);
		this.drawSprite(Player.animationIdle.get(0), g);
		
		//draw the currently equipped weapon
		BufferedImage wepImg = Weapon.sprites.get(this.equippedWeapon.id);
		Point center = new Point((pos.x) * GameManager.tileSize + MainPanel.WIDTH / 2 - GameManager.cameraOffset.x, (pos.y) * GameManager.tileSize + MainPanel.HEIGHT / 2 - GameManager.cameraOffset.y);
		
		Vector attackVector = new Vector(center, new Point(mouse.x, mouse.y));
		attackVector.setMagnitude(1.5);
		
		double rads = Math.atan2(attackVector.y, attackVector.x);
		
		//System.out.println(rads % (Math.PI * 2));
		if(rads < -Math.PI / 2d || rads > Math.PI / 2d) {
			wepImg = GraphicsTools.flipImageVertical(wepImg);
		}
		
		BufferedImage rotatedImg = GraphicsTools.rotateImageByDegrees(wepImg, Math.toDegrees((rads)));
		
		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
		double w = this.equippedWeapon.width;
	    double h = this.equippedWeapon.height;
	    double newWidth = w * cos + h * sin;
	    double newHeight = h * cos + w * sin;
		
		this.equippedWeapon.pos = new Vector(this.pos);
		this.equippedWeapon.pos.addVector(attackVector);
		
		this.equippedWeapon.drawSprite(rotatedImg, g, newWidth, newHeight);
	}
	
	public void swapWeapon(Weapon wep) {
		if(this.equippedWeapon != null) {
			this.equippedWeapon.onDrop(this.pos);
		}
		GameManager.items.remove(wep);
		this.equippedWeapon = wep;
	}
	
	public void mousePressed(MouseEvent arg0) {	
		this.mouseAttack = true;
		//this.attack(new Point(mouse.x, mouse.y));
	}
	
	public void mouseReleased() {
		this.mouseAttack = false;
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_A) {
			left = true;
		}
		else if(k == KeyEvent.VK_D){
			right = true;
		}
		else if(k == KeyEvent.VK_SPACE) {
			jump = true;
		}
		else if(k == KeyEvent.VK_LEFT) {
			leftAttack = true;
		}
		else if(k == KeyEvent.VK_RIGHT) {
			rightAttack = true;
		}
		else if(k == KeyEvent.VK_E) {
			for(int i = 0; i < GameManager.items.size(); i++) {
				Item item = GameManager.items.get(i);
				if(item instanceof Weapon && item.collision(this)) {
					Weapon wep = (Weapon) item;
					wep.onPickup();
					break;
				}
			}
		}
	}
	
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_A) {
			left = false;
		}
		else if(k == KeyEvent.VK_D){
			right = false;
		}
		else if(k == KeyEvent.VK_SPACE) {
			jump = false;
		}
		else if(k == KeyEvent.VK_LEFT) {
			leftAttack = false;
		}
		else if(k == KeyEvent.VK_RIGHT) {
			rightAttack = false;
		}
	}
	
}
