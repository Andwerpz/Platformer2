package particles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import entities.Hitbox;
import game.Map;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class DamageNumber extends Particle{
	
	public int val;
	public String valString;
	
	public Font font = new Font("Georgia", Font.BOLD, 20);

	public DamageNumber(int val, Vector pos) {
		super();
		
		this.val = val;
		this.pos = new Vector(pos);
		
		this.valString = val + "";
		int stringWidth = GraphicsTools.calculateTextWidth(valString, this.font);
		
		this.width = ((double) stringWidth) / 25d;
		this.height = ((double) this.font.getSize()) / 25d;
		
		this.envHitbox = new Hitbox(width, height);
		
		this.vel = new Vector((Math.random() - 0.5d) * 0.1, -(Math.random() * 0.15 + 0.1));
		this.frictionInAir = false;
		
		this.timeLeft = 30;
	}
	
	@Override
	public void tick(Map map) {
		
		if(this.outOfBounds(map)) {
			this.timeLeft = 0;
		}
		else {
			this.move(map);
		}
		
		
		this.timeLeft --;
	}

	@Override
	public void draw(Graphics g) {
		//this.drawHitboxes(g, cameraOffset);
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setComposite(GraphicsTools.makeComposite(Math.max(0, (double) this.timeLeft / 30d)));
		g2.setFont(font);
		g2.setColor(Color.RED);
		g2.drawString(this.valString, 
				(int) ((this.pos.x - this.width / 2) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2),
				(int) ((this.pos.y + this.height / 2) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2 - (GameManager.tileSize / 5d)));
		
		g2.setComposite(GraphicsTools.makeComposite(1));
		
	}

}
