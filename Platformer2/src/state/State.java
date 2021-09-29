package state;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class State {
	
	public State() {}
	
	public abstract void init();
	public abstract void tick(Point mouse2);
	public abstract void draw(Graphics g, Point mouse);
	
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	public abstract void keyTyped(int k);
	
	public abstract void mouseClicked(MouseEvent arg0);
	public abstract void mouseEntered(MouseEvent arg0);
	public abstract void mouseExited(MouseEvent arg0);
	public abstract void mousePressed(MouseEvent arg0);
	public abstract void mouseReleased(MouseEvent arg0);
	public abstract void mouseWheelMoved(MouseWheelEvent arg0);
	
}
