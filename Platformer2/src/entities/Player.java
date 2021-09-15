package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import game.GameManager;
import game.Map;
import main.Main;
import main.MainPanel;
import melee.MeleeAttack;
import particles.DamageNumber;
import util.Point;
import util.Vector;

public class Player extends Entity{
	
	public boolean left = false;
	public boolean right = false;
	public boolean jump = false;
	
	public boolean immune = false;
	public int immuneTimeLeft;
	public int immuneTime = 60;
	
	public MeleeAttack ma;
	public int attackCooldown = 30;
	public int attackTimer;
	
	public boolean mouseAttack = false;
	public boolean leftAttack = false;
	public boolean rightAttack = false;
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	public int health;
	public int maxHealth = 100;
	
	public Player(java.awt.Point mouse, Vector pos) {
		super();
		
		this.envHitbox = new Hitbox(0.7, 1.5);
		this.pos = new Vector(pos);
		
		this.ma = new MeleeAttack();
		
		this.immuneTimeLeft = 0;
		
		this.gravity = 0.03;
		this.jumpVel = 0.2;
		
		this.health = maxHealth;
		
		this.attackTimer = 0;
	}
	
	//takes in hitbox and position vector and checks whether the hitbox collides with the player
	//if yes, then calculate the x difference from the player to the position vector. 
	//The y vel change is constant, the x vel is dependent on the x difference.
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
		
		this.attackTimer --;
		
		if(this.jump) {
			this.gravity = 0.01;
		}
		else {
			this.gravity = 0.025;
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
		if(this.attackTimer <= 0 && (this.mouseAttack || this.leftAttack || this.rightAttack)) {
			this.attackTimer = this.attackCooldown;
			if(this.mouseAttack) {
				this.ma.Slash(pos, new Point(mouse.x, mouse.y));
			}
			else if(this.leftAttack) {
				this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 - 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
			}
			else {
				this.ma.Slash(pos, new Point((this.pos.x) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2 + 100, (this.pos.y) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2));
			}
			
		}
	}
	
	public void draw(Graphics g) {
		//ma.drawHitboxes(g, pos);
		ma.draw(g, pos);
		
		this.drawHitboxes(g);
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
