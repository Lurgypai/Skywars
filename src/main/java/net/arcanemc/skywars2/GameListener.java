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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.arcanemc.corev2.game.GameChatFormat;
import net.arcanemc.corev2.game.GameUser;
import net.arcanemc.corev2.game.GameUser.Mode;
import net.arcanemc.corev2.game.State;
import net.arcanemc.corev2.game.events.GameEndEvent;
import net.arcanemc.corev2.game.events.GameStartEvent;
import net.arcanemc.corev2.user.User;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

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
		//restart
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
			if (e.getPlayer().getItemInHand().getType() == Material.CHEST) {
				net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(e.getPlayer().getItemInHand());
				NBTTagCompound tag = nmsItem.getTag();
				if(tag.hasKey("swtype")) {
					new Thread(() -> {
						String insert = "INSERT INTO chests (type, worldname, x, y, z), VALUES (?, ?, ?, ?, ?)";
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
					}).start();
			}
		}
	}
}
