package net.arcanemc.skywars2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.arcanemc.corev2.game.GameChatFormat;
import net.arcanemc.corev2.game.GameUser;
import net.arcanemc.corev2.game.GameUser.Mode;
import net.arcanemc.corev2.game.State;
import net.arcanemc.corev2.game.events.GameEndEvent;
import net.arcanemc.corev2.game.events.GameStartEvent;
import net.arcanemc.corev2.game.events.GameStartWaitEvent;
import net.arcanemc.corev2.user.User;
import net.arcanemc.skywars2.kit.Kit;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class GameListener implements Listener{
	
	Skywars plugin;
	
	GameListener(Skywars plugin_) {
		this.plugin = plugin_;
	}
	
	//WORLD INTERACTIONS ------
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		e.getPlayer().getInventory().clear();
		switch(plugin.getGame().getState()) {
		case JOIN_WAIT:
			//actual players
			e.getPlayer().teleport(plugin.deserializeLocation("lobby"));
			e.getPlayer().getInventory().setItem(4, Kit.generateItem("Kit Selector", "", 1, Material.SLIME_BALL, "selectkit"));
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
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Player " + e.getEntity().getDisplayName() + " has died.");
        //check if we have a winner
        if(plugin.getGame().getGpAdmin().getPlayers().stream().filter(u -> u.getMode() == Mode.PLAYER).toArray().length == 1) {
	    	Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() +
	    			Bukkit.getPlayer(plugin.getGame().getGpAdmin().getPlayers().get(0).getId()).getDisplayName() + " has WON!");
        	plugin.getGame().setState(State.END_WAIT);
        }
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(plugin.getGame().getGpAdmin().getPlayers().stream().filter(u -> u.getMode() == Mode.PLAYER).toArray().length == 1) {
	    	Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() +
	    			Bukkit.getPlayer(plugin.getGame().getGpAdmin().getPlayers().get(0).getId()).getDisplayName() + " has WON!");
        	plugin.getGame().setState(State.END_WAIT);
	    }
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if(plugin.getGame().getState() == State.START_WAIT) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
			if (e.getPlayer().getItemInHand().getType() == Material.CHEST) {
				net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(e.getPlayer().getItemInHand());
				NBTTagCompound tag = nmsItem.getTag();
				if(tag.hasKey("swtype")) {
					new Thread(() -> {
						String insert = "INSERT INTO chests (type, worldname, x, y, z) VALUES (?, ?, ?, ?, ?);";
						try {
							PreparedStatement stmnt = plugin.getConn().prepareStatement(insert);
							stmnt.setInt(1, tag.getInt("swtype"));
							stmnt.setString(2, e.getBlockPlaced().getWorld().getName());
							stmnt.setInt(3, e.getBlockPlaced().getX());
							stmnt.setInt(4, e.getBlockPlaced().getY());
							stmnt.setInt(5, e.getBlockPlaced().getZ());
							stmnt.executeUpdate();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					Bukkit.getLogger().info("[Skywars] Saved chest location.");
					}).start();
			}
		}
	}
	
	//DOWNTIME ---------
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		switch(plugin.getGame().getState()) {
		case JOIN_WAIT:
		case JOIN_CLOSED:
		case START_WAIT:
		case END_WAIT:
		case END:
		case HALT:
			e.setCancelled(true);
			break;
		}
	}
	
	@EventHandler
	public void click(InventoryClickEvent e) {
		switch(plugin.getGame().getState()) {
		case JOIN_WAIT:
		case JOIN_CLOSED:
		case START_WAIT:
		case END_WAIT:
		case END:
		case HALT:
			e.setCancelled(true);
			break;
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		switch(plugin.getGame().getState()) {
		case JOIN_WAIT:
		case JOIN_CLOSED:
		case START_WAIT:
		case END_WAIT:
		case END:
		case HALT:
			e.setCancelled(true);
			break;
		}
	}
	
	//GAME EVENTS -----------
	@EventHandler
	public void onStartWait(GameStartWaitEvent e) {
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
				//prepare player
				player.teleport(plugin.deserializeLocation("map.spawn" + generated.get(playerNum)));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				plugin.getKitManager().givePlayerKit(player);
				playerNum++;
			} else if(user.getMode() == Mode.SPECTATOR) {
				player.teleport(plugin.deserializeLocation("lobby"));
			}
		}
	}
	
	@EventHandler
	public void onStart(GameStartEvent e) {
		plugin.getMapD().start(plugin);
	}
	
	@EventHandler
	public void onEnd(GameEndEvent e) {
		Bukkit.getServer().shutdown();
	}
}
