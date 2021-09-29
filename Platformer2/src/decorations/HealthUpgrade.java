package decorations;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import game.Map;
import state.GameManager;
import util.Vector;

public class HealthUpgrade extends Decoration {

	public HealthUpgrade(Vector pos) {
		super(3, 4, pos);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick(Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_E && this.collision(GameManager.player)) {
			
		}
	}

}
