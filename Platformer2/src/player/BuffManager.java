package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import state.GameManager;

public class BuffManager {
	
	public static final int MULTISHOT = 0;
	public static final int BULLET_BOUNCE = 1;
	public static final int CRIT_CHANCE = 2;
	public static final int CRIT_DAMAGE = 3;
	public static final int MAX_HEALTH = 4;
	public static final int MAX_STAMINA = 5;
	public static final int IMMUNE_FRAMES = 6;
	public static final int SHOP_PRICE_DECREASE = 7;
	public static final int DAMAGE_RESISTANCE = 8;
	public static final int FIRE = 9;	//damage over time, increase dmg on upgrade
	public static final int ICE = 10;	//enemy slows down, immobilized on upgrade
	public static final int ELECTRICITY = 11;	//chain damage between enemies, upgrade will either increase range of chain, or increase num chained enemies
	public static final int POISON = 12;	//percentage max health damage over time, increase time of damage
	public static final int NATURE = 13;	//lifesteal for every enemy killed with nature dmg, upgrade will increase the amount of health gained from kill
	
	public static HashMap<Integer, String> buffDescriptions = new HashMap<Integer, String>() {{
		put(MULTISHOT, "Makes more bullets shoot out of your guns");
		put(BULLET_BOUNCE, "Makes your bullets bounce on walls");
		put(CRIT_CHANCE, "Increases your critical hit chance");
		put(CRIT_DAMAGE, "Increases your critical hit damage");
		put(MAX_HEALTH, "Increases your max health by " + HEALTH_PER_UPGRADE);
		put(MAX_STAMINA, "Increases your max stamina by " + STAMINA_PER_UPGRADE);
		put(IMMUNE_FRAMES, "Increases the immunity time after being hit");
		put(SHOP_PRICE_DECREASE, "Decreases all item costs");
		put(DAMAGE_RESISTANCE, "Grants you +1 damage resistance");
		put(FIRE, "Makes you more resistant to fire, and all your fire imbued attacks become stronger");
		put(ICE, "Makes you more resistant to ice, and all your ice imbued attacks become stronger");
		put(ELECTRICITY, "Makes you more resistant to electricity, and all your electricity imbued attacks become stronger");
		put(POISON, "Makes you more resistant to poison, and all your poison imbued attacks become stronger");
		put(NATURE, "Makes you more resistant to nature, and all your nature imbued attacks become stronger");
	}};
	
	public static HashMap<Integer, String> buffTitles = new HashMap<Integer, String>() {{
		put(MULTISHOT, "Multishot");
		put(BULLET_BOUNCE, "Bouncing Bullets");
		put(CRIT_CHANCE, "Critical Chance");
		put(CRIT_DAMAGE, "Critical Damage");
		put(MAX_HEALTH, "Health Upgrade");
		put(MAX_STAMINA, "Stamina Upgrade");
		put(IMMUNE_FRAMES, "Immune Time Upgrade");
		put(SHOP_PRICE_DECREASE, "Shop Sale");
		put(DAMAGE_RESISTANCE, "Damage Resistance");
		put(FIRE, "Fire Buff");
		put(ICE, "Ice Buff");
		put(ELECTRICITY, "Electricity Buff");
		put(POISON, "Poison Buff");
		put(NATURE, "Nature Buff");
	}};
	
	public static final int HEALTH_PER_UPGRADE = 50;
	public static final int STAMINA_PER_UPGRADE = 50;
	public static final int IMMUNE_FRAMES_PER_UPGRADE = 15;
	
	public HashMap<Integer, Integer> numBuffs;
	public Player player;	//this is the player that this buff manager is handling buffs for
	
	public BuffManager(Player player) {
		this.player = player;
		this.numBuffs = new HashMap<Integer, Integer>();
	}
	
	//gives a list of random buffs.
	//if the amount of buffs requested is larger than the number of existing buffs, then the amount returned will be equal to the amount of existing buffs.
	public static ArrayList<Integer> getRandomBuffs(int n){
		ArrayList<Integer> buffList = new ArrayList<Integer>();
		for(int i : buffTitles.keySet()) {
			buffList.add(i);
		}
		
		Collections.shuffle(buffList);
		
		ArrayList<Integer> ans = new ArrayList<Integer>();
		for(int i = 0; i < Math.min(n, buffList.size()); i++) {
			ans.add(buffList.get(i));
		}
		
		return ans;
	}
	
	public void getBuff(int buff) {
		numBuffs.put(buff, numBuffs.getOrDefault(buff, 0) + 1);
		
		switch(buff) {
		case MULTISHOT:
			break;
			
		case BULLET_BOUNCE:
			break;
			
		case CRIT_CHANCE:
			player.critChance = Player.BASE_CRIT_CHANCE + (double) numBuffs.get(CRIT_CHANCE) * 0.1;
			break;
			
		case CRIT_DAMAGE:
			player.critMultiplier = Player.BASE_CRIT_MULTIPlIER + numBuffs.get(CRIT_DAMAGE);
			break;
			
		case MAX_HEALTH:
			player.maxHealth = Player.BASE_HEALTH + numBuffs.get(MAX_HEALTH) * HEALTH_PER_UPGRADE;
			break;
			
		case MAX_STAMINA:
			player.maxStamina = Player.BASE_STAMINA + numBuffs.get(MAX_STAMINA) * STAMINA_PER_UPGRADE; 
			break;
			
		case IMMUNE_FRAMES:
			player.immuneTime = Player.BASE_IMMUNE_FRAMES + numBuffs.get(IMMUNE_FRAMES) * IMMUNE_FRAMES_PER_UPGRADE;
			
		case SHOP_PRICE_DECREASE:
			break;
			
		case DAMAGE_RESISTANCE:
			break;
			
		case FIRE:
			break;
			
		case ICE:
			break;
			
		case ELECTRICITY:
			break;
			
		case POISON:
			break;
			
		case NATURE:
			break;
		}
	}
	
	public void resetBuffs() {
		this.numBuffs = new HashMap<Integer, Integer>();
		
		
	}

}
