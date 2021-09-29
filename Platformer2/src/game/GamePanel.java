package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import decorations.Decoration;
import enemy.Enemy;
import entities.Player;
import item.Coin;
import item.Item;
import main.MainPanel;
import particles.Particle;
import state.GameManager;
import state.HubState;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class GamePanel {

public Map map;
	
	public BufferedImage nearBackground;
	public BufferedImage farBackground;
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	public boolean pause = false;	//if buttons do show up, then they should be drawn in the state class

	public GamePanel(String mapfile) {
		map = new Map();
		map.readFromFile(mapfile);
		
		map.calculateMapLight();
		
		Vector playerSpawn = map.playerSpawn;
		GameManager.player = new Player(playerSpawn);
		
		GameManager.enemies = new ArrayList<Enemy>();
		GameManager.particles = new ArrayList<Particle>();
		GameManager.items = new ArrayList<Item>();
		GameManager.decorations = new ArrayList<Decoration>();
		
		GameManager.decorations = this.map.getDecorations();
		
		farBackground = GraphicsTools.loadImage("/Textures/pixel mountain background.png");
		nearBackground = GraphicsTools.loadImage("/Textures/pixel meadow background.png");
		
		Point playerPos = new Point((GameManager.player.pos.x + GameManager.player.envHitbox.width / 2) * GameManager.tileSize, (GameManager.player.pos.y + GameManager.player.envHitbox.length / 2) * GameManager.tileSize - (MainPanel.HEIGHT / 2 - MainPanel.HEIGHT / 3));	
		GameManager.cameraOffset = new Vector(new Point(0, 0), playerPos);
		
	}


	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void tick(java.awt.Point mouse2) {}


	public void draw(Graphics g, java.awt.Point mouse) {
		
		if(!pause) {
			
			//calculate new camera offset
			GameManager.player.mouse = mouse;
			GameManager.player.tick(map);
			Point playerPos = new Point((GameManager.player.pos.x + GameManager.player.envHitbox.width / 2) * GameManager.tileSize, (GameManager.player.pos.y + GameManager.player.envHitbox.length / 2) * GameManager.tileSize - (MainPanel.HEIGHT / 2 - MainPanel.HEIGHT / 3));	
			Vector nextCameraOffset = new Vector(new Point(0, 0), playerPos);
			Vector oldToNew = new Vector(new Point(GameManager.cameraOffset), new Point(nextCameraOffset));
			oldToNew.multiply(0.15);	//the tracking speed of the camera
			GameManager.cameraOffset.addVector(oldToNew);
			//the point at where the camera will be centered around
			//enable this if you want the camera to lock onto the player
			//GameManager.cameraOffset = new Vector(new Point(0, 0), playerPos);	//vector from player location to center of screen
			
			//move, and get rid of dead enemies
			for(int i = 0; i < GameManager.enemies.size(); i++) {
				Enemy e = GameManager.enemies.get(i);
				if(e.health <= 0) {
					for(int j = 0; j < 3; j++) {
						GameManager.items.add(new Coin(e.pos));
					}
					
					GameManager.enemies.remove(i);
					i --;
					continue;
				}
				
				e.tick(map);
			}
			
			//move, and get rid of coins
			for(int i = 0; i < GameManager.items.size(); i++) {
				Item c = GameManager.items.get(i);
				if(c.envHitbox.collision(c.pos, GameManager.player.envHitbox, GameManager.player.pos)) {
					c.onPickup();
					GameManager.items.remove(i);
					i --;
				}
				
				c.tick(map);
			}
			
			//tick decorations
			for(Decoration d : GameManager.decorations) {
				d.tick(map);
			}
			
			//move, and get rid of expired particles
			for(int i = 0; i < GameManager.particles.size(); i++) {
				//System.out.println(particles.get(i).width);
				if(GameManager.particles.get(i).timeLeft < 0) {
					GameManager.particles.remove(i);
					i --;
					continue;
				}
				GameManager.particles.get(i).tick(map);
			}
			
			//enemies hitting player. Contact or bullets
			for(Enemy e : GameManager.enemies) {
				if(e.damageOnContact) {
					GameManager.player.hit(e.envHitbox, e.pos, e.contactDamage);
				}
				
			}
			
			//if the player is attacking 
			boolean hit = false;
			if(GameManager.player.ma.attacking) {
				for(Enemy e : GameManager.enemies) {
					
					if(e.hit(GameManager.player.ma, GameManager.player.pos, (int) (Math.random() * 9) + 1)) {
						hit = true;
					}
	
				}
			}

		}
		
		g.setColor(new Color(103, 199, 239));
		g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		
		//draw background
		int farBackgroundOffset = (int) -(GameManager.player.pos.x % farBackground.getWidth());
		int farHeight = MainPanel.HEIGHT / 2;
		int farVerticalOffset = MainPanel.HEIGHT / 4 + ((int) (-GameManager.player.pos.y / 5d));
		
		int nearBackgroundOffset = (int) -((GameManager.player.pos.x * 2d) % nearBackground.getWidth());
		int nearHeight = MainPanel.HEIGHT / 2;
		int nearVerticalOffset = (MainPanel.HEIGHT / 3) * 2 + ((int) (-GameManager.player.pos.y / 2d));
		
		g.drawImage(farBackground, farBackgroundOffset, farVerticalOffset, (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farHeight, null);
		g.drawImage(farBackground, farBackgroundOffset + (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farVerticalOffset, (farBackground.getWidth() * (farHeight / farBackground.getHeight())), farHeight, null);
		g.drawImage(nearBackground, nearBackgroundOffset, nearVerticalOffset, (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearHeight, null);
		g.drawImage(nearBackground, nearBackgroundOffset + (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearVerticalOffset, (nearBackground.getWidth() * (nearHeight / nearBackground.getHeight())), nearHeight, null);
		
		
		for(Decoration d : GameManager.decorations) {
			d.draw(g);
		}
		
		for(Item c : GameManager.items) {
			c.draw(g);
		}
		
		for(Enemy e : GameManager.enemies) {
			e.draw(g);
		}
		
		GameManager.player.draw(g);
		
		for(Particle p : GameManager.particles) {
			p.draw(g);
		}
		
		map.draw(g);
		

		
		//gray pause overlay
		if(pause) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(GraphicsTools.makeComposite(0.25));
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		}
		
	}

	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ESCAPE) {
			this.pause = !this.pause;
		}
		if(!pause) {
			GameManager.player.keyPressed(k);
			
			for(Decoration d : GameManager.decorations) {
				d.keyPressed(k);
			}
			
		}
	}

	
	public void keyReleased(int k) {
		if(!pause) {
			GameManager.player.keyReleased(k);
		}
	}

	
	public void keyTyped(int k) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mousePressed(MouseEvent arg0) {
		if(!pause) {
			GameManager.player.mousePressed(arg0);
		}
		else {
			
		}
	}

	
	public void mouseReleased(MouseEvent arg0) {
		if(!pause) {
			GameManager.player.mouseReleased();
		}
	}

	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
