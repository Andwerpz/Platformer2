package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import state.GameManager;


public class MainPanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseWheelListener{
	
	public final static int WIDTH = 1280;
	public final static int HEIGHT = 720;
	
	private boolean isRunning = true;
	private Thread thread;
	
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	public Point mouse = new Point(0, 0);
	
	public static PrintWriter fout;
	
	private GameManager gm;
	//private Images images;

	public MainPanel() {
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		setFocusable(true);
		setVisible(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		fout = null;
		
		try {
			fout = new PrintWriter(new BufferedWriter(new FileWriter("outputMap.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//this.images = new Images();
		
		this.start();
		
	}
	
	private void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		
		long start, elapsed, wait;
		
		gm = new GameManager();
		
		while(isRunning) {
			
			start = System.nanoTime();
			
			tick();
			repaint();
			
			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;
			
			if(wait < 0) {
				wait = 5;
			}
			
			try {
				thread.sleep(wait);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void tick() {
		
		java.awt.Point mouse2 = MouseInfo.getPointerInfo().getLocation();
		
		mouse.setLocation(mouse2.x, mouse2.y);
		SwingUtilities.convertPointToScreen(mouse2, this);
		
		mouse.setLocation(mouse.x - (mouse2.x - mouse.x), mouse.y - (mouse2.y - mouse.y));
		
		gm.tick(mouse);
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		g.clearRect(0, 0, WIDTH * 2, HEIGHT * 2);
		
		gm.draw(g, this.mouse);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		gm.mouseClicked(arg0);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		gm.mouseEntered(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		gm.mouseExited(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		gm.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		gm.mouseReleased(arg0);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		gm.keyPressed(arg0.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		gm.keyReleased(arg0.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		gm.keyTyped(arg0.getKeyCode());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		gm.mouseWheelMoved(arg0);
	}

	
	
}
