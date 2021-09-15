package game;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import entities.Enemy;
import entities.Slime;
import main.MainPanel;
import util.GraphicsTools;
import util.Point;
import util.Vector;

public class Map {
	
	public int[][] map;
	
	public BufferedImage[][] mapTileTexture;
	
	public ArrayList<ArrayList<int[]>> enemyWaves;	//wave number, enemy number, {enemy type, row, col}
	public int selectedWave = 0;
	
	public HashMap<Integer, Integer> tileTextureMap;
	
	public BufferedImage errorTileTexture;
	public BufferedImage errorCornerTexture;
	public BufferedImage errorSideTexture;
	
	public ArrayList<BufferedImage> tileTextures;
	public ArrayList<BufferedImage> cornerTextures;	//corners must be at the bottom left corner
	public ArrayList<BufferedImage> sideTextures;	//sides must be on the bottom
	
	public boolean drawTileGrid = false;
	public boolean editing = false;
	
	public Map() {
		map = new int[][] {{0}};
		mapTileTexture = new BufferedImage[][] {{null}};
		
		this.enemyWaves = new ArrayList<ArrayList<int[]>>();
		this.enemyWaves.add(new ArrayList<int[]>());
		
		this.tileTextures = new ArrayList<BufferedImage>();
		this.cornerTextures = new ArrayList<BufferedImage>();
		this.sideTextures = new ArrayList<BufferedImage>();
		
		this.tileTextureMap = new HashMap<Integer, Integer>();
		
		//loading all textures
		BufferedReader fin;
		InputStream is;
		
		System.out.println("LOADING TEXTURES");
		
		try {
			
			is = this.getClass().getResourceAsStream("/texturemap.txt");
			
			fin = new BufferedReader(new InputStreamReader(is));
			
			String line = fin.readLine();
			
			while(line != null) {
				StringTokenizer st = new StringTokenizer(line);
				
				if(st.hasMoreTokens()) {
					int mapNum = Integer.parseInt(st.nextToken());
					
					this.tileTextureMap.put(mapNum, this.tileTextures.size());
					
					this.tileTextures.add(this.readTextureFromFile(st.nextToken()));
					this.sideTextures.add(this.readTextureFromFile(st.nextToken()));
					this.cornerTextures.add(this.readTextureFromFile(st.nextToken()));
				}
				
				
				
				line = fin.readLine();
			}
			
		} catch(IOException e) {
			
		}
		
//		System.out.println(this.textureMap);
//		System.out.println(this.tileTextures.size());
//		System.out.println(this.sideTextures.size());
	}
	
	//pre-bakes the lighting for each tile before we call the render function.
	//for now, it will just calculate the distance from each tile to the nearest 'air' block. Then it will do the lighting based on that.
	public void calculateMapLight() {
		int width = this.map[0].length;
		int height = this.map.length;
		
		int[][] grid = new int[width][height];
		
		Queue<int[]> q = new ArrayDeque<int[]>();
		Queue<Integer> dist = new ArrayDeque<Integer>();
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(this.map[i][j] == 0) {
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
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int next = grid[i][j];
				//System.out.print(next + " ");
				if(next != 0) {
					//baking the lighting into the image
					next = Math.min(next, 5);
					double darkness = 1d - ((double) next / 5d);
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
				
				this.mapTileTexture[i][j] = resized;
			}
			//System.out.println();
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
		if(this.tileTextureMap.containsKey(tileType)) {
			BufferedImage img = this.tileTextures.get(tileTextureMap.get(tileType));
			this.mapTileTexture[i][j] = GraphicsTools.copyImage(img);
		}
		else {
			
			BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
			boolean change = false;
			
			//sides
			if(i + 1 < map.length && map[i + 1][j] != 0) {
				img = GraphicsTools.combineImages(this.sideTextures.get(tileTextureMap.get(map[i + 1][j])), img);
				change = true;
			}
			if(i - 1 >= 0 && map[i - 1][j] != 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.sideTextures.get(tileTextureMap.get(map[i - 1][j])), 180), img);
				change = true;
			}
			if(j + 1 < map[0].length && map[i][j + 1] != 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.sideTextures.get(tileTextureMap.get(map[i][j + 1])), 270), img);
				change = true;
			}
			if(j - 1 >= 0 && map[i][j - 1] != 0) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.sideTextures.get(tileTextureMap.get(map[i][j - 1])), 90), img);
				change = true;
			}
			
			//corners
			if(i + 1 < map.length && j + 1 < map[0].length && map[i + 1][j + 1] != 0 && map[i + 1][j] == map[i][j + 1]) {
				img = GraphicsTools.combineImages(this.cornerTextures.get(tileTextureMap.get(map[i + 1][j + 1])), img);
				change = true;
			}
			if(i - 1 >= 0 && j + 1 < map[0].length && map[i - 1][j + 1] != 0 && map[i - 1][j] == map[i][j + 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.cornerTextures.get(tileTextureMap.get(map[i - 1][j + 1])), 270), img);
				change = true;
			}
			if(i + 1 < map.length && j - 1 >= 0 && map[i + 1][j - 1] != 0 && map[i + 1][j] == map[i][j - 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.cornerTextures.get(tileTextureMap.get(map[i + 1][j - 1])), 90), img);
				change = true;
			}
			if(i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] != 0 && map[i - 1][j] == map[i][j - 1]) {
				img = GraphicsTools.combineImages(GraphicsTools.rotateImageByDegrees(this.cornerTextures.get(tileTextureMap.get(map[i - 1][j - 1])), 180), img);
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
		
		this.enemyWaves = new ArrayList<ArrayList<int[]>>();
		
		System.out.println("LOADING MAP: " + filename);
		
		try {
			
			is = this.getClass().getResourceAsStream("/Maps/" + filename);
			fin = new BufferedReader(new InputStreamReader(is));
			
			StringTokenizer st = new StringTokenizer(fin.readLine());
			int width = Integer.parseInt(st.nextToken());
			int height = Integer.parseInt(st.nextToken());
			
			map = new int[height][width];
			mapTileTexture = new BufferedImage[height][width];
			
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
					if(type.equals("wave")) {
						this.enemyWaves.add(new ArrayList<int[]>());
					}
					else {
						
						int row = Integer.parseInt(st.nextToken());
						int col = Integer.parseInt(st.nextToken());
						
						this.enemyWaves.get(this.enemyWaves.size() - 1).add(new int[] {Integer.parseInt(type), row, col});
						
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
	
	public void spawnNextWave() {
		if(this.selectedWave >= this.enemyWaves.size()) {
			return;
		}
		for(int[] i : enemyWaves.get(this.selectedWave)) {
			int type = i[0];
			int x = i[2];
			int y = i[1];
			
			switch(type) {
			case Enemy.SLIME:
				GameManager.enemies.add(new Slime(new Vector(x, y)));
				break;
			}
		}
		this.selectedWave ++;
	}
	
	public void draw(Graphics g) {
		
		int minX = (int) (GameManager.cameraOffset.x / GameManager.tileSize - (MainPanel.WIDTH / 2) / GameManager.tileSize);
		int minY = (int) (GameManager.cameraOffset.y / GameManager.tileSize - (MainPanel.HEIGHT / 2) / GameManager.tileSize);
		
		int maxX = (int) (GameManager.cameraOffset.x / GameManager.tileSize + (MainPanel.WIDTH / 2) / GameManager.tileSize + 2);
		int maxY = (int) (GameManager.cameraOffset.y / GameManager.tileSize + (MainPanel.HEIGHT / 2) / GameManager.tileSize + 2);
		
		for(int i = Math.max(minY, 0); i < Math.min(maxY, map.length); i++) {
			for(int j = Math.max(minX, 0); j < Math.min(maxX, map[0].length); j++) {
				
				int x = (int) (j * GameManager.tileSize - GameManager.cameraOffset.x + MainPanel.WIDTH / 2);
				int y = (int) (i * GameManager.tileSize - GameManager.cameraOffset.y + MainPanel.HEIGHT / 2);
				
				if(x > -GameManager.tileSize && x < MainPanel.WIDTH + GameManager.tileSize && y > -GameManager.tileSize && y < MainPanel.HEIGHT + GameManager.tileSize && this.mapTileTexture != null) {
					
					g.drawImage(this.mapTileTexture[i][j], x, y, null);
					//g.drawImage(this.mapTileTexture[i][j], x, y, (int) GameManager.tileSize, (int) GameManager.tileSize, null);
					
					if(this.drawTileGrid) {
						g.drawRect(x, y, (int) GameManager.tileSize, (int) GameManager.tileSize);
					}
				}
				
				
			}
		}	
		
	}
	
	
	//the version of draw that you use when you want to edit
	public void drawEditing(Graphics g) {
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
			for(int[] i : this.enemyWaves.get(selectedWave)) {
				
				int enemyType = i[0];
				double x = i[2];
				double y = i[1];
				
				switch(enemyType) {
				
				case Enemy.SLIME:
					Slime s = new Slime(new Vector(x, y));
					s.draw(g);
					break;
					
				case Enemy.GOBLIN:
					break;
					
				case 0:
					break;
				
				}
				
			}
		}
		
		
	}
	
}
