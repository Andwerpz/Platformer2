package projectiles;

import java.awt.Graphics;

import game.Map;
import util.GraphicsTools;
import util.Vector;

public abstract class Explosion extends Projectile {
	
	//just a big area damage effect.
	//usually very short, and goes away quick
	
	//the animation usually lasts longer than the hurt frames, so we'll need to account for that

	public Explosion(Vector pos, double width, double height, int damage) {
		super(pos, new Vector(0, 0), width, height, damage);
		this.timeLeft = 8;
	}
	
	public static void loadAnimations() {
		LargeExplosion.animation = GraphicsTools.loadAnimation("/Textures/Explosions/pixel explosion 1 big.png", 100, 100);
	}

	@Override
	public void tick(Map map) {
		//TODO
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hit() {
		//nothing happens. explosions can affect multiple enemies.
	}

	@Override
	public void timeOut() {
		//nothing happens usually
	}

}
