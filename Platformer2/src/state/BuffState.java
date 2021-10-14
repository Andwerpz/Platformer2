package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import button.Button;
import button.ButtonManager;
import main.MainPanel;
import player.BuffManager;
import util.GraphicsTools;

public class BuffState extends State{
	
	//draw a few buttons onto the screen, displaying the buffs. The player should choose one of them, and when one is chosen, the buff state should exit to either a hub
	//or another game state.
	
	public static int buttonSize = 200;
	
	public Color backgroundColor = new Color(0, 33, 92);
	
	public Button buff1;
	public Button buff2;
	public Button buff3;
	
	public int levelsRemaining;
	
	public ArrayList<Integer> buffs;
	
	public BuffState(int levelsRemaining) {
		this.levelsRemaining = levelsRemaining;
		
		this.buffs = BuffManager.getRandomBuffs(3);
		
		this.buff1 = new Button(MainPanel.WIDTH / 4 - buttonSize / 2, MainPanel.HEIGHT / 2 - buttonSize / 2, buttonSize, buttonSize, new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB));
		this.buff2 = new Button(MainPanel.WIDTH / 2 - buttonSize / 2, MainPanel.HEIGHT / 2 - buttonSize / 2, buttonSize, buttonSize, new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB));
		this.buff3 = new Button(MainPanel.WIDTH - MainPanel.WIDTH / 4 - buttonSize / 2, MainPanel.HEIGHT / 2 - buttonSize / 2, buttonSize, buttonSize, new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(Point mouse2) {
		//write the buff descriptions when the mouse hovers over a button
	}

	@Override
	public void draw(Graphics g, Point mouse) {
		
		g.setColor(this.backgroundColor);
		g.fillRect(0, 0, MainPanel.WIDTH, MainPanel.HEIGHT);
		
		buff1.draw(g);
		buff2.draw(g);
		buff3.draw(g);
		
		Font descriptionFont = new Font("Georgia", Font.PLAIN, 30);
		Font titleFont = new Font("Georgia", Font.BOLD, 60);
		String buffTitle = "";
		String buffDescription = "";
		
		if(buff1.contains(mouse)) {
			buffDescription = BuffManager.buffDescriptions.get(buffs.get(0));
			buffTitle = BuffManager.buffTitles.get(buffs.get(0));
		}
		else if(buff2.contains(mouse)) {
			buffDescription = BuffManager.buffDescriptions.get(buffs.get(1));
			buffTitle = BuffManager.buffTitles.get(buffs.get(1));
		}
		else if(buff3.contains(mouse)) {
			buffDescription = BuffManager.buffDescriptions.get(buffs.get(2));
			buffTitle = BuffManager.buffTitles.get(buffs.get(2));
		}
		
		int descriptionWidth = GraphicsTools.calculateTextWidth(buffDescription, descriptionFont);
		int titleWidth = GraphicsTools.calculateTextWidth(buffTitle, titleFont);
		
		g.setColor(Color.WHITE);
		
		g.setFont(descriptionFont);
		g.drawString(buffDescription, MainPanel.WIDTH / 2 - descriptionWidth / 2, MainPanel.HEIGHT - (MainPanel.HEIGHT / 5));
		
		g.setFont(titleFont);
		g.drawString(buffTitle, MainPanel.WIDTH / 2 - titleWidth / 2, MainPanel.HEIGHT - (MainPanel.HEIGHT / 5 + titleFont.getSize() + 20));
	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(int k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(buff1.isClicked(arg0)) {
			GameManager.player.buffManager.getBuff(this.buffs.get(0));
			GameManager.transition(new GameState(this.levelsRemaining), "going deeper");
		}
		else if(buff2.isClicked(arg0)) {
			GameManager.player.buffManager.getBuff(this.buffs.get(1));
			GameManager.transition(new GameState(this.levelsRemaining), "going deeper");
		}
		else if(buff3.isClicked(arg0)) {
			GameManager.player.buffManager.getBuff(this.buffs.get(2));
			GameManager.transition(new GameState(this.levelsRemaining), "going deeper");
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		buff1.pressed(arg0);
		buff2.pressed(arg0);
		buff3.pressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		buff1.released();
		buff2.released();
		buff3.released();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
