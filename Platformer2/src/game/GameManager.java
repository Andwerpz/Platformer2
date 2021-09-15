package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import button.ButtonManager;
import entities.Coin;
import entities.Enemy;
import entities.Entity;
import entities.Hitbox;
import entities.Player;
import entities.Slime;
import main.MainPanel;
import melee.MeleeAttack;
import particles.DamageNumber;
import particles.Particle;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class GameManager {
	
	public static ButtonManager bm;
	
	public static double tileSize = 20;	//controls the scale of the entire game. Could be useful in a zoom function

	public Map map;
	
	public Player player;
	
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Particle> particles;
	public static ArrayList<Coin> coins;
	
	public static Vector cameraOffset = new Vector(0, 0);	//or alternatively, you could just use inverse of the player position
	
	public BufferedImage nearBackground;
	public BufferedImage farBackground;
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	public static boolean pause = false;
	
	public static int gold = 0;
	
	public boolean wavePause = true;	//the pause between waves
	//during the pause, a big "Wave x" should display
	
	public int wavePauseTime;
	public double wavePopupOpacity = 1d;
	
	public GameManager() {
		map = new Map();
		map.readFromFile("crucible with waves.txt");
		
		map.calculateMapLight();
		
		player = new Player(mouse, new Vector(50, 50));
		
		//loading all entity textures
		//should move this somewhere else, but this works for now
		MeleeAttack.loadTextures();
		Coin.loadTextures();
		Slime.loadTextures();
		
		GameManager.enemies = new ArrayList<Enemy>();
		
		GameManager.particles = new ArrayList<Particle>();
		GameManager.coins = new ArrayList<Coin>();
		
		farBackground = GraphicsTools.loadImage("/Textures/pixel mountain background.png");
		nearBackground = GraphicsTools.loadImage("/Textures/pixel meadow background.png");
		
		Point playerPos = new Point((player.pos.x + player.envHitbox.width / 2) * tileSize, (player.pos.y + player.envHitbox.length / 2) * tileSize - (MainPanel.HEIGHT / 2 - MainPanel.HEIGHT / 3));	
		GameManager.cameraOffset = new Vector(new Point(0, 0), playerPos);
		
		this.wavePauseTime = 360;
	}
	
	public void tick() {}
	
	public void draw(Graphics g, java.awt.Point mouse) {
		
		if(!pause) {
			//spawn new enemies
			if(GameManager.enemies.size() == 0 && !this.wavePause) {
				this.wavePause = true;
				this.wavePauseTime = 360;
				//this.map.spawnNextWave();
			}
			
			if(wavePause) {
				this.wavePauseTime --;
				if(this.wavePauseTime <= 0) {
					this.wavePause = false;
					this.map.spawnNextWave();
				}
			}
			
			
			
			//calculate new camera offset
			player.mouse = mouse;
			player.tick(map);
			Point playerPos = new Point((player.pos.x + player.envHitbox.width / 2) * tileSize, (player.pos.y + player.envHitbox.length / 2) * tileSize - (MainPanel.HEIGHT / 2 - MainPanel.HEIGHT / 3));	
			Vector nextCameraOffset = new Vector(new Point(0, 0), playerPos);
			Vector oldToNew = new Vector(new Point(GameManager.cameraOffset), new Point(nextCameraOffset));
			oldToNew.multiply(0.15);	//the tracking speed of the camera
			GameManager.cameraOffset.addVector(oldToNew);
			//the point at where the camera will be centered around
			//enable this if you want the camera to lock onto the player
			//GameManager.cameraOffset = new Vector(new Point(0, 0), playerPos);	//vector from player location to center of screen
			
			//move, and get rid of dead enemies
			for(int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if(e.health <= 0) {
					for(int j = 0; j < 3; j++) {
						GameManager.coins.add(new Coin(e.pos));
					}
					
					enemies.remove(i);
					i --;
					continue;
				}
				
				e.tick(map);
			}
			
			//move, and get rid of coins
			for(int i = 0; i < coins.size(); i++) {
				Coin c = coins.get(i);
				if(c.envHitbox.collision(c.pos, player.envHitbox, player.pos)) {
					coins.remove(i);
					GameManager.gold ++;
					i --;
				}
				
				c.tick(map);
			}
			
			//move, and get rid of expired particles
			for(int i = 0; i < particles.size(); i++) {
				//System.out.println(particles.get(i).width);
				if(particles.get(i).timeLeft < 0) {
					particles.remove(i);
					i --;
					continue;
				}
				particles.get(i).tick(map);
			}
			
			//enemies hitting player. Contact or bullets
			for(Enemy e : GameManager.enemies) {
				if(e.damageOnContact) {
					player.hit(e.envHitbox, e.pos, e.contactDamage);
				}
				
			}
			
			//if the player is attacking 
			boolean hit = false;
			if(player.ma.attacking) {
				for(Enemy e : GameManager.enemies) {
					
					if(e.hit(player.ma, player.pos, (int) (Math.random() * 9) + 1)) {
						hit = true;
					}
	
				}
			}
			if(hit) {
	//			if(!player.onGround) {
	//				player.vel.y = -player.jumpVel;
	//			}
			}
		}
		
		g.setColor(new Color(103, 199, 239));
		g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		
		//draw background
		int farBackgroundOffset = (int) -(player.pos.x % farBackground.getWidth());
		int farHeight = MainPanel.HEIGHT / 2;
		int farVerticalOffset = MainPanel.HEIGHT / 4 + ((int) (-player.pos.y / 5d));
		
		int nearBackgroundOffset = (int) -((player.pos.x * 2d) % nearBackground.getWidth());
		int nearHeight = MainPanel.HEIGHT / 2;
		int nearVerticalOffset = (MainPanel.HEIGHT / 3) * 2 + ((int) (-player.pos.y / 2d));
		
		g.drawImage(farBackground, farBackgroundOffset, farVerticalOffset, (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farHeight, null);
		g.drawImage(farBackground, farBackgroundOffset + (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farVerticalOffset, (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farHeight, null);
		g.drawImage(nearBackground, nearBackgroundOffset, nearVerticalOffset, (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearHeight, null);
		g.drawImage(nearBackground, nearBackgroundOffset + (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearVerticalOffset, (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearHeight, null);
		
		
		for(Coin c : coins) {
			c.draw(g);
		}
		
		for(Enemy e : enemies) {
			e.draw(g);
		}
		
		player.draw(g);
		
		for(Particle p : particles) {
			p.draw(g);
		}
		
		map.draw(g);
		
		//drawing hud
		g.setFont(new Font("Dialogue", Font.BOLD, 12));
		if(player.immune) {
			g.setColor(Color.RED);
		}
		else {
			g.setColor(Color.black);
		}
		g.drawString("Health: " + player.health, 20, 20);
		
		g.setColor(Color.black);
		g.drawString("Gold: " + GameManager.gold, 20, 40);
		
		g.drawString("Wave: " + (this.map.selectedWave), 20, 60);
		
		
		if(this.wavePause) {
			String popup = "Wave " + (this.map.selectedWave + 1);
			Font popupFont = new Font("Georgia", Font.BOLD, 48);
			
			int popupWidth = GraphicsTools.calculateTextWidth(popup, popupFont);
			
			this.wavePopupOpacity = ((double) this.wavePauseTime / (double) 360);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(popupFont);
			g2.setComposite(GraphicsTools.makeComposite(this.wavePopupOpacity));
			g2.drawString(popup, MainPanel.WIDTH / 2 - popupWidth / 2, MainPanel.HEIGHT / 4);
		}
		
		
		//gray pause overlay
		if(pause) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(GraphicsTools.makeComposite(0.25));
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		}
		
	}
	
	public void mousePressed(MouseEvent arg0) {
		if(!pause) {
			player.mousePressed(arg0);
		}
		else {
			
		}
	}
	
	public void keyTyped(int k) {
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ESCAPE) {
			GameManager.pause = !GameManager.pause;
		}
		if(!pause) {
			player.keyPressed(k);
		}
		
	}
	
	public void keyReleased(int k) {
		if(!pause) {
			player.keyReleased(k);
		}
		
	}
}
