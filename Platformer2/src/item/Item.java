package item;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;
import weapon.Weapon;

public abstract class Item extends Entity {
	
	public static final int COIN = 1;
	public static final int APPLE = 2;
	
	public boolean autoPickup = false;
	
	public boolean purchaseable = false;	//if true, then a certain amount of gold is required to pick this item up for the first time.
	public int itemCost;	//the amount of gold this item costs
	
	public Item(Vector pos, Vector vel, double width, double height) {
		this.pos = new Vector(pos);
		this.vel = new Vector(vel);
		this.width = width;
		this.height = height;
		this.envHitbox = new Hitbox(width, height);
	}
	
	public abstract void onPickup();
	public abstract void tick(Map map);
	public abstract void draw(Graphics g);
	
	public static void loadTextures() {
		Coin.animation = GraphicsTools.loadAnimation("/Textures/Items/Coin/coin_rotate.png", 11, 11);
		Apple.animation = GraphicsTools.loadAnimation("/Textures/Items/Apple/apple.png", 32, 35);
		Weapon.loadAnimations();
	}
	
	public void drawCostIndicator(Graphics g) {
		//draw cost indicator above item
		if(this.purchaseable) {
			String costString = this.itemCost + "g";
			Font font = new Font("Dialogue", Font.PLAIN, 12);
			int stringWidth = GraphicsTools.calculateTextWidth(costString, font);
			Vector labelPos = new Vector(this.pos);
			labelPos.y -= this.height / 2;
			Vector labelScreenPos = GameManager.getScreenPos(labelPos);
			labelScreenPos.y -= font.getSize();
			
			g.setFont(font);
			g.setColor(new Color(Integer.parseInt("fed752", 16)));
			
			g.drawString(costString, (int) (labelScreenPos.x - stringWidth / 2d), (int) labelScreenPos.y);
		}
	}

}
