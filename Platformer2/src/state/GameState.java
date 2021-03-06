package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import button.ButtonManager;
import decorations.Decoration;
import enemy.Enemy;
import enemy.Slime;
import game.GamePanel;
import game.Map;
import game.TilesetManager;
import item.Coin;
import item.Item;
import main.MainPanel;
import melee.MeleeAttack;
import particles.Particle;
import player.Player;
import util.GraphicsTools;
import util.Vector;
import util.Point;

public class GameState extends State{
	
	//this is for the actual gameplay
	
	//start at the left in a spawn room, then go room by room to the right. 
	//Each time a new room is reached, some event should trigger that closes all the doors to said room. 
	//then the normal wave spawn logic should take place.
	
	//as we load each map tile, we can save the map objects in a queue. That way when the player 
	//is traversing through them, we can just send the waves at them until they clear all of them, or die. 
	//then non-wave logic ensues until another room is triggered, or the end stage is reached.
	
	//5 enemy rooms per layer.
	//3 layers per depth level.
	
	//rooms will be 60 tiles tall. the width of a room can vary, but minimum 10 tiles in width
	//waves will trigger when the player goes past the first 5 tiles of the new room
	
	//end cap tiles (the left and rightmost tiles) should have no doors that would lead the player out of bounds.
	//non-end cap tiles have to have two doors, one to either side.
	
	public boolean inLevel = false;	//if the player has triggered a level start, this should be true
	public int curLevelNum = 0;
	public Map curLevel;
	
	public boolean wavePause = true;	//the pause between waves
	//during the pause, a big "Wave x" should display
	
	public int wavePauseTime = 240;
	public int wavePauseTimer;
	public double wavePopupOpacity = 1d;
	
	public int stageClearPopupTime = 120;
	public int stageClearPopupTimer;
	
	public int levelsRemaining;
	
	public boolean win = false;
	
	public GamePanel gp;
	
	public Map map;
	
	public ArrayList<Map> levelTiles;
	public ArrayList<Integer> levelTileOffsets;
	
	public ButtonManager bm;
	
	public GameState(int levelsRemaining) {
		
		this.levelsRemaining = levelsRemaining;
		
	}


	public void init() {
		
		this.stageClearPopupTimer = 0;
		
		this.map = new Map();
		
		this.levelTileOffsets = new ArrayList<Integer>();
		this.levelTiles = new ArrayList<Map>();
		
		ArrayList<String> tiles = TilesetManager.generateTiles();
		
		int height = 60;
		int width = 0;
		
		for(int i = 0; i < tiles.size(); i++) {
			Map nextTile = new Map(tiles.get(i));
			
			levelTileOffsets.add(width);
			levelTiles.add(nextTile);
			
			nextTile.mapTileTexture = new BufferedImage[][] {{null}};
			
			width += nextTile.map[0].length;
			width ++;
			
			
		}
		
		width --;
		
		map.map = new int[height][width];
		map.mapTileTexture = new BufferedImage[height][width];
		
		
		int offset = 0;
		for(int i = 0; i < tiles.size(); i++) {
			Map nextTile = levelTiles.get(i);
			
			//importing tile data
			for(int y = 0; y < nextTile.map.length; y++) {
				for(int x = 0; x < nextTile.map[0].length; x++) {
					map.map[y][x + offset] = nextTile.map[y][x];
				}
			}
			
			//importing decoration data
			for(double[] d : nextTile.decorations) {
				int decorationType = (int) d[0];
				double row = d[1];
				double col = d[2];
				
				col += offset;
				
				this.map.decorations.add(new double[] {decorationType, row, col});
			}
			
			//importing loot data
			for(double[] d : nextTile.loot) {
				int lootRarity = (int) d[0];
				double row = d[1];
				double col = d[2];
				
				col += offset;
				
				this.map.loot.add(new double[] {lootRarity, row, col});
			}
			
			offset += nextTile.map[0].length + 1;
			
			if(i != tiles.size() - 1) {
				for(int y = 0; y < height; y++) {
					if(y >= 27 && y <= 32) {
						map.map[y][offset - 1] = 0;
					}
					else {
						map.map[y][offset - 1] = 2;
					}
				}
			}
		}
		this.map.loadMapTileTextures();
		this.map.generateNearBackground(0);
		this.map.playerSpawn = levelTiles.get(0).playerSpawn;
		this.wavePauseTimer = this.wavePauseTime;;
		
		
		this.gp = new GamePanel(this.map);	//resets all entities
		
		this.map.placeLoot();
		
		
		
		
		if(this.gp == null) {
			System.out.println("NULL");
			System.exit(0);
		}
		
	}
	
	//the player has reached the elevator at the end of the level
	//if there are more layers, then just switch the layer, else, send them back to the hub.
	public void nextLayer() {
		if(this.levelsRemaining >= 2) {
			GameManager.transition(new BuffState(this.levelsRemaining - 1), "Levels Remaining: " + (this.levelsRemaining - 1));
		}
		else {
			GameManager.transition(new HubState(), "Depth Cleared");
		}
	}

	public void tick(java.awt.Point mouse2) {}


	public void draw(Graphics g, java.awt.Point mouse) {
		
		if(!this.gp.pause) {
			//doing enemy wave logic and drawing the hud
			//everything else is handled in the game panel object
			
			while(this.curLevelNum < this.levelTiles.size() && !this.levelTiles.get(curLevelNum).hasEnemies) {
				this.curLevelNum ++;
			}
			
			//if a level isn't currently active, then check if the player has triggered a next level
			
			if(!this.inLevel) {
				
				this.stageClearPopupTimer --;
				
				if(this.curLevelNum < this.levelTileOffsets.size()) {
					if(GameManager.player.pos.x > this.levelTileOffsets.get(curLevelNum) + 5) {
						this.inLevel = true;
						this.curLevel = this.levelTiles.get(this.curLevelNum);
						this.wavePauseTimer = this.wavePauseTime;
						//System.out.println("NEXT WAVE");
						
						//turning on the barriers
						for(int i = 1; i < this.levelTileOffsets.size(); i++) {
							int x = this.levelTileOffsets.get(i) - 1;
							//System.out.print(x + " ");
							for(int y = 27; y <= 32; y++) {
								this.map.map[y][x] = -1;
							}
						}
						//System.out.println();
					}
				}
				
			}
			//if a level is active, then do the spawning logic
			else if(inLevel){
				//spawn new enemies
				if(GameManager.enemies.size() == 0 && !this.wavePause) {
					if(this.curLevel.selectedWave >= this.curLevel.enemyWaves.size()) {
						this.inLevel = false;
						this.curLevelNum ++;
						
						//turning off the barriers
						for(int i = 1; i < this.levelTileOffsets.size(); i++) {
							int x = this.levelTileOffsets.get(i) - 1;
							for(int y = 27; y <= 32; y++) {
								this.map.map[y][x] = 0;
							}
						}
						
						this.stageClearPopupTimer = this.stageClearPopupTime;
					}
					else {
						this.wavePause = true;
						this.wavePauseTimer = this.wavePauseTime;
						//this.map.spawnNextWave();
					}
				}
				
				if(wavePause) {
					this.wavePauseTimer --;
					if(this.wavePauseTimer <= 0) {
						this.wavePause = false;
						this.curLevel.spawnNextWave(this.levelTileOffsets.get(curLevelNum), 0);
					}
				}
			}
			
		}
		
		this.gp.draw(g, mouse);
	
		if(this.wavePause && this.inLevel) {
			String popup = "Wave " + (this.curLevel.selectedWave + 1);
			Font popupFont = new Font("Georgia", Font.BOLD, 48);
			
			int popupWidth = GraphicsTools.calculateTextWidth(popup, popupFont);
			
			this.wavePopupOpacity = ((double) this.wavePauseTimer / (double) this.wavePauseTime);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(popupFont);
			g2.setComposite(GraphicsTools.makeComposite(this.wavePopupOpacity));
			g2.setColor(Color.WHITE);
			g2.drawString(popup, MainPanel.WIDTH / 2 - popupWidth / 2, MainPanel.HEIGHT / 4);
			
		}
		else if(this.stageClearPopupTimer > 0) {
			String popup = "Stage Clear";
			Font popupFont = new Font("Georgia", Font.BOLD, 48);
			
			int popupWidth = GraphicsTools.calculateTextWidth(popup, popupFont);
			
			double opacity = ((double) this.stageClearPopupTimer / (double) this.stageClearPopupTime);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(popupFont);
			g2.setComposite(GraphicsTools.makeComposite(opacity));
			g2.setColor(Color.WHITE);
			g2.drawString(popup, MainPanel.WIDTH / 2 - popupWidth / 2, MainPanel.HEIGHT / 4);
		}
		
		if(GameManager.player.health <= 0) {
			GameManager.transition(new HubState(), "You Died");
		}
		
	}

	
	public void keyPressed(int k) {
//		if(k == KeyEvent.VK_ESCAPE) {
//			this.pause = !this.pause;
//		}
//		if(!pause) {
//			player.keyPressed(k);
//		}
		gp.keyPressed(k);
	}

	
	public void keyReleased(int k) {
		gp.keyReleased(k);
//		if(!pause) {
//			player.keyReleased(k);
//		}
	}

	
	public void keyTyped(int k) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseClicked(MouseEvent arg0) {
		if(this.gp.pause) {
			//pause menu buttons
		}
	}

	
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mousePressed(MouseEvent arg0) {
//		if(!pause) {
//			player.mousePressed(arg0);
//		}
//		else {
//			
//		}
		
		gp.mousePressed(arg0);
	}

	
	public void mouseReleased(MouseEvent arg0) {
//		if(!pause) {
//			player.mouseReleased();
//		}
		gp.mouseReleased(arg0);
	}

	
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
