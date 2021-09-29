package decorations;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Hitbox;
import game.Map;
import state.GameManager;
import state.ShopState;
import util.Vector;

public class Shop extends Decoration{
	
	public static ArrayList<BufferedImage> animation;
	
	public Shop(Vector pos) {
		super(8, 10, pos);
		
		this.animationFrame = 0;
		this.animationFrameInterval = 1;
	}

	@Override
	public void tick(Map map) {
		this.animationFrame ++;
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
		//this.drawSprite(animation.get(animationFrame / this.animationFrameInterval), g);
	}

	@Override
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_E && this.envHitbox.collision(this.pos, GameManager.player.envHitbox, GameManager.player.pos)) {
			GameManager.transition(new ShopState(), "LOADING");
		}
	}

}
