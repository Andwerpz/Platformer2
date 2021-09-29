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
import entities.Player;
import game.GamePanel;
import game.Map;
import item.Coin;
import item.Item;
import main.MainPanel;
import melee.MeleeAttack;
import particles.Particle;
import util.GraphicsTools;
import util.Vector;
import util.Point;

public class GameState extends State{
	
	public boolean wavePause = true;	//the pause between waves
	//during the pause, a big "Wave x" should display
	
	public int wavePauseTime;
	public double wavePopupOpacity = 1d;
	
	public boolean win = false;
	
	public GamePanel gp;

	public GameState() {
		
		
		
	}


	public void init() {
		this.wavePauseTime = 360;	//initial wave pause time
		
		this.gp = new GamePanel("crucible with waves.txt");
		
	}

	public void tick(java.awt.Point mouse2) {}


	public void draw(Graphics g, java.awt.Point mouse) {
		
		//doing enemy wave logic and drawing the hud
		//everything else is handled in the game panel object
		
		//spawn new enemies
		if(GameManager.enemies.size() == 0 && !this.wavePause) {
			if(gp.map.selectedWave >= gp.map.enemyWaves.size()) {
				this.win = true;
			}
			else {
				this.wavePause = true;
				this.wavePauseTime = 360;
				//this.map.spawnNextWave();
			}
		}
		
		if(wavePause) {
			this.wavePauseTime --;
			if(this.wavePauseTime <= 0) {
				this.wavePause = false;
				gp.map.spawnNextWave();
			}
		}
		
		this.gp.draw(g, mouse);
		
		//drawing hud
		g.setFont(new Font("Dialogue", Font.BOLD, 12));
		if(GameManager.player.immune) {
			g.setColor(Color.RED);
		}
		else {
			g.setColor(Color.black);
		}
		g.drawString("Health: " + GameManager.player.health, 20, 20);
		
		g.setColor(Color.black);
		g.drawString("Gold: " + GameManager.gold, 20, 40);
		
		g.drawString("Wave: " + (gp.map.selectedWave), 20, 60);
		
		
		if(this.wavePause) {
			String popup = "Wave " + (gp.map.selectedWave + 1);
			Font popupFont = new Font("Georgia", Font.BOLD, 48);
			
			int popupWidth = GraphicsTools.calculateTextWidth(popup, popupFont);
			
			this.wavePopupOpacity = ((double) this.wavePauseTime / (double) 360);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(popupFont);
			g2.setComposite(GraphicsTools.makeComposite(this.wavePopupOpacity));
			g2.drawString(popup, MainPanel.WIDTH / 2 - popupWidth / 2, MainPanel.HEIGHT / 4);
		}
		
		if(GameManager.player.health <= 0) {
			GameManager.transition(new HubState(), "You Died");
		}
		
		if(win) {
			GameManager.transition(new HubState(), "You Win");
		}
		
	}

	
	public void keyPressed(int k) {
//		if(k == KeyEvent.VK_ESCAPE) {
//			this.pause = !this.pause;
//		}
//		if(!pause) {
//			player.keyPressed(k);
//		}
		gp.keyPressed(k);
	}

	
	public void keyReleased(int k) {
		gp.keyReleased(k);
//		if(!pause) {
//			player.keyReleased(k);
//		}
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
//		if(!pause) {
//			player.mousePressed(arg0);
//		}
//		else {
//			
//		}
		
		gp.mousePressed(arg0);
	}

	
	public void mouseReleased(MouseEvent arg0) {
//		if(!pause) {
//			player.mouseReleased();
//		}
		gp.mouseReleased(arg0);
	}

	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
