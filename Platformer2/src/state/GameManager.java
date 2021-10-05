package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import button.ButtonManager;
import decorations.Decoration;
import enemy.Enemy;
import enemy.Slime;
import entities.Entity;
import entities.Hitbox;
import entities.Player;
import game.Map;
import game.TilesetManager;
import item.Coin;
import item.Item;
import main.MainPanel;
import melee.MeleeAttack;
import particles.DamageNumber;
import particles.Particle;
import projectiles.Projectile;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class GameManager {
	
	public static double tileSize = 20;	//controls the scale of the entire game. Could be useful in a zoom function
	
	public static Player player;
	
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Particle> particles;
	public static ArrayList<Item> items;
	public static ArrayList<Decoration> decorations;	//have to figure out a better way of ticking these.
	//want to support animation.
	public static ArrayList<Projectile> projectiles;	//these do damage to enemies
	
	public static Vector cameraOffset = new Vector(0, 0);	//or alternatively, you could just use inverse of the player position
	
	public static int gold = 0;	//should find a better place to put this info
	//maybe make an object to hold all the players stuff. 
	
	public static State curState;
	public static State nextState;	//so we know what to draw under the transition
	
	public static boolean transitioning = false;
	public static int transitionTime = 30;
	public static int transitionTimeLeft;
	public static String transitionMessage;
	
	public static BufferedImage healthBar = GraphicsTools.loadImage("/Textures/UI/healthbar resized.png");
	
	
	public GameManager() {
		
		GameManager.player = new Player(new Vector(0, 0));
		
		//loading all entity textures
		//should move this somewhere else, but this works for now
		Enemy.loadTextures();
		Player.loadTextures();
		Decoration.loadTextures();
		Item.loadTextures();
		Map.loadTileTextures();
		MeleeAttack.loadTextures();
		
		Projectile.loadAnimations();
		Particle.loadAnimations();
		
		TilesetManager.loadTiles();
		
		//Slime.loadTextures();
		
		GameManager.enemies = new ArrayList<Enemy>();
		GameManager.particles = new ArrayList<Particle>();
		GameManager.items = new ArrayList<Item>();
		GameManager.decorations = new ArrayList<Decoration>();
		GameManager.projectiles = new ArrayList<Projectile>();
			
		GameManager.cameraOffset = new Vector(0, 0);
		
		curState = new HubState();
		//curState = new GameState();
		//curState = new EditorState("hub.txt");
		curState.init();
	}
	
	public static void transition(State nextState, String message) {
		if(!GameManager.transitioning) {
			GameManager.nextState = null;
			GameManager.nextState = nextState;
			GameManager.transitionTimeLeft = transitionTime;
			GameManager.transitioning = true;
			GameManager.transitionMessage = message;
		}
	}
	
	public void tick(java.awt.Point mouse) {}
	
	public void draw(Graphics g, java.awt.Point mouse) {
		
		GameManager.curState.draw(g, mouse);
		
		//transitioning code
		if(transitioning) {
			g.setColor(Color.BLACK);
			//System.out.println(transitioning + " " + transitionTimeLeft);
			transitionTimeLeft --;
			if((double) transitionTimeLeft >= ((double)transitionTime / 3d) * 2d) {
				
				double oneThird = (double) transitionTime / 3d;
				double twoThirds = ((double) oneThird * 2d);
				double timeLeft = (double) transitionTimeLeft - twoThirds;
				int width = (int) (MainPanel.WIDTH * ((oneThird - timeLeft) / oneThird));
				//System.out.println(width);
				g.fillRect(0, 0, 
						width,
						MainPanel.HEIGHT);
			}
			else if(transitionTimeLeft <= transitionTime / 3){
				int width = (int) (MainPanel.WIDTH * ((double) (transitionTimeLeft) / (double) (transitionTime / 3d)));
				g.fillRect(MainPanel.WIDTH - width, 0, width, MainPanel.HEIGHT);
			}
			else {
				g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
				Font font = new Font("Georgia", 0, 48);
				int stringWidth = GraphicsTools.calculateTextWidth(transitionMessage, font);
				g.setColor(Color.WHITE);
				g.setFont(font);
				g.drawString(transitionMessage, MainPanel.WIDTH / 2 - stringWidth / 2, MainPanel.HEIGHT / 2 - font.getSize() / 2);
			}
			if(transitionTimeLeft == transitionTime / 2) {
				//System.out.println("CHANGE");
				GameManager.curState = null;
				GameManager.curState = GameManager.nextState;
				curState.init();
			}
			if(transitionTimeLeft <= 0) {
				transitioning = false;
			}
		}
		
	}
	
	public void mouseClicked(MouseEvent arg0) {
		GameManager.curState.mouseClicked(arg0);
	}
	
	public void mouseEntered(MouseEvent arg0) {
		GameManager.curState.mouseEntered(arg0);
	}
	
	public void mouseExited(MouseEvent arg0) {
		GameManager.curState.mouseExited(arg0);
	}
	
	public void mousePressed(MouseEvent arg0) {
		this.curState.mousePressed(arg0);
	}
	
	public void mouseReleased(MouseEvent arg0) {
		this.curState.mouseReleased(arg0);
	}
	
	public void keyTyped(int k) {
		this.curState.keyTyped(k);
	}
	
	public void keyPressed(int k) {
		this.curState.keyPressed(k);
	}
	
	public void keyReleased(int k) {
		this.curState.keyReleased(k);
	}
	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		this.curState.mouseWheelMoved(arg0);
	}
}
