package item;

import java.awt.Graphics;

import entities.Entity;
import game.Map;
import util.GraphicsTools;
import weapon.Weapon;

public abstract class Item extends Entity {
	
	public static final int COIN = 1;
	public static final int APPLE = 2;
	
	public boolean autoPickup = false;
	
	public abstract void onPickup();
	public abstract void tick(Map map);
	public abstract void draw(Graphics g);
	
	public static void loadTextures() {
		Coin.animation = GraphicsTools.loadAnimation("/Textures/Items/Coin/coin_rotate.png", 11, 11);
		Apple.animation = GraphicsTools.loadAnimation("/Textures/Items/Apple/apple.png", 32, 35);
		Weapon.loadAnimations();
	}

}
