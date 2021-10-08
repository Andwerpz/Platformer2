package item;

import java.awt.Graphics;

import game.Map;
import player.BuffManager;
import player.Player;
import state.GameManager;
import util.Vector;

public class Buff extends Item {
	
	public int type = BuffManager.MULTISHOT;
	
	public Buff(Vector pos) {
		super(pos, new Vector(0, 0), 2, 2);
		
		this.purchaseable = true;
		this.itemCost = 35;
	}

	@Override
	public void onPickup() {
		if(this.purchaseable) {
			if(GameManager.gold >= this.itemCost) {
				GameManager.gold -= this.itemCost;
				GameManager.player.buffManager.getBuff(this.type);
				GameManager.items.remove(this);
			}
		}
		else {
			GameManager.player.buffManager.getBuff(this.type);
			if(GameManager.items.contains(this)) {
				GameManager.items.remove(this);
			}
		}
		
	}

	@Override
	public void tick(Map map) {
		this.move(map);
	}

	@Override
	public void draw(Graphics g) {
		this.drawHitboxes(g);
		this.drawCostIndicator(g);
	}

}
