package net.arcanemc.skywars2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import net.arcanemc.corev2.database.ConnectionManager;
import net.arcanemc.corev2.database.ConnectionManager.RDBMS;
import net.arcanemc.corev2.game.Game;
import net.arcanemc.corev2.user.UserRetreiver;

public class Skywars extends JavaPlugin {
	private ConnectionManager connManager = new ConnectionManager();
	UserRetreiver uRet;
	private Game game;
	private int NUM_SPAWNS = this.getConfig().getInt("spawns");
	
	static Skywars instance;
	static Skywars getInstance() {
		return instance;
	}
	
	public Skywars() {
		instance = this;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public int getNumSpawns() {
		return this.NUM_SPAWNS;
	}
	@Override
	public void onEnable() {
		//load and register maps from the config
		
		connManager.initializeDatabasePool("skywars_main", RDBMS.POSTGRESQL, "app", "?_9KpC4q7&#Y/Rwu", "127.0.0.1", 5432, "prod", 2);
		uRet = new UserRetreiver(connManager, "skywars_main");
		game = new Game(this, uRet, 2, NUM_SPAWNS, (u -> {
			//the example says to save u here. Do I need to save it to my own container, or put it in the game somehow,
			//or not do anything with it, or do something else completely?
			return true;
		}));
	}

	@Override
	public void onDisable() {	}
	
	public Location deserializeLocation(String locationName) {
		World world = Bukkit.getWorld(this.getConfig().getString(locationName + ".world"));
		double x = this.getConfig().getDouble(locationName + ".x");
		double y = this.getConfig().getDouble(locationName + ".y");
		double z = this.getConfig().getDouble(locationName + ".z");
		return new Location(world, x, y, z);
	}
}


//Plan of action
//when a player dies, handle messaging
//if one player is left after a player dies, win them the game
//teleport back to hub

//map manager to contain the maps, randomly select one