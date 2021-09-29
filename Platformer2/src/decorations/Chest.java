package decorations;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import game.Map;
import item.Coin;
import particles.DamageNumber;
import state.GameManager;
import util.Vector;

public class Chest extends Decoration{

	public Chest(Vector pos) {
		super(2, 2, pos);
	}

	@Override
	public void tick(Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}

	@Override
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_E && this.collision(GameManager.player)) {
			for(int i = 0; i < 10; i++) {
				double x = (Math.random() * this.width) - this.width / 2d + this.pos.x;
				double y = -this.height / 2d + this.pos.y; 
				GameManager.items.add(new Coin(new Vector(x, y)));
			}
		}
	}

}
