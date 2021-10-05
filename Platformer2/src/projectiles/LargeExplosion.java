package projectiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Map;
import util.Vector;

public class LargeExplosion extends Explosion{
	
	public static ArrayList<BufferedImage> animation;
	public static int animationFrameTime = 3;
	
	public int animationFrame;
	
	public LargeExplosion(Vector pos, int damage) {
		super(pos, 8, 8, damage);
		this.timeLeft = LargeExplosion.animation.size() * LargeExplosion.animationFrameTime;
		this.animationFrame = 0;
	}
	
	@Override
	public void tick(Map map) {
		if(this.timeLeft != LargeExplosion.animation.size() * LargeExplosion.animationFrameTime) {
			this.active = false;
		}
		this.timeLeft --;
	}
	
	@Override
	public void draw(Graphics g) {
		this.drawSprite(animation.get(animationFrame / LargeExplosion.animationFrameTime), g);
		this.animationFrame = Math.min(this.animationFrame + 1, (LargeExplosion.animation.size() - 1) * LargeExplosion.animationFrameTime);
	}

}
