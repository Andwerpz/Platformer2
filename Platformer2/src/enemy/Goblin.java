package enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import util.Vector;

public class Goblin extends Enemy {
	
	public static ArrayList<BufferedImage> idleAnimation;
	
	public Goblin(Vector pos) {
		super();
		
		this.pos = new Vector(pos);
		
		this.health = 50;
		this.frictionInAir = false;
	}

	@Override
	public void tick(Map map) {
		this.move(map);
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}

}
