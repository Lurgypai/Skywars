package net.arcanemc.skywars2.kit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.arcanemc.skywars2.Skywars;

public class RecallListener implements Listener {
	
	Skywars plugin;
	private HashMap<UUID, Location> locations = new HashMap<UUID, Location>();
	
	public RecallListener(Skywars sky) {
		this.plugin = sky;
	}
	
	Location getLoc(UUID u) {
		return locations.get(u);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		if(plugin.getKitManager().playerHasKit(e.getPlayer(), "recall")) {
			Location below = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY() - 1, e.getFrom().getZ());
			if(below.getBlock().getType() != Material.AIR) {
				locations.put(e.getPlayer().getUniqueId(), e.getFrom());
			}
		}
	}
}
