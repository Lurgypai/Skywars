package net.arcanemc.skywars2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import net.arcanemc.corev2.game.GameChatFormat;

public class MapDestructor {
	private Location center;
	private int radius;
	private static int delay = 10;
	private static int wait = 4 * 60;
	
	
	MapDestructor(Location center_, int radius_) {
		this.center = center_;
		this.radius = radius_;
	}
	
	public void start(Plugin plugin) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Destructor(center, radius), 0, 20);
	}
	
	private class Destructor implements Runnable {

		private int time = 0;
		private float dradius;
		private Location dcenter;
		
		Destructor(Location center_, float dradius_) {
			this.dcenter = center_;
			dradius = dradius_;
		}
		
		
		@Override
		public void run() {
			time++;
			if(time == wait) {
				Bukkit.getServer().broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "The map has begun disappearing!");
			}
			if(dradius != 0 ) {
				if(time >= wait)  {
					if(time % delay == 0) {
					Location offsetXP = new Location(dcenter.getWorld(), dcenter.getBlockX() + 1, dcenter.getBlockY(), dcenter.getBlockZ());
					Location offsetXN = new Location(dcenter.getWorld(), dcenter.getBlockX() - 1, dcenter.getBlockY(), dcenter.getBlockZ());
					Location offsetZP = new Location(dcenter.getWorld(), dcenter.getBlockX(), dcenter.getBlockY(), dcenter.getBlockZ() + 1);
					Location offsetZN = new Location(dcenter.getWorld(), dcenter.getBlockX(), dcenter.getBlockY(), dcenter.getBlockZ() - 1);
					destroycircle(dcenter);
					destroycircle(offsetXP);
					destroycircle(offsetXN);
					destroycircle(offsetZP);
					destroycircle(offsetZN);
						dradius--;
					}
				}
			}
		}
		
		private void destroycircle(Location cen) {
			for(float x_ = -dradius + 2; x_ != dradius - 1; x_++) {
				long z_ = Math.round(dradius * Math.sin(Math.acos(x_ / dradius)));
				for(int y =0; y!=256; y++) {
					Location loc1 = new Location(cen.getWorld(), cen.getX() + x_, y, cen.getZ() + z_);
					if(loc1.getBlock().getType() != Material.AIR) {
						loc1.getBlock().setType(Material.AIR);
					}
					Location loc2 = new Location(cen.getWorld(), cen.getX() + z_, y, cen.getZ() + x_);
					if(loc2.getBlock().getType() != Material.AIR) {
						loc2.getBlock().setType(Material.AIR);
					}
					Location loc3 = new Location(cen.getWorld(), cen.getX() - x_, y, cen.getZ() - z_);
					if(loc3.getBlock().getType() != Material.AIR) {
						loc3.getBlock().setType(Material.AIR);
					}
					Location loc4 = new Location(cen.getWorld(), cen.getX() - z_, y, cen.getZ() - x_);
					if(loc4.getBlock().getType() != Material.AIR) {
						loc4.getBlock().setType(Material.AIR);
					}
				}
			}
		}
		
	}
}
