package particles;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Map;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Vector;

public class Smoke extends Particle {
	
	public static BufferedImage sprite;

	public Smoke(Vector pos, Vector vel, double size) {
		super(pos, vel, size, size);
		this.timeLeft = 60;
		
		this.gravity = false;
		this.frictionInAir = true;
		this.verticalFriction = true;
		this.frictionOnGround = false;
		
		this.maxHorizontalSpeed = 1;
		this.maxVerticalSpeed = 1;
	}
	
	@Override
	public void tick(Map map) {
		if(this.outOfBounds(map)) {
			this.timeLeft = 0;
		}
		else {
			this.move(map);
		}
		this.timeLeft -= 1;
	}

	@Override
	public void draw(Graphics g) {
		if(this.timeLeft >= 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(GraphicsTools.makeComposite((double) this.timeLeft / 120d));	//as time goes on, this particle gets more transparent
			g2.drawImage(Smoke.sprite, 
					(int) ((this.pos.x - this.width / 2) * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2), 
					(int) ((this.pos.y - this.height / 2) * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2), 
					(int) (this.width * GameManager.tileSize), 
					(int) (this.height * GameManager.tileSize), null);
			g2.setComposite(GraphicsTools.makeComposite(1));
		}
		
	}
	
}
