package particles;

import java.awt.Graphics;

import entities.Entity;
import game.Map;
import util.Vector;

public abstract class Particle extends Entity{
	
	public int timeLeft = 60;
	
	public Particle() {
		super();
	}

	public abstract void tick(Map map);
	public abstract void draw(Graphics g);

}
