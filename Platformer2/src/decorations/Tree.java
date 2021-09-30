package decorations;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import enemy.Slime;
import entities.Hitbox;
import game.Map;
import item.Apple;
import item.Coin;
import particles.DamageNumber;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;
import weapon.AirburstShotgun;

public class Tree extends Decoration {
	
	public static ArrayList<BufferedImage> animation;
	
	public boolean droppedApple;
	
	public Tree(Vector pos) {
		super(4, 4, pos);
		
		this.animationFrameInterval = 1;
		this.animationFrame = 0;
		
		this.droppedApple = false;
	}

	@Override
	public void tick(Map map) {
		
		
	}

	@Override
	public void draw(Graphics g) {
		this.drawSprite(Tree.animation.get(0), g);
	}
  
	public void keyPressed(int k) {

		//the tree can drop one apple. The player can walk up to the tree and press 'e' to shake it.
		
		if(k == KeyEvent.VK_E && this.envHitbox.collision(this.pos, GameManager.player.envHitbox, GameManager.player.pos)) {
			if(!this.droppedApple && Math.random() > 0.9) {
				double x = (Math.random() * this.width) - this.width / 2d + this.pos.x;
				double y = (Math.random() * this.height) - this.height / 2d + this.pos.y; 
				this.droppedApple = true;
				GameManager.items.add(new AirburstShotgun(new Vector(x, y)));
			}
			else if(!this.droppedApple) {
				for(int i = 0; i < 10; i++) {
					double x = (Math.random() * this.width) - this.width / 2d + this.pos.x;
					double y = (Math.random() * this.height) - this.height / 2d + this.pos.y; 
					GameManager.particles.add(new DamageNumber(1, new Vector(x, y)));
				}
			}
		}
		
		
	}
	
}
