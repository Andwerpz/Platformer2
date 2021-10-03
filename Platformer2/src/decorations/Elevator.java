package decorations;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.Map;
import state.GameManager;
import state.GameState;
import util.Vector;

public class Elevator extends Decoration{
	
	public static BufferedImage sprite;
	
	public boolean active = true;	//if this elevator is active, then if interacted with, it should transport player to another level

	public Elevator(Vector pos) {
		super(9, 14, pos);
	}

	@Override
	public void tick(Map map) {
		
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
	}

	@Override
	public void keyPressed(int k) {
		if(this.active && this.collision(GameManager.player)) {
			if(GameManager.curState instanceof GameState) {	//if the player is currently in a level
				
			}
			else {
				GameManager.transition(new GameState(), "uwu");
			}
		}
	}

}
