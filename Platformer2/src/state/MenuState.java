package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import button.ButtonManager;
import button.Button;
import button.SliderButton;
import button.ToggleButton;
import util.TextBox;

public class MenuState extends State{
	
	ButtonManager bm;
	
	public MenuState() {
		bm = new ButtonManager();
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void tick(Point mouse) {

		bm.tick(mouse);
		
		//this.gsm.states.add(new EditorState(gsm, "crucible.txt"));
		
	}

	@Override
	public void draw(Graphics g, Point mouse) {
		
		bm.draw(g);
		
	}

	@Override
	public void keyPressed(int k) {
	
	}

	@Override
	public void keyReleased(int k) {

	}

	@Override
	public void keyTyped(int k) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

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
		
		bm.pressed(arg0);


	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		bm.mouseReleased();
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
