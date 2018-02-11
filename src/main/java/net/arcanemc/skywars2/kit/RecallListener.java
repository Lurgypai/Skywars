package net.arcanemc.skywars2.kit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
	@EventHandler
	public void onPlayerLand(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player =  (Player)e.getEntity();
			if(plugin.getKitManager().playerHasKit(player, "recall")) {
				if(e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
				}
			}
		}
	}
}
