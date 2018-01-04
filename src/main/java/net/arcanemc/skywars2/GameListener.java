package net.arcanemc.skywars2;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import net.arcanemc.corev2.game.GameChatFormat;
import net.arcanemc.corev2.game.GameUser;
import net.arcanemc.corev2.game.GameUser.Mode;
import net.arcanemc.corev2.game.State;
import net.arcanemc.corev2.game.events.GameEndEvent;
import net.arcanemc.corev2.game.events.GameStartEvent;
import net.arcanemc.corev2.user.User;

public class GameListener implements Listener{
	
	Skywars plugin;
	
	GameListener(Skywars plugin_) {
		this.plugin = plugin_;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		switch(plugin.getGame().getState()) {
		case JOIN_WAIT:
			//actual players
			e.getPlayer().teleport(plugin.deserializeLocation("lobby"));
			break;
		case JOIN_CLOSED:
		case START_WAIT:
		case START:
		case END_WAIT:
			//spectators
			e.getPlayer().teleport(plugin.deserializeLocation("map.spawn"));
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		//set as spectator
		plugin.getGame().getGpAdmin().getGameUserByUUID(e.getEntity().getUniqueId()).get().setMode(Mode.SPECTATOR);
        User.resetState(e.getEntity());
		e.getEntity().setGameMode(GameMode.ADVENTURE);
		e.getEntity().setAllowFlight(true);
        Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(e.getEntity()));
        //teleport
        e.getEntity().teleport(plugin.deserializeLocation("map.spawn"));
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Player: " + e.getEntity().getDisplayName() + " has died.");
        //check if we have a winner
        if(plugin.getGame().getGpAdmin().getPlayers().stream().filter(u -> u.getMode() == Mode.PLAYER).toArray().length == 1) {
        	Bukkit.broadcastMessage("Winner winner chicken dinner");
        	plugin.getGame().setState(State.END_WAIT);
        }
        //appropriate message
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(plugin.getGame().getGpAdmin().getPlayers().stream().filter(u -> u.getMode() == Mode.PLAYER).toArray().length == 1) {
	    	Bukkit.broadcastMessage("Winner winner chicken dinner");
        	plugin.getGame().setState(State.END_WAIT);
	    }
	}
	
	//separate start and start wait, make startwait spawn them frozen, make start make them move
	@EventHandler
	public void onStart(GameStartEvent e) {
		//teleport players to spawns or hub place
		ArrayList<Integer> generated = new ArrayList<Integer>();
		while(generated.size() != 
				plugin.getGame().getGpAdmin().getPlayers().stream().filter(u -> u.getMode() == Mode.PLAYER).toArray().length) {
			int i = Skywars.rand.nextInt(Skywars.rand.nextInt(plugin.getNumSpawns() - 1) + 1);
			while(generated.contains(i)) {
				i = Skywars.rand.nextInt(Skywars.rand.nextInt(plugin.getNumSpawns() - 1) + 1);
			}
			generated.add(i);
		}
		int playerNum = 0;
		for(GameUser user : plugin.getGame().getGpAdmin().getPlayers()) {
			Player player = Bukkit.getPlayer(user.getId());
			if(user.getMode() == Mode.PLAYER) {
				player.teleport(plugin.deserializeLocation("map.spawn" + generated.get(playerNum)));
				playerNum++;
			} else if(user.getMode() == Mode.SPECTATOR) {
				player.teleport(plugin.deserializeLocation("lobby"));
			}
		}
		//fill chests
	}
	
	@EventHandler
	public void onEnd(GameEndEvent e) {
	}
	
	private void fillChests() {
		//obtain chest locations
		//temporary
		Location loc = plugin.deserializeLocation("lobby");
		if(loc.getBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) loc.getBlock();
			//get a tag that says its ring location (set when the chest is obtained for building)
			
		}
		//use loot level to generate chests with identified loot level gear
	}
}
