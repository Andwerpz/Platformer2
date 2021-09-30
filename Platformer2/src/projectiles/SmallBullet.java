package projectiles;

import java.awt.Graphics;

import game.Map;
import util.Vector;

public class SmallBullet extends Bullet {

	public SmallBullet(Vector pos, Vector vel, int damage) {
		super(pos, vel, 0.3, 0.3, damage);
		this.timeLeft = 180;
	}

	@Override
	public void tick(Map map) {
		this.move(map);
		if(this.envCollision) {
			this.hit();
		}
		this.timeLeft --;
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}

	@Override
	public void hit() {
		this.timeLeft = -1;
	}

}
