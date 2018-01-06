package net.arcanemc.skywars2.kit;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonKitExecutor implements KitExecutor {

	@Override
	public void execute(Player user) {
		for(Player player : user.getWorld().getPlayers()) {
			if(user.getLocation().distance(player.getLocation()) < 5) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15, 2));
			}
		}
	}
}
