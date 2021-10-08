package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import state.GameManager;

public class BuffManager {
	
	//buff ideas:
	//multishot
	//bullet bounce
	//crit chance / damage. Will require implementation of crit system
	//max health
	//max stamina / stamina regen
	//shop price decrease
	//damage resistance
	//+attack speed
	//elemental resistance and buffs: fire, ice, electricity, poison, nature
	//fire: dmg over time. buff will increase dmg over time
	//ice: slows enemies. buff will immobilize enemies
	//electricity: chain damage. buff will increase the distance between enemies that it can chain to, and increases the damage
	//poison: percentage max health damage. buff will make the damage last longer
	//nature: lifesteal per enemy. buff will increase the amount of lifesteal
	
	public static final int MULTISHOT = 0;
	public static final int BULLET_BOUNCE = 1;
	public static final int CRIT_CHANCE = 2;
	public static final int CRIT_DAMAGE = 3;
	public static final int MAX_HEALTH = 4;
	public static final int MAX_STAMINA = 5;
	public static final int SHOP_PRICE_DECREASE = 7;
	public static final int DAMAGE_RESISTANCE = 8;
	public static final int FIRE = 9;
	public static final int ICE = 10;
	public static final int ELECTRICITY = 11;
	public static final int POISON = 12;
	public static final int NATURE = 13;
	
	public static HashMap<Integer, String> buffDescriptions = new HashMap<Integer, String>() {{
		put(MULTISHOT, "Makes more bullets shoot out of your guns");
		put(BULLET_BOUNCE, "Makes your bullets bounce on walls");
		put(CRIT_CHANCE, "Increases your critical hit chance");
		put(CRIT_DAMAGE, "Increases your critical hit damage");
		put(MAX_HEALTH, "Increases your max health by " + HEALTH_PER_UPGRADE);
		put(MAX_STAMINA, "Increases your max stamina by " + STAMINA_PER_UPGRADE);
		put(SHOP_PRICE_DECREASE, "Decreases all item costs");
		put(DAMAGE_RESISTANCE, "Grants you +1 damage resistance");
		put(FIRE, "Makes you more resistant to fire, and all your fire imbued attacks become stronger");
		put(ICE, "Makes you more resistant to ice, and all your ice imbued attacks become stronger");
		put(ELECTRICITY, "Makes you more resistant to electricity, and all your electricity imbued attacks become stronger");
		put(POISON, "Makes you more resistant to poison, and all your poison imbued attacks become stronger");
		put(NATURE, "Makes you more resistant to nature, and all your nature imbued attacks become stronger");
	}};
	
	public static final int HEALTH_PER_UPGRADE = 50;
	public static final int STAMINA_PER_UPGRADE = 50;
	
	public HashMap<Integer, Integer> numBuffs;
	public Player player;	//this is the player that this buff manager is handling buffs for
	
	public BuffManager(Player player) {
		this.player = player;
		this.numBuffs = new HashMap<Integer, Integer>();
	}
	
	//gives a list of random buffs.
	//if the amount of buffs requested is larger than the number of existing buffs, then the amount returned will be equal to the amount of existing buffs.
	public static ArrayList<Integer> getRandomBuffs(int n){
		ArrayList<Integer> buffList = new ArrayList<Integer>(Arrays.asList(
			MULTISHOT, 
			BULLET_BOUNCE, 
			CRIT_CHANCE, 
			CRIT_DAMAGE, 
			MAX_HEALTH, 
			MAX_STAMINA, 
			SHOP_PRICE_DECREASE, 
			DAMAGE_RESISTANCE, 
			FIRE, 
			ICE, 
			ELECTRICITY, 
			POISON, 
			NATURE
		));
		
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
			player.critChance = (numBuffs.get(CRIT_CHANCE) + 1) * 0.1;
			break;
			
		case CRIT_DAMAGE:
			player.critMultiplier = (numBuffs.get(CRIT_DAMAGE) + 2);
			break;
			
		case MAX_HEALTH:
			player.maxHealth = 100 + numBuffs.get(MAX_HEALTH) * HEALTH_PER_UPGRADE;
			break;
			
		case MAX_STAMINA:
			player.maxStamina = 100 + numBuffs.get(MAX_STAMINA) * STAMINA_PER_UPGRADE; 
			break;
			
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

}
