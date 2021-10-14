package game;

import java.util.ArrayList;

public class TilesetManager {
	
	//this will handle all the tiles.
	//eventually, tiles will be split up into categories depending on which depth they spawn at.

	public static ArrayList<String> levelTiles;
	public static ArrayList<String> shopTiles;
	public static ArrayList<String> startTiles;
	public static ArrayList<String> endTiles;
	
	public static void loadTiles() {
		TilesetManager.levelTiles = new ArrayList<String>();
		TilesetManager.shopTiles = new ArrayList<String>();
		TilesetManager.startTiles = new ArrayList<String>();
		TilesetManager.endTiles = new ArrayList<String>();
		
		
		levelTiles.add("slime cave");
		levelTiles.add("slime cave parkour");
		
		shopTiles.add("slime cave loot");
		
		startTiles.add("tile start template.txt");
		
		endTiles.add("tile end template.txt");
	}
	
	public static void generateMap(Map outMap, ArrayList<Map> outLevelTiles, ArrayList<Integer> outLevelOffsets) {};	//TODO
	
	public static ArrayList<String> generateTiles() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(startTiles.get((int) (Math.random() * startTiles.size())));
		for(int i = 0; i < 5; i++) {
			if(i == 2) {
				out.add(shopTiles.get((int) (Math.random() * shopTiles.size())));
			}
			else {
				out.add(levelTiles.get((int) (Math.random() * levelTiles.size())));
			}
		}
		out.add(endTiles.get((int) (Math.random() * endTiles.size())));
		return out;
	}
	
}
