package game;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import decorations.Decoration;
import decorations.Tree;
import enemy.Enemy;
import enemy.Slime;
import entities.Player;
import main.MainPanel;
import state.GameManager;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class Map {
	
	//value of -1 is invis wall
	
	public int[][] map;
	
	public BufferedImage[][] mapTileTexture;
	public double[][] mapTileLight;
	
	public BufferedImage nearLayerBackgroundImage;
	public BufferedImage mapTileImage;	
	
	public ArrayList<ArrayList<double[]>> enemyWaves;	//wave number, enemy number, {enemy type, row, col}
	public int selectedWave = 0;
	
	public ArrayList<double[]> decorations;	//id and location info for decorations within map
	
	public static HashMap<Integer, Integer> tileTextureMap;
	public static ArrayList<BufferedImage> tileTextures;
	public static ArrayList<BufferedImage> cornerTextures;	//corners must be at the bottom left corner
	public static ArrayList<BufferedImage> sideTextures;	//sides must be on the bottom
	
	public Vector playerSpawn;
	
	public boolean drawTileGrid = false;
	public boolean editing = false;
	
	public Map() {
		map = new int[][] {{0}};
		mapTileLight = new double[][] {{0}};
		mapTileTexture = new BufferedImage[][] {{null}};
		
		this.enemyWaves = new ArrayList<ArrayList<double[]>>();
		this.enemyWaves.add(new ArrayList<double[]>());
		
		this.decorations = new ArrayList<double[]>();
		
		this.playerSpawn = new Vector(0, 0);
		
	}
	
	public Map(String filename) {
		map = new int[][] {{0}};
		mapTileTexture = new BufferedImage[][] {{null}};
		
		this.enemyWaves = new ArrayList<ArrayList<double[]>>();
		this.enemyWaves.add(new ArrayList<double[]>());
		
		this.decorations = new ArrayList<double[]>();
		
		this.playerSpawn = new Vector(0, 0);
		
		this.readFromFile(filename);
	}
	
	
	//called on game startup
	public static void loadTileTextures() {
		tileTextures = new ArrayList<BufferedImage>();
		cornerTextures = new ArrayList<BufferedImage>();
		sideTextures = new ArrayList<BufferedImage>();
		
		tileTextureMap = new HashMap<Integer, Integer>();
		
		//loading all textures
		BufferedReader fin;
		InputStream is;
		
		System.out.println("LOADING TEXTURES");
		
		try {
			
			is = Map.class.getResourceAsStream("/texturemap.txt");
			
			fin = new BufferedReader(new InputStreamReader(is));
			
			String line = fin.readLine();
			
			while(line != null) {
				StringTokenizer st = new StringTokenizer(line);
				
				if(st.hasMoreTokens()) {
					int mapNum = Integer.parseInt(st.nextToken());
					
					tileTextureMap.put(mapNum, tileTextures.size());
					
					tileTextures.add(GraphicsTools.loadImage("/Textures/Tiles/" + st.nextToken()));
					sideTextures.add(GraphicsTools.loadImage("/Textures/Tiles/" + st.nextToken()));
					cornerTextures.add(GraphicsTools.loadImage("/Textures/Tiles/" + st.nextToken()));
				}
				
				
				
				line = fin.readLine();
			}
			
		} catch(IOException e) {
			
		}
	}
	
	//pre-bakes the lighting for each tile before we call the render function.
	//for now, it will just calculate the distance from each tile to the nearest 'air' block. Then it will do the lighting based on that.
	//after lighting is calculated, we just draw it all into mapTileImage
	public void calculateMapLight() {
		int width = this.map[0].length;
		int height = this.map.length;
		
		int[][] grid = new int[height][width];
		
		this.mapTileLight = new double[height][width];
		
		Queue<int[]> q = new ArrayDeque<int[]>();		
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(this.map[i][j] == 0 || this.map[i][j] == -1) {
					q.add(new int[] {i, j});
					grid[i][j] = 0;
				}
				else {
					grid[i][j] = -1;
				}
			}
		}
		
		int[] dx = new int[] {1, -1, 0, 0};
		int[] dy = new int[] {0, 0, 1, -1};
		
		while(q.size() != 0) {
				int[] cur = q.poll();
				int curDist = grid[cur[0]][cur[1]];
				for(int i = 0; i < 4; i++) {
					int nextX = cur[1] + dx[i];
					int nextY = cur[0] + dy[i];
					if(nextX >= 0 && nextX < width && nextY >= 0 && nextY < height && (grid[nextY][nextX] == -1)) {
						grid[nextY][nextX] = curDist + 1;
						q.add(new int[] {nextY, nextX});
					}
				}
		}
		
		this.mapTileImage = new BufferedImage((int) (GameManager.tileSize * this.map[0].length), (int) (GameManager.tileSize * this.map.length), BufferedImage.TYPE_INT_ARGB);
		Graphics gTile = this.mapTileImage.getGraphics();
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int next = grid[i][j];
				//System.out.print(next + " ");
				if(next > 0) {
					//baking the lighting into the image
					next = Math.min(next, 5);
					double darkness = 1d - ((double) next / 5d);
					this.mapTileLight[i][j] = darkness;
					this.mapTileTexture[i][j] = GraphicsTools.darkenImage(darkness, this.mapTileTexture[i][j]);
				}
				
				//making an image that is resized to the game tile size
				//it makes rendering images much faster
				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			    GraphicsDevice device = env.getDefaultScreenDevice();
			    GraphicsConfiguration config = device.getDefaultConfiguration();
				BufferedImage resized = config.createCompatibleImage((int) GameManager.tileSize, (int) GameManager.tileSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = (Graphics2D) resized.getGraphics();
				
				g2.drawImage(this.mapTileTexture[i][j], 0, 0, (int) GameManager.tileSize, (int) GameManager.tileSize, null);
				
				int x = (int) (j * GameManager.tileSize);
				int y = (int) (i * GameManager.tileSize);
				
				
				gTile.drawImage(resized, x, y, null);
				
				if(this.editing) {
					this.mapTileTexture[i][j] = resized;
				}
				
			}
			//System.out.println();
		}
		
		
		//drawing tile textures to mapTileImage
		/*
		this.mapTileImage = new BufferedImage((int) (GameManager.tileSize * this.map[0].length), (int) (GameManager.tileSize * this.map.length), BufferedImage.TYPE_INT_ARGB);
		Graphics gTile = this.mapTileImage.getGraphics();
		
		for(int i = 0; i < this.map.length; i++) {
			for(int j = 0; j < this.map[0].length; j++) {
				
				int x = (int) (j * GameManager.tileSize);
				int y = (int) (i * GameManager.tileSize);
				
				if(this.mapTileTexture[i][j] != null) {
					gTile.drawImage(this.mapTileTexture[i][j], x, y, null);
				}
				
			}
		}
		*/	
		
		gTile.dispose();
		
		if(!editing) {
			this.mapTileTexture = new BufferedImage[][] {{null}};
		}
	}
	
	//preloading all the tile images
	//this is so that when drawing frames, we don't have to calculate the tile images
	public void loadMapTileTextures() {
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				this.loadTileTextures(i, j);
			}
		}	
	}
	
	public void loadTileTextures(int i, int j) {
		
		//per tile loading. So we can easily edit textures. Useful for adding or removing blocks
		
		int tileType = map[i][j];
		if(Map.tileTextureMap.containsKey(tileType)) {
			BufferedImage img = Map.tileTextures.get(tileTextureMap.get(tileType));
			this.mapTileTexture[i][j] = GraphicsTools.copyImage(img);
		}
		else {
			
			BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
			boolean change = false;
			
			//sides
			if(i + 1 < map.length && map[i + 1][j] > 0) {
				img = GraphicsTools.combineImages(Map.sideTextures.get(tileTextureMap.get(map[i + 1][j])), img);
				change = true;
			}
			if(i - 1 >= 0 && map[i - 1][j] > 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.sideTextures.get(tileTextureMap.get(map[i - 1][j])), 180), img);
				change = true;
			}
			if(j + 1 < map[0].length && map[i][j + 1] > 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.sideTextures.get(tileTextureMap.get(map[i][j + 1])), 270), img);
				change = true;
			}
			if(j - 1 >= 0 && map[i][j - 1] > 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.sideTextures.get(tileTextureMap.get(map[i][j - 1])), 90), img);
				change = true;
			}
			
			//corners
			if(i + 1 < map.length && j + 1 < map[0].length && map[i + 1][j + 1] > 0 && map[i + 1][j] == map[i][j + 1]) {
				img = GraphicsTools.combineImages(Map.cornerTextures.get(tileTextureMap.get(map[i + 1][j + 1])), img);
				change = true;
			}
			if(i - 1 >= 0 && j + 1 < map[0].length && map[i - 1][j + 1] > 0 && map[i - 1][j] == map[i][j + 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.cornerTextures.get(tileTextureMap.get(map[i - 1][j + 1])), 270), img);
				change = true;
			}
			if(i + 1 < map.length && j - 1 >= 0 && map[i + 1][j - 1] > 0 && map[i + 1][j] == map[i][j - 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.cornerTextures.get(tileTextureMap.get(map[i + 1][j - 1])), 90), img);
				change = true;
			}
			if(i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] > 0 && map[i - 1][j] == map[i][j - 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(Map.cornerTextures.get(tileTextureMap.get(map[i - 1][j - 1])), 180), img);
				change = true;
			}
			
			if(change) {
				this.mapTileTexture[i][j] = img;
			}
			else {
				this.mapTileTexture[i][j] = null;
			}
		}
	}
	
	public void readFromFile(String filename) {
		
		BufferedReader fin;
		InputStream is;
		
		this.enemyWaves = new ArrayList<ArrayList<double[]>>();
		
		System.out.println("LOADING MAP: " + filename);
		
		try {
			
			is = this.getClass().getResourceAsStream("/Maps/" + filename);
			fin = new BufferedReader(new InputStreamReader(is));
			
			StringTokenizer st = new StringTokenizer(fin.readLine());
			double spawnX = Double.parseDouble(st.nextToken());	//row, col
			double spawnY = Double.parseDouble(st.nextToken());
			
			this.playerSpawn = new Vector(spawnX, spawnY);
			
			st = new StringTokenizer(fin.readLine());
			int width = Integer.parseInt(st.nextToken());
			int height = Integer.parseInt(st.nextToken());
			
			map = new int[height][width];
			mapTileTexture = new BufferedImage[height][width];
			mapTileLight = new double[height][width];
			
			for(int i = 0; i < height; i++) {
				st = new StringTokenizer(fin.readLine());
				for(int j = 0; j < width; j++) {
					map[i][j] = Integer.parseInt(st.nextToken());
				}
			}
			
			String line = fin.readLine();
			
			while(line != null) {
				st = new StringTokenizer(line);
				if(st.hasMoreTokens()) {
					String type = st.nextToken();
					
					//this is currently pretty jank rn, but it works. Fix later
					if(type.equals("wave")) {
						this.enemyWaves.add(new ArrayList<double[]>());
					}
					else if(type.equals("decoration")) {
						double decorationType = Double.parseDouble(st.nextToken());
						double row = Double.parseDouble(st.nextToken());
						double col = Double.parseDouble(st.nextToken());
						
						this.decorations.add(new double[] {decorationType, row, col});
					}
					else {
						
						double row = Double.parseDouble(st.nextToken());
						double col = Double.parseDouble(st.nextToken());
						
						this.enemyWaves.get(this.enemyWaves.size() - 1).add(new double[] {Double.parseDouble(type), row, col});
						
					}
				}
				line = fin.readLine();
			}
			
		} catch(IOException e) {
			System.out.println("MAP LOADING FAILED");
		}
		
		this.loadMapTileTextures();
		
	}
	
	public BufferedImage readTextureFromFile(String filename) {
		
		InputStream is;
		BufferedImage img = null;
		
		System.out.print("LOADING TEXTURE: " + filename);
		
		try {
			
			is = this.getClass().getResourceAsStream("/Textures/" + filename);
			img = ImageIO.read(is);
			
			System.out.println(" SUCCESS");
			
		} catch(IOException e) {
			System.out.println(" FAILED");
			System.out.println(e); 
			System.exit(0);
		}
		
		return img;
		
	}
	
	//returns true if all waves are cleared
	public boolean spawnNextWave(int xOffset, int yOffset) {
		if(this.selectedWave >= this.enemyWaves.size()) {
			return true;
		}
		for(double[] i : enemyWaves.get(this.selectedWave)) {
			int type = (int) i[0];
			double x = i[2];
			double y = i[1];
			
			switch(type) {
			case Enemy.SLIME:
				GameManager.enemies.add(new Slime(new Vector(x + xOffset, y + yOffset)));
				break;
			}
		}
		this.selectedWave ++;
		return false;
	}
	
	public ArrayList<Decoration> getDecorations() {
		ArrayList<Decoration> ans = new ArrayList<Decoration>();
		for(double[] i : decorations) {
			int type = (int) i[0];
			double x = i[2];
			double y = i[1];
			
			ans.add(Decoration.getDecoration(type, new Vector(x, y)));
		}
		return ans;
	}
	
	//generates the near background image
	public void generateNearBackground(int tileType) {
		
		this.nearLayerBackgroundImage = new BufferedImage((int) (GameManager.tileSize * this.map[0].length), (int) (GameManager.tileSize * this.map.length), BufferedImage.TYPE_INT_ARGB);
		Graphics gBackground = this.nearLayerBackgroundImage.getGraphics();
		
		if(tileType == -1) {	//set near background to be clear
			return;
		}
		
		for(int i = 0; i < this.map.length; i++) {
			for(int j = 0; j < this.map[0].length; j++) {
				
				int x = (int) (j * GameManager.tileSize);
				int y = (int) (i * GameManager.tileSize);
				
				if(this.map[i][j] <= 0) {
					BufferedImage nextTile = GraphicsTools.copyImage(tileTextures.get(tileType));
					nextTile = GraphicsTools.darkenImage(0.5, nextTile);
					gBackground.drawImage(nextTile, x, y, (int) (GameManager.tileSize), (int) (GameManager.tileSize), null);
				}
				
			}
		}
		
		gBackground.dispose();
	}
	
	//drawing the tiles that correspond to the hitboxes in the map
	public void draw(Graphics g) {
		
		//old tile based map rendering
		
		/*
		int minX = (int) (GameManager.cameraOffset.x / GameManager.tileSize - (MainPanel.WIDTH / 2) / GameManager.tileSize);
		int minY = (int) (GameManager.cameraOffset.y / GameManager.tileSize - (MainPanel.HEIGHT / 2) / GameManager.tileSize);
		
		int maxX = (int) (GameManager.cameraOffset.x / GameManager.tileSize + (MainPanel.WIDTH / 2) / GameManager.tileSize + 2);
		int maxY = (int) (GameManager.cameraOffset.y / GameManager.tileSize + (MainPanel.HEIGHT / 2) / GameManager.tileSize + 2);
		
		for(int i = Math.max(minY, 0); i < Math.min(maxY, map.length); i++) {
			for(int j = Math.max(minX, 0); j < Math.min(maxX, map[0].length); j++) {
				
				int x = (int) (j * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2);
				int y = (int) (i * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2);
				
				if(x > -GameManager.tileSize && x < MainPanel.WIDTH + GameManager.tileSize && y > -GameManager.tileSize && y < MainPanel.HEIGHT + GameManager.tileSize && this.mapTileTexture != null) {
					
					if(this.mapTileLight[i][j] > 0 || this.map[i][j] == 0 || this.map[i][j] == -1) {
						g.drawImage(this.mapTileTexture[i][j], x, y, null);
					}
					s
					//g.drawImage(this.mapTileTexture[i][j], x, y, (int) GameManager.tileSize, (int) GameManager.tileSize, null);
					
					if(this.drawTileGrid) {
						g.drawRect(x, y, (int) GameManager.tileSize, (int) GameManager.tileSize);
					}
				}s
				
				
			}
		}	
		*/
		
		//draw the black bars around the map boundaries		
		double top = -GameManager.cameraOffset.y + (double) MainPanel.HEIGHT / 2d;
		double left = -GameManager.cameraOffset.x + (double) MainPanel.WIDTH / 2d;
		double bottom = top + this.map.length * GameManager.tileSize;
		double right = left + this.map[0].length * GameManager.tileSize;
		
		g.setColor(Color.BLACK);
		
		if(top >= 0) {
			g.fillRect(0, 0, MainPanel.WIDTH, (int) top + 1);
		}
		if(left >= 0) {
			g.fillRect(0, 0, (int) left + 1, MainPanel.HEIGHT);
		}
		if(bottom <= MainPanel.HEIGHT) {
			g.fillRect(0, (int) bottom - 1, MainPanel.WIDTH, MainPanel.HEIGHT - (int) bottom + 1);
		}
		if(right <= MainPanel.WIDTH) {
			g.fillRect((int) right - 1, 0, MainPanel.WIDTH - (int) right + 1, MainPanel.HEIGHT);
		}

		
		//draw the tiles
		g.drawImage(this.mapTileImage, (int) -GameManager.cameraOffset.x + MainPanel.WIDTH / 2, (int) -GameManager.cameraOffset.y + MainPanel.HEIGHT / 2, null);
		
	}
	
	//drawing the near and far layer backgrounds
	//far layer background consists of pngs that scroll
	//near layer background consists of a png that is made of tile textures. 
	//there might be procedurally generated holes in the near layer background so that the far layer one is visible.
	public void drawBackground(Graphics g) {
		g.drawImage(this.nearLayerBackgroundImage, (int) (-GameManager.cameraOffset.x + (double) MainPanel.WIDTH / 2d), (int) (-GameManager.cameraOffset.y + (double) MainPanel.HEIGHT / 2d), null);
	}
	
	
	//the version of draw that you use when you want to edit
	public void drawEditing(Graphics g) {
		
		for(double[] i : decorations) {
			int type = (int) i[0];
			double x = i[2];
			double y = i[1];
			
			Decoration.getDecoration(type, new Vector(x, y)).draw(g);
		}
		
		int minX = (int) (GameManager.cameraOffset.x / GameManager.tileSize - (MainPanel.WIDTH / 2) / GameManager.tileSize);
		int minY = (int) (GameManager.cameraOffset.y / GameManager.tileSize - (MainPanel.HEIGHT / 2) / GameManager.tileSize);
		
		int maxX = (int) (GameManager.cameraOffset.x / GameManager.tileSize + (MainPanel.WIDTH / 2) / GameManager.tileSize + 2);
		int maxY = (int) (GameManager.cameraOffset.y / GameManager.tileSize + (MainPanel.HEIGHT / 2) / GameManager.tileSize + 2);
		
		for(int i = Math.max(minY, 0); i < Math.min(maxY, map.length); i++) {
			for(int j = Math.max(minX, 0); j < Math.min(maxX, map[0].length); j++) {
				
				int x = (int) (j * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2);
				int y = (int) (i * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2);
				
				if(x > -GameManager.tileSize && x < MainPanel.WIDTH + GameManager.tileSize && y > -GameManager.tileSize && y < MainPanel.HEIGHT + GameManager.tileSize && this.mapTileTexture != null) {
					
					//the only change
					//g.drawImage(this.mapTileTexture[i][j], x, y, null);
					g.drawImage(this.mapTileTexture[i][j], x, y, (int) GameManager.tileSize, (int) GameManager.tileSize, null);
					
					if(this.drawTileGrid) {
						g.drawRect(x, y, (int) GameManager.tileSize, (int) GameManager.tileSize);
					}
				}
				
				
			}
		}
		
		if(this.enemyWaves.size() != 0) {
			for(double[] i : this.enemyWaves.get(selectedWave)) {
				
				int enemyType = (int) i[0];
				double x = i[2];
				double y = i[1];
				
				Enemy.getEnemy(enemyType, new Vector(x, y)).draw(g);
				
			}
		}
		
		if(this.playerSpawn != null) {
			Player p = new Player(this.playerSpawn);
			p.drawHitboxes(g);
		}
		
		
	}
	
}
