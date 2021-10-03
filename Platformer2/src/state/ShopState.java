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

import game.GamePanel;
import game.Map;


public class ShopState extends State{
	
	public GamePanel gp;
	
	public ShopState() {
		
	}

	@Override
	public void init() {
		//reset players health to max when they enter the hub
		Map map = new Map();
		map.readFromFile("shop.txt");
		this.gp = new GamePanel(map);
		
		GameManager.player.health = GameManager.player.maxHealth;
	}

	public void tick(java.awt.Point mouse2) {}


	public void draw(Graphics g, java.awt.Point mouse) {
		gp.draw(g, mouse);
	}

	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_E) {
			GameManager.transition(new HubState(), "Back to Hub");
		}
		gp.keyPressed(k);
	}

	
	public void keyReleased(int k) {
		gp.keyReleased(k);
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
		gp.mousePressed(arg0);
	}

	
	public void mouseReleased(MouseEvent arg0) {
		gp.mouseReleased(arg0);
	}

	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
