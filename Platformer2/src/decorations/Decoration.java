package decorations;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import util.GraphicsTools;
import util.Vector;

public abstract class Decoration extends Entity {
	
	//this is a pretty bad name for this class.
	//perhaps for the interactive ones, we could make a seperate class, but this works for now
	//InteractiveDecorations? 
	
	//and for the static ones, there probably is a better way to load and draw them
	//i don't want to have to make a class for every type of decoration
	
	public static final int TREE = 1;
	public static final int SHOP = 2;
	public static final int CHEST = 3;
	
	public int animationFrame;
	public int animationFrameInterval;
	
	public Decoration(int width, int height, Vector pos) {
		this.pos = new Vector(pos);
		
		this.width = width;
		this.height = height;
		this.envHitbox = new Hitbox(width, height);
	}
	
	public static void loadTextures() {
		Tree.animation = GraphicsTools.loadAnimation("/Textures/Decorations/Tree/pixel_tree.png", 29, 32);
	}
	
	public static Decoration getDecoration(int type, Vector pos) {
		switch (type) {
		case TREE:
			return new Tree(pos);
			
		case SHOP:
			return new Shop(pos);
			
		case CHEST:
			return new Chest(pos);
		}
		
		return null;
	}
	
	public abstract void tick(Map map);
	public abstract void draw(Graphics g);
	public abstract void keyPressed(int k);	//this is for interactive things like chests or teleport pads

}
