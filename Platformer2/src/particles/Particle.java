package particles;

import java.awt.Graphics;

import entities.Entity;
import entities.Hitbox;
import game.Map;
import util.GraphicsTools;
import util.Vector;

public abstract class Particle extends Entity{
	
	public int timeLeft = 60;
	
	public Particle(Vector pos, Vector vel, double width, double height) {
		super();
		this.pos = new Vector(pos);
		this.vel = new Vector(vel);
		this.width = width;
		this.height = height;
		this.envHitbox = new Hitbox(width, height);
	}
	
	public static void loadAnimations() {
		Smoke.sprite = GraphicsTools.loadImage("/Textures/Particles/pixel smoke.png");
	}

	public abstract void tick(Map map);
	public abstract void draw(Graphics g);

}
