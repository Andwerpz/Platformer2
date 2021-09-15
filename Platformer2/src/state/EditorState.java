package state;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import button.Button;
import button.ButtonManager;
import button.ToggleButton;
import game.GameManager;
import game.Map;
import main.MainPanel;
import util.Vector;

public class EditorState extends State {
	
	ButtonManager bm;
	
	public Map map;
	
	public Vector cameraOffset;
	
	public util.Point mouse = new util.Point(0, 0);
	
	public boolean rightMouse = false;
	public boolean leftMouse = false;
	
	public boolean erase = false;
	
	public boolean placeEnemy = false;
	public int enemyType = 1;
	
	public boolean fillMode = false;
	public Point fillPress;
	public Point fillRelease;
	
	public int tileType = 1;
	
	public Vector cameraPos;

	public EditorState(StateManager gsm, String filepath) {
		super(gsm);
		
		bm = new ButtonManager();
		
		bm.addButton(new Button(10, 10, 100, 25, "Save Map"));
		bm.addButton(new Button(10, 40, 100, 25, "Tile Type"));
		bm.addButton(new Button(10, 130, 100, 25, "Calculate Lighting"));
		bm.addButton(new Button(10, 190, 100, 25, "Add Wave"));
		bm.addButton(new Button(10, 220, 50, 25, "Prev"));
		bm.addButton(new Button(60, 220, 50, 25, "Next"));
		
		
		bm.addToggleButton(new ToggleButton(10, 70, 100, 25, "Erase"));	bm.toggleButtons.get(0).setToggled(false);
		bm.addToggleButton(new ToggleButton(10, 100, 100, 25, "Draw Grid"));	bm.toggleButtons.get(1).setToggled(true);
		bm.addToggleButton(new ToggleButton(10, 160, 100, 25, "Place Enemy"));	bm.toggleButtons.get(2).setToggled(false);
		
		this.map = new Map();
		this.map.drawTileGrid = true;
		
		
		
		this.map.map = new int[100][100];
		for(int i = 0; i < map.map.length; i++) {
			for(int j = 0; j < map.map[0].length; j++) {
				if(Math.random() > 1) {
					this.map.map[i][j] = 1;
				}
			}
		}
		
		this.map.mapTileTexture = new BufferedImage[map.map.length][map.map[0].length];
		
		this.map.loadMapTileTextures();
		
		this.map.readFromFile(filepath);
		this.map.calculateMapLight();
		
		this.cameraPos = new Vector(this.map.map[0].length / 2, this.map.map.length / 2);
		this.cameraOffset = new Vector((int) ((cameraPos.x) * GameManager.tileSize), (int) ((cameraPos.y) * GameManager.tileSize));
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void writeToFile() {
		int width = this.map.map[0].length;
		int height = this.map.map.length;
		
		MainPanel.fout.println(width + " " + height);
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				MainPanel.fout.print(this.map.map[i][j] + " ");
			}
			MainPanel.fout.println();
		}
		for(ArrayList<int[]> a : this.map.enemyWaves) {
			MainPanel.fout.println("wave");
			for(int[] i : a) {
				MainPanel.fout.println(i[0] + " " + i[1] + " " + i[2]);
			}
		}
		MainPanel.fout.flush();
		MainPanel.fout.close();
		
//		System.out.println(width + " " + height);
//		for(int i = 0; i < height; i++) {
//			for(int j = 0; j < width; j++) {
//				System.out.print(this.map.map[i][j] + " ");
//			}
//			System.out.println();
//		}
		
		
		
		System.out.println("SAVE SUCCESSFUL");
	}

	@Override
	public void tick(Point mouse2) {
		
	}

	@Override
	public void draw(Graphics g, Point mouse2) {
		
		this.erase = bm.toggleButtons.get(0).getToggled();
		map.drawTileGrid = bm.toggleButtons.get(1).getToggled();
		this.placeEnemy = bm.toggleButtons.get(2).getToggled();
		
		double xDiff = mouse2.x - mouse.x;
		double yDiff = mouse2.y - mouse.y;
		
		if(this.rightMouse) {
			this.cameraPos.addVector(new Vector(-xDiff / GameManager.tileSize, -yDiff / GameManager.tileSize));
		}
		GameManager.cameraOffset = new Vector((int) ((cameraPos.x) * GameManager.tileSize), (int) ((cameraPos.y) * GameManager.tileSize));
		
		this.mouse = new util.Point(mouse2.x, mouse2.y);
		
		//calculating where on the map the mouse is
		//just do the opposite transformation required to go from map space to screen space
		int mapX = (int) ((mouse.x + GameManager.cameraOffset.x - MainPanel.WIDTH / 2) / GameManager.tileSize);
		int mapY = (int) ((mouse.y + GameManager.cameraOffset.y - MainPanel.HEIGHT / 2) / GameManager.tileSize);
		
		if(this.leftMouse) {
			if(this.placeEnemy) {
				if(this.erase) {
					for(int i = 0; i < this.map.enemyWaves.get(this.map.selectedWave).size(); i++) {
						if(this.map.enemyWaves.get(this.map.selectedWave).get(i)[1] == mapY && this.map.enemyWaves.get(this.map.selectedWave).get(i)[2] == mapX) {
							this.map.enemyWaves.get(this.map.selectedWave).remove(i);
							break;
						}
					}
				}
				else {
					boolean contains = false;
					for(int i = 0; i < this.map.enemyWaves.get(this.map.selectedWave).size(); i++) {
						if(this.map.enemyWaves.get(this.map.selectedWave).get(i)[1] == mapY && this.map.enemyWaves.get(this.map.selectedWave).get(i)[2] == mapX) {
							contains = true;
							break;
						}
					}
					if(!contains) {
						this.map.enemyWaves.get(this.map.selectedWave).add(new int[] {this.enemyType, mapY, mapX});
					}	
				}
			}
			else {
				if(mapX >= 0 && mapX < this.map.map[0].length && mapY >= 0 && mapY < this.map.map.length) {
					if(erase && this.map.map[mapY][mapX] != 0) {
						this.map.map[mapY][mapX] = 0;
						for(int i = mapY - 1; i <= mapY + 1; i++) {
							for(int j = mapX - 1; j <= mapX + 1; j++) {
								if(i >= 0 && i < this.map.map.length && j >= 0 && j < this.map.map[0].length) {
									this.map.loadTileTextures(i, j);
								}
							}
						}
					}
					else if(!erase){
						this.map.map[mapY][mapX] = this.tileType;
						for(int i = mapY - 1; i <= mapY + 1; i++) {
							for(int j = mapX - 1; j <= mapX + 1; j++) {
								if(i >= 0 && i < this.map.map.length && j >= 0 && j < this.map.map[0].length) {
									this.map.loadTileTextures(i, j);
								}
							}
						}
						
					}
				}
			}
			
			
		}
		
		this.map.drawEditing(g);
		
		bm.tick(mouse2);
		bm.draw(g);
		
		g.drawRect(150, 40, 25, 25);
		g.drawImage(map.tileTextures.get(map.tileTextureMap.get(this.tileType)), 151, 41, 24, 24, null);
		
		g.drawString("WAVE: " + (map.selectedWave + 1), 130, 205);
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
		
		bm.buttonClicked(arg0);
		
		String which = bm.buttonClicked(arg0);
		
		if(which != null) {
			switch(which) {
			case "Save Map":
				this.writeToFile();
				break;
				
			case "Tile Type":
				this.tileType ++;
				if(this.tileType == this.map.tileTextureMap.size() + 1) {
					this.tileType = 1;
				}
				break;
				
			case "Calculate Lighting":
				this.map.loadMapTileTextures();
				this.map.calculateMapLight();
				break;
				
			case "Add Wave":
				this.map.enemyWaves.add(new ArrayList<int[]>());
				this.map.selectedWave = this.map.enemyWaves.size() - 1;
				break;
				
			case "Prev":
				this.map.selectedWave --;
				this.map.selectedWave = Math.max(this.map.selectedWave, 0);
				break;
				
			case "Next":
				this.map.selectedWave ++;
				this.map.selectedWave = Math.min(this.map.selectedWave, this.map.enemyWaves.size() - 1);
				break;
			}
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
		if(bm.buttonClicked(arg0) != null) {
			bm.pressed(arg0);
		}
		else if(arg0.getButton() == MouseEvent.BUTTON1) {
			this.leftMouse = true;
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3) {
			this.rightMouse = true;
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			this.leftMouse = false;
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3) {
			this.rightMouse = false;
		}
		
		bm.mouseReleased();
	}
		
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if(arg0.getWheelRotation() > 0) {
			GameManager.tileSize /= 1.2;
			GameManager.tileSize = (int) GameManager.tileSize;
			GameManager.tileSize = Math.max(5, GameManager.tileSize);
		}
		else {
			GameManager.tileSize *= 1.2;
			GameManager.tileSize = (int) GameManager.tileSize;
		}
	}

}
