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
import java.util.Stack;

import button.Button;
import button.ButtonManager;
import button.ToggleButton;
import decorations.Decoration;
import enemy.Enemy;
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
	
	public boolean rectMode = false;
	public Point rectPress;
	public Point rectRelease;
	public boolean leftMouseReleased = false;
	
	public boolean placeDecoration = false;
	public int decorationType = 4;
	
	public boolean placePlayerSpawn = false;
	
	public boolean fillMode = false;
	
	public int tileType = 1;
	
	public boolean placeLoot = false;
	public int lootRarity = 0;
	
	public Vector cameraPos;

	public EditorState(String filepath) {
				
		bm = new ButtonManager();
		
		bm.addButton(new Button(10, 10, 100, 25, "Save Map"));
		bm.addButton(new Button(10, 40, 100, 25, "Tile Type"));
		bm.addButton(new Button(10, 130, 100, 25, "Calculate Lighting"));
		bm.addButton(new Button(10, 190, 100, 25, "Add Wave"));
		bm.addButton(new Button(10, 220, 50, 25, "Prev"));
		bm.addButton(new Button(60, 220, 50, 25, "Next"));
		bm.addButton(new Button(10, 280, 50, 25, "Prev Dec"));
		bm.addButton(new Button(60, 280, 50, 25, "Next Dec"));
		bm.addButton(new Button(10, 340, 50, 25, "Player Spawn"));
		
		
		bm.addToggleButton(new ToggleButton(10, 70, 100, 25, "Erase"));	bm.toggleButtons.get(0).setToggled(false);
		bm.addToggleButton(new ToggleButton(10, 100, 100, 25, "Draw Grid"));	bm.toggleButtons.get(1).setToggled(true);
		bm.addToggleButton(new ToggleButton(10, 160, 100, 25, "Place Enemy"));	bm.toggleButtons.get(2).setToggled(false);
		bm.addToggleButton(new ToggleButton(10, 250, 100, 25, "Place Decoration"));	bm.toggleButtons.get(3).setToggled(false);
		bm.addToggleButton(new ToggleButton(10, 310, 100, 25, "Rect Mode"));	bm.toggleButtons.get(4).setToggled(false);	
		bm.addToggleButton(new ToggleButton(10, 370, 100, 25, "Fill Mode"));	bm.toggleButtons.get(5).setToggled(false);
		bm.addToggleButton(new ToggleButton(10, 400, 100, 25, "Place Loot"));	bm.toggleButtons.get(6).setToggled(false);
		
		this.map = new Map();
		this.map.drawTileGrid = true;
		this.map.editing = true;
		
		int width = 60;
		int height = 60;
		
		this.map.map = new int[height][width];
		
		this.map.mapTileTexture = new BufferedImage[map.map.length][map.map[0].length];
		
		this.map.loadMapTileTextures();
		
		this.map.readFromFile(filepath);
		//this.map.calculateMapLight();
		
		this.cameraPos = new Vector(this.map.map[0].length / 2, this.map.map.length / 2);
		this.cameraOffset = new Vector((int) ((cameraPos.x) * GameManager.tileSize), (int) ((cameraPos.y) * GameManager.tileSize));
		
	}
	
	@Override
	public void init() {
		
	}

	
	public void writeToFile() {
		int width = this.map.map[0].length;
		int height = this.map.map.length;
		
		MainPanel.fout.println(this.map.playerSpawn.y + " " + this.map.playerSpawn.x);
		MainPanel.fout.println(width + " " + height);
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				MainPanel.fout.print(this.map.map[i][j] + " ");
			}
			MainPanel.fout.println();
		}
		for(ArrayList<double[]> a : this.map.enemyWaves) {
			MainPanel.fout.println("wave");
			for(double[] i : a) {
				MainPanel.fout.println(i[0] + " " + i[1] + " " + i[2]);
			}
		}
		
		for(double[] i : this.map.decorations) {
			MainPanel.fout.println("decoration " + ((int) i[0]) + " "  + i[1] + " " + i[2]);
		}
		
		for(double[] i : this.map.loot) {
			MainPanel.fout.println("loot " + ((int) i[0]) + " " + i[1] + " " + i[2]);
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
		this.placeDecoration = bm.toggleButtons.get(3).getToggled();
		this.rectMode = bm.toggleButtons.get(4).getToggled();
		this.fillMode = bm.toggleButtons.get(5).getToggled();
		this.placeLoot = bm.toggleButtons.get(6).getToggled();
		
		double xDiff = mouse2.x - mouse.x;
		double yDiff = mouse2.y - mouse.y;
		
		if(this.rightMouse) {
			this.cameraPos.addVector(new Vector(-xDiff / GameManager.tileSize, -yDiff / GameManager.tileSize));
		}
		GameManager.cameraOffset = new Vector((int) ((cameraPos.x) * GameManager.tileSize), (int) ((cameraPos.y) * GameManager.tileSize));
		
		this.mouse = new util.Point(mouse2.x, mouse2.y);
		
		//calculating where on the map the mouse is
		//just do the opposite transformation required to go from map space to screen space
		double mapX = ((mouse.x + GameManager.cameraOffset.x - MainPanel.WIDTH / 2) / GameManager.tileSize);
		double mapY = ((mouse.y + GameManager.cameraOffset.y - MainPanel.HEIGHT / 2) / GameManager.tileSize);
		mapX = Math.round(mapX * 2d) / 2d;
		mapY = Math.round(mapY * 2d) / 2d;
		
		if(this.placePlayerSpawn) {
			if(this.leftMouse) {
				this.placePlayerSpawn = false;
				this.map.playerSpawn = new Vector(mapX, mapY);
				return;
			}
		}
		
		if(!this.erase) {
			if(this.placeEnemy) {
				Enemy.getEnemy(enemyType, new Vector(mapX, mapY)).draw(g);
			}
			else if(this.placeDecoration) {
				Decoration.getDecoration(decorationType, new Vector(mapX, mapY)).draw(g);
			}
			else if(this.placeLoot) {
				//draw a string displaying the rarity of the currently held item
			}
			else {
				//maybe you could draw the tile. Not needed though.
			}
		}
		
		if(this.leftMouseReleased) {
			this.leftMouseReleased = false;
			if(this.rectMode) {
				int startMapX = (int) ((this.rectPress.x + GameManager.cameraOffset.x - MainPanel.WIDTH / 2) / GameManager.tileSize);
				int startMapY = (int) ((this.rectPress.y + GameManager.cameraOffset.y - MainPanel.HEIGHT / 2) / GameManager.tileSize);
				
				int endMapX = (int) ((this.rectRelease.x + GameManager.cameraOffset.x - MainPanel.WIDTH / 2) / GameManager.tileSize);
				int endMapY = (int) ((this.rectRelease.y + GameManager.cameraOffset.y - MainPanel.HEIGHT / 2) / GameManager.tileSize);
				
				if(startMapX > endMapX) {
					int temp = startMapX;
					startMapX = endMapX;
					endMapX = temp;
				}
				
				if(startMapY > endMapY) {
					int temp = startMapY;
					startMapY = endMapY;
					endMapY = temp;
				}
				
				if(this.erase) {
					for(int i = Math.max(startMapY, 0); i < Math.min(endMapY + 1, this.map.map.length); i++) {
						for(int j = Math.max(startMapX, 0); j < Math.min(endMapX + 1, this.map.map[0].length); j++) {
							this.placeTile(j, i, 0);
						}
					}
				}
				
				else {
					for(int i = Math.max(startMapY, 0); i < Math.min(endMapY + 1, this.map.map.length); i++) {
						for(int j = Math.max(startMapX, 0); j < Math.min(endMapX + 1, this.map.map[0].length); j++) {
							
							this.placeTile(j, i, this.tileType);
						}
					}
				}
			}
		}
		
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
						this.map.enemyWaves.get(this.map.selectedWave).add(new double[] {this.enemyType, mapY, mapX});
					}	
				}
			}
			else if(this.placeDecoration) {
				if(this.erase) {
					for(int i = 0; i < this.map.decorations.size(); i++) {
						if(this.map.decorations.get(i)[1] == mapY && this.map.decorations.get(i)[2] == mapX) {
							this.map.decorations.remove(i);
							break;
						}
					}
				}
				else {
					boolean contains = false;
					for(double[] i : this.map.decorations) {
						if(i[0] == this.decorationType && i[1] == mapY && i[2] == mapX) {
							contains = true;
						}
					}
					if(!contains) {
						this.map.decorations.add(new double[] {this.decorationType, mapY, mapX});
					}
				}
			}
			else if(this.placeLoot) {
				if(this.erase) {
					for(int i = 0; i < this.map.loot.size(); i++) {
						if(this.map.loot.get(i)[1] == mapY && this.map.loot.get(i)[2] == mapX) {
							this.map.loot.remove(i);
							break;
						}
					}
				}
				else {
					boolean contains = false;	//TODO
					for(double[] i : this.map.loot) {
						if(i[1] == mapY && i[2] == mapX) {
							contains = true;
						}
					}
					if(!contains) {
						this.map.loot.add(new double[] {this.lootRarity, mapY, mapX});
					}
				}
			}
			else {
				if(mapX >= 0 && mapX < this.map.map[0].length && mapY >= 0 && mapY < this.map.map.length) {
					if(this.fillMode) {
						if(this.map.map[(int) mapY][(int) mapX] == 0) {
							Stack<int[]> s = new Stack<int[]>();
							s.add(new int[] {(int) mapY, (int) mapX});
							
							
							int[] dx = new int[] {-1, 1, 0, 0};
							int[] dy = new int[] {0, 0, -1, 1};
							
							while(s.size() != 0) {
								int[] cur = s.pop();
								int curY = cur[0];
								int curX = cur[1];
								this.map.map[curY][curX] = this.tileType;
								this.map.loadTileTextures(curY, curX);
								for(int i = 0; i < 4; i++) {
									int nextY = curY + dy[i];
									int nextX = curX + dx[i];
									if(this.map.map[nextY][nextX] == 0) {
										s.add(new int[] {nextY, nextX});
									}
								}
							}
						}
					}
					else {
					
						if(erase && this.map.map[(int) mapY][(int) mapX] != 0) {
							this.map.map[(int) mapY][(int) mapX] = 0;
							for(int i = (int) mapY - 1; i <= (int) mapY + 1; i++) {
								for(int j = (int) mapX - 1; j <= (int) mapX + 1; j++) {
									if(i >= 0 && i < this.map.map.length && j >= 0 && j < this.map.map[0].length) {
										this.map.loadTileTextures(i, j);
									}
								}
							}
						}
						else if(!erase){
							this.map.map[(int) mapY][(int) mapX] = this.tileType;
							for(int i = (int) mapY - 1; i <= (int) mapY + 1; i++) {
								for(int j = (int) mapX - 1; j <= (int) mapX + 1; j++) {
									if(i >= 0 && i < this.map.map.length && j >= 0 && j < this.map.map[0].length) {
										this.map.loadTileTextures(i, j);
									}
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
		g.drawImage(Map.tileTextures.get(Map.tileTextureMap.get(this.tileType)), 151, 41, 24, 24, null);
		
		g.drawString("WAVE: " + (map.selectedWave + 1), 130, 205);
	}
	
	public void placeTile(int x, int y, int tileType) {
		if(x >= 0 && x < this.map.map[0].length && y >= 0 && y < this.map.map.length) {
			this.map.map[y][x] = tileType;
			
			for(int i = (int) y - 1; i <= (int) y + 1; i++) {
				for(int j = (int) x - 1; j <= (int) x + 1; j++) {
					if(i >= 0 && i < this.map.map.length && j >= 0 && j < this.map.map[0].length) {
						this.map.loadTileTextures(i, j);
					}
				}
			}
		}
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
			this.leftMouseReleased = false;
			switch(which) {
			case "Save Map":
				this.writeToFile();
				break;
				
			case "Tile Type":
				this.tileType ++;
				if(this.tileType == Map.tileTextureMap.size() + 1) {
					this.tileType = 1;
				}
				break;
				
			case "Calculate Lighting":
				this.map.loadMapTileTextures();
				this.map.calculateMapLight();
				break;
				
			case "Add Wave":
				this.map.enemyWaves.add(new ArrayList<double[]>());
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
				
			case "Prev Dec":
				this.decorationType --;
				decorationType = Math.max(decorationType, 0);
				break;
				
			case "Next Dec":
				this.decorationType ++;
				decorationType = Math.min(decorationType, 4);
				break;
				
			case "Player Spawn":
				this.placePlayerSpawn = true;
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
			this.rectPress = new Point(arg0.getX(), arg0.getY());
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3) {
			this.rightMouse = true;
		}
		
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			this.leftMouse = false;
			this.leftMouseReleased = true;
			this.rectRelease = new Point(arg0.getX(), arg0.getY());
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
