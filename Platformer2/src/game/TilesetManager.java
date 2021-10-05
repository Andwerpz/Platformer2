package game;

import java.util.ArrayList;

public class TilesetManager {
	
	//this will handle all the tiles.
	//eventually, tiles will be split up into categories depending on which depth they spawn at.

	public static ArrayList<Map> levelTiles;
	public static ArrayList<Map> startTiles;
	public static ArrayList<Map> endTiles;
	
	public static void loadTiles() {
		TilesetManager.levelTiles = new ArrayList<Map>();
		TilesetManager.startTiles = new ArrayList<Map>();
		TilesetManager.endTiles = new ArrayList<Map>();
		
		
		levelTiles.add(new Map("slime cave"));
	}
	
}
