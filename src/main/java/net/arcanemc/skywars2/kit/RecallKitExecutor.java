package net.arcanemc.skywars2.kit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RecallKitExecutor implements KitExecutor {

	RecallListener listener;
	
	public RecallKitExecutor(RecallListener l) {
		listener=l;
	}
	
	@Override
	public void execute(Player user) {
		Location loc = listener.getLoc(user.getUniqueId());
		user.setVelocity(new Vector());
		user.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
	}

}
