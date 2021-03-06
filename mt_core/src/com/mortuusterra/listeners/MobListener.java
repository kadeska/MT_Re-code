package com.mortuusterra.listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.mortuusterra.MortuusTerraMain;

public class MobListener implements Listener {
	public MortuusTerraMain main;

	public MobListener(MortuusTerraMain m) {
		main = m;
	}

	/**
	 * Enderman, Pig, Cow, Sheep, Horse, Squid, zombie. 
	 * Only these mobs should be allowed to spawn.
	 * @param e CreatureSpawnEvent
	 */
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER) && !e.getEntityType().equals(EntityType.ZOMBIE)
				&& !e.getEntityType().equals(EntityType.ENDERMAN) && !e.getEntityType().equals(EntityType.PIG)
				&& !e.getEntityType().equals(EntityType.COW) && !e.getEntityType().equals(EntityType.SHEEP)
				&& !e.getEntityType().equals(EntityType.HORSE) && !e.getEntityType().equals(EntityType.SQUID)) {

			e.setCancelled(true);
			e.getLocation().getWorld().spawn(e.getLocation(), Zombie.class);
		}
	}

	/**
	 * Chance for a Zombie to drop a sponge when it dies
	 * @param e EntityDeathEvent
	 */
	@EventHandler
	public void onZombieDie(EntityDeathEvent e) {
		if (e.getEntityType().equals(EntityType.ZOMBIE)) {
			if (new Random().nextInt(100) == 0) {
				e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.SPONGE));
			}
		}
	}
	
	/**
	 * Zombies can not burn from sunlight.
	 * @param e EntityCombustEvent
	 */
	@EventHandler
	public void onZombieBurn(EntityCombustEvent e) {
		if(e.getEntityType().equals(EntityType.ZOMBIE)) {
			if(e.getEntity().getLocation().getBlock().getLightFromSky() == 15) {
				e.getEntity().setFireTicks(0);
				e.setCancelled(true);
			}
		}
	}

}
