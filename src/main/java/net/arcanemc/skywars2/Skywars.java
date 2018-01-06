package net.arcanemc.skywars2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;

import net.arcanemc.corev2.database.ConnectionManager;
import net.arcanemc.corev2.database.ConnectionManager.RDBMS;
import net.arcanemc.corev2.game.Game;
import net.arcanemc.corev2.user.UserRetreiver;
import net.arcanemc.skywars2.kit.KitManager;

public class Skywars extends JavaPlugin {
	private ConnectionManager connManager = new ConnectionManager();
	private UserRetreiver uRet;
	private Connection conn;
	private Commands commands = new Commands();
	
	private Game game;
	private LootPool lootpool;
	private KitManager kitmanager;
	
	private int NUM_SPAWNS = this.getConfig().getInt("spawns");
	public static Random rand = new Random();
	
	static Skywars instance;
	static Skywars getInstance() {
		return instance;
	}
	
	public Skywars() {
		instance = this;
		lootpool = LootPool.obtain().get();
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public Connection getConn() {
		return this.conn;
	}
	
	public KitManager getKitManager() {
		return this.kitmanager;
	}
	
	public LootPool getLootPool() {
		return this.lootpool;
	}
	
	public int getNumSpawns() {
		return this.NUM_SPAWNS;
	}
	@Override
	public void onEnable() {
		//load and register maps from the config
		
		Bukkit.getServer().getPluginManager().registerEvents(new GameListener(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new KitManager(), this);
		
		connManager.initializeDatabasePool("skywars_main", RDBMS.POSTGRESQL, "app", "?_9KpC4q7&#Y/Rwu", "127.0.0.1", 5432, "prod", 2);
		conn = connManager.getPooledConnection("skywars_main");
		uRet = new UserRetreiver(connManager, "skywars_main");
		game = new Game(this, uRet, 2, NUM_SPAWNS, (u -> {
			//the example says to save u here. Do I need to save it to my own container, or put it in the game somehow,
			//or not do anything with it, or do something else completely?
			return true;
		}));
		//fill chests async
		runChests();
	}

	@Override
	public void onDisable() {	}
	
	//obtain location from config.yml
	public Location deserializeLocation(String locationName) {
		World world = Bukkit.getWorld(this.getConfig().getString(locationName + ".world"));
		double x = this.getConfig().getDouble(locationName + ".x");
		double y = this.getConfig().getDouble(locationName + ".y");
		double z = this.getConfig().getDouble(locationName + ".z");
		return new Location(world, x, y, z);
	}
	
	//generate numberOf numbers in range [min, max)
	public static ArrayList<Integer> generateRandomOrder(int numberOf, int min, int max) {
		ArrayList<Integer> generated = new ArrayList<Integer>();
		while(generated.size() != numberOf) {
			int i = (Skywars.rand.nextInt(max - min) + min);
			while(generated.contains(i)) {
				i = (Skywars.rand.nextInt(max - min) + min);
			}
			generated.add(i);
		}
		return generated;
	}
	
	/*
	 * Table Format
	 * Table Name "Chests"
	 * int type(0-3), char worldname, int x, int y, int z
	 */
	//load chests
	public void locateAndFillChests() {
		Bukkit.getLogger().info("[Skywars] Began loading chests.");
		String retrieveChest = "SELECT * FROM chests;";
		try {
			PreparedStatement stmnt = conn.prepareStatement(retrieveChest);
			ResultSet rs = stmnt.executeQuery();
			while(rs.next()) {
				int type = rs.getInt(1);
				String worldName = rs.getString(2);
				int x = rs.getInt(3);
				int y = rs.getInt(4);
				int z = rs.getInt(5);
				
				Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
				if(loc.getBlock().getType() == Material.CHEST) {
					Chest chest = (Chest)loc.getBlock().getState();
					lootpool.generateLoot(chest, LootPool.Level.values()[type]);
				}
			}
			Bukkit.getLogger().info("[Skywars] Chests loaded.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//locate and fill chest async (wait 10 secs and give up)
	public void runChests() {
		CompletableFuture<Void> chests = CompletableFuture.runAsync(() -> locateAndFillChests());
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			if(!chests.isDone()) {
				chests.cancel(true);
				Bukkit.getLogger().info("[Skywars] WARNING: Couldn't load Chests");
			}
		}, 200L);
	}
}

//locate and fill chests


//Plan of action
//when a player dies, handle messaging
//if one player is left after a player dies, win them the game
//teleport back to hub

//map manager to contain the maps, randomly select one