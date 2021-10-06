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
import entities.Hitbox;
import entities.Player;
import item.Apple;
import item.Coin;
import item.Item;
import main.MainPanel;
import particles.Particle;
import projectiles.Explosion;
import projectiles.Projectile;
import state.GameManager;
import state.HubState;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class GamePanel {
	
	//this is where all the actual map updating and drawing takes place.

	public Map map;
	
	public BufferedImage nearBackground;
	public BufferedImage farBackground;
	
	public java.awt.Point mouse = new java.awt.Point(0, 0);
	
	public boolean pause = false;	//if buttons do show up, then they should be drawn in the state class

	public GamePanel(Map map) {
		this.map = map;
		
		map.calculateMapLight();
		
		Vector playerSpawn = map.playerSpawn;
		GameManager.player.pos = new Vector(playerSpawn);
		
		GameManager.enemies = new ArrayList<Enemy>();
		GameManager.particles = new ArrayList<Particle>();
		GameManager.items = new ArrayList<Item>();
		GameManager.decorations = new ArrayList<Decoration>();
		GameManager.projectiles = new ArrayList<Projectile>();
		
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
			
			//move and get rid of dead projectiles
			//also do collision checks between projectiles and enemies
			for(int i = 0; i < GameManager.projectiles.size(); i++) {
				Projectile p = GameManager.projectiles.get(i);
				if(p.timeLeft == 0) {
					p.timeOut();
					GameManager.projectiles.remove(i);
					i--;
					continue;
				}
				p.tick(map);
				if(p.timeLeft < 0) {
					GameManager.projectiles.remove(i);
					i--;
					continue;
				}
				
				//if this projectile still has hurt frames
				if(p.active) {
					for(int j = 0; j < GameManager.enemies.size(); j++) {
						Enemy e = GameManager.enemies.get(j);
						if(e.hit(p)) {
							p.hit();
							if(p.timeLeft < 0) {
								GameManager.projectiles.remove(i);
								i--;
								break;
							}
							
						}
					}
				}
				
				
			}
			
			//move, and handle items
			//if the item is a coin, then make the player automatically pick it up
			for(int i = 0; i < GameManager.items.size(); i++) {
				Item c = GameManager.items.get(i);
				if(c.autoPickup) {
					if(c.envHitbox.collision(c.pos, GameManager.player.envHitbox, GameManager.player.pos)) {
						c.onPickup();
						GameManager.items.remove(i);
						i --;
						continue;
					}
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
		
		map.drawBackground(g);
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
		
		for(Projectile p : GameManager.projectiles) {
			p.draw(g);
		}
		
		map.draw(g);
		
		//draw ui
		//for now, a simple png overlay will do
		//in the future, I want to have control over the components of the overlay, so I can dynamically shrink and stretch the health bar
		
		int healthBarWidth = 360;
		int staminaBarWidth = 240;
		
		g.setColor(Color.RED);
		g.fillRect(0, 33, (int) (((double) GameManager.player.health / (double) GameManager.player.maxHealth) * (double) healthBarWidth), 33);
		
		g.setColor(Color.blue);
		g.fillRect(0, 64, (int) (((double) GameManager.player.stamina / (double) GameManager.player.maxStamina) * (double) staminaBarWidth), 33);
		
		g.drawImage(GameManager.healthBar, 0, 0, 400, 120, null);
		
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Dialogue", Font.BOLD, 20));
		
		g.drawString("GOLD: " + GameManager.gold, 20, 200);
		
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
