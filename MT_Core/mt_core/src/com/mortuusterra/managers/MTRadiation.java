package com.mortuusterra.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mortuusterra.MortuusTerraMain;
import com.mortuusterra.tasks.MTTimer;

public class MTRadiation {

	private MortuusTerraMain main;
	private final int geckRange = 10, generatorRange = 25;

	private HashMap<Player, MTTimer> map = new HashMap<Player, MTTimer>();
	private ArrayList<MTGeck> MTGeckList = new ArrayList<MTGeck>();
	private ArrayList<MTGenerator> MTGeneratorList = new ArrayList<MTGenerator>();

	//private MTRadiationDamageEvent event;

	public MTRadiation(MortuusTerraMain m) {
		this.main = m;
	}

	public void addPlayer(Player p) {
		// main.notifyConsol("test");
		map.put(p, new MTTimer(main, false, 20, 60) {

			@Override
			public void run() {
				// main.callEvent(event = new MTRadiationDamageEvent(p));
				/*
				 * if (event.isCancelled()) return;
				 */

				if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) {
					return;
				}
				// can the player be damaged by radiation?
				if (playerCheck(p)) {
					new BukkitRunnable() {

						@Override
						public void run() {
							if (p.getWorld().isThundering()) {
								p.damage(2.1);
								return;
							}
							if (p.getWorld().hasStorm()) {
								p.damage(2.1);
								return;
							}
							if (p.getLocation().getBlock().getType().equals(Material.WATER)) {
								p.damage(3.5);
								return;
							}
							p.damage(1.1);
						}
					}.runTask(main);
				}

			}
		});// wait 1 second(20 ticks) to run the task timer, and run the task
			// timer again after 3 seconds(60 ticks)
	}

	public void removePlayer(Player p) {
		map.get(p).stop();
		map.remove(p);
		return;
	}

	// check if the player can be damaged by radiation
	public boolean playerCheck(Player p) {
		if (isPlayerInRange(p)) {
			return false;
		}
		return true;
	}

	public void addGeck(MTGeck geck) {
		if (MTGeckList.contains(geck)) {
			return;
		}
		MTGeckList.add(geck);
		return;
	}

	public void removeGeck(MTGeck geck) {
		if (!MTGeckList.contains(geck)) {
			return;
		}
		MTGeckList.remove(geck);
		return;
	}

	public boolean containsGeck(MTGeck geck) {
		if (MTGeckList.contains(geck)) {
			return true;
		}
		return false;
	}

	public MTGeck getGeck(Location geckLocation) {
		for (MTGeck geck : MTGeckList) {
			if (geck.getGeckLocation().distance(geckLocation) == 0) {
				return geck;
			}
		}
		return null;
	}

	public void addGenerator(MTGenerator mtgenerator) {
		MTGeneratorList.add(mtgenerator);
	}

	public void removeGenerator(MTGenerator mtgenerator) {
		MTGeneratorList.remove(mtgenerator);
	}

	public MTGenerator getGenerator(Location mtgeneratorLocation) {
		for (MTGenerator gen : MTGeneratorList) {
			if (gen.getGeneratorLocation().distance(mtgeneratorLocation) == 0) {
				return gen;
			}
		}
		return null;
	}

	public boolean containsGenerator(MTGenerator mtgenerator) {
		if (MTGeneratorList.contains(mtgenerator)) {
			return true;
		}
		return false;
	}

	public boolean containsGenerator(Location location) {
		for (MTGenerator gen : MTGeneratorList) {
			if (gen.getGeneratorLocation().distance(location) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isInGeneratorRange(Block block) {
		for (MTGenerator gen : MTGeneratorList) {
			if (block.getLocation().distance(gen.getGeneratorLocation()) <= generatorRange) {
				if (!gen.isValid()) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public boolean canPlayerInteractGenerator(MTGenerator gen, Player player) {
		if (gen.getOwner().getUniqueId().equals(player.getUniqueId()) || gen.getAllowedPlayers().contains(player)) {
			return true;
		}
		return false;
	}

	public boolean isPlayerInRange(Player p) {
		for (MTGeck mtgeck : MTGeckList) {
			if (p.getLocation().distance(mtgeck.getGeckLocation()) <= geckRange && mtgeck.getPowered()
					&& mtgeck.isValid()) {
				p.sendMessage("You are in range of a GECK.");
				return true;
			}
		}
		p.sendMessage("You are not in range of a GECK.");
		return false;
	}

}
