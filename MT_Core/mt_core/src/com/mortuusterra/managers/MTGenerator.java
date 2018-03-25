package com.mortuusterra.managers;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.mortuusterra.MortuusTerraMain;

public class MTGenerator {
	private MTGenerator gen = this;
	private MortuusTerraMain main;
	private int u = 0, d = 10;
	private Boolean powered = false;
	private boolean valid = false;
	private Location generatorLocation;
	private Block furnace;
	private Block lamp;
	private World world;
	private Player owner;
	private ArrayList<Player> allowedPlayers = new ArrayList<Player>();
	private BukkitTask run;

	private boolean busy;

	public MTGenerator(Player owner, Location furnacLocation, MortuusTerraMain m) {
		this.main = m;
		this.generatorLocation = furnacLocation;
		this.furnace = furnacLocation.getBlock();
		this.lamp = furnace.getRelative(BlockFace.UP);
		this.owner = owner;
	}

	public boolean isBusy() {
		return busy;
	}

	public Boolean getPowered() {
		return powered;
	}

	public void setPowered(Boolean powered) {
		this.powered = powered;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Location getGeneratorLocation() {
		return generatorLocation;
	}

	public ArrayList<Player> getAllowedPlayers() {
		return allowedPlayers;
	}

	public Player getOwner() {
		return owner;
	}

	private Inventory getFurnaceInventory() {
		InventoryHolder ih = (InventoryHolder) furnace.getState();
		return ih.getInventory();
	}

	public Block getFurnace() {
		return furnace;
	}

	public void startWaitForCoal() {
		this.run = new BukkitRunnable() {
			@Override
			public void run() {
				if (isBroken()) {
					owner.sendMessage(
							"Your Generator is broken, it is not generating power anymore until you fix it!!");
					stopGenerator();

					return;
				}
				if (getFurnaceInventory() == null) {
					owner.sendMessage("Generator is missing the furnace, shutting down!");
					stopGenerator();
					return;
				}
				if (getFurnaceInventory().contains(Material.COAL)) {
					getFurnaceInventory().removeItem(new ItemStack(Material.COAL, 1));
				} else {
					owner.sendMessage("Generator needs coal, shutting down!");
					stopGenerator();
				}

			}
		}.runTaskTimerAsynchronously(main, 10, 600);
	}

	public boolean isBroken() {
		if (scan()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param gen
	 *            The generator that is to be scanned
	 * @return True if generator is still fully built. False if generator is not
	 *         still fully built.
	 */
	public boolean scan() {
		this.busy = true;
		new BukkitRunnable() {
			@Override
			public void run() {
				Block furnace = gen.getFurnace();
				if (furnace.getRelative(BlockFace.UP).getType().equals(Material.REDSTONE_LAMP_OFF)) {
					Block lamp = furnace.getRelative(BlockFace.UP);
					if (furnace.getRelative(BlockFace.DOWN).getType().equals(Material.SMOOTH_BRICK)) {
						Block centerGround = furnace.getRelative(BlockFace.DOWN);
						if (lamp.getRelative(BlockFace.UP).getType().equals(Material.STEP)) {
							Block centerTop = lamp.getRelative(BlockFace.UP);
							BlockFace[] squareFaces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
									BlockFace.SOUTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST,
									BlockFace.SOUTH_WEST };
							BlockFace[] ironFenceFaces = { BlockFace.NORTH_EAST, BlockFace.NORTH_WEST,
									BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST };
							for (BlockFace f : squareFaces) {
								if (!centerGround.getRelative(f).getType().equals(Material.SMOOTH_BRICK)
										|| !centerTop.getRelative(f).getType().equals(Material.STEP)) {
									gen.getOwner().sendMessage("Generator is not built corectly!");
									stopGenerator();
									gen.setValid(false);
									return;
								}
							}
							for (BlockFace f : ironFenceFaces) {
								if (!furnace.getRelative(f).getType().equals(Material.IRON_FENCE)
										|| !lamp.getRelative(f).getType().equals(Material.IRON_FENCE)) {
									gen.getOwner().sendMessage("Generator is not built corectly!");
									stopGenerator();
									gen.setValid(false);
									return;
								}
							}
						}
					}
				}
				// generator must be built correctly still, so set valid true and update lamp if
				// it is off
				gen.setValid(true);
				gen.updateLamp(Material.REDSTONE_LAMP_ON);
			}
		}.runTaskAsynchronously(main);
		this.busy = false;
		return valid;
	}

	public void startGenerator() {
		this.busy = true;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(u < 0 || u > 11) {
					cancel();
					//u = 0;
					//return;
				}
				if (u == 0) {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot up progress: " + ChatColor.YELLOW + "0 "
							+ ChatColor.GOLD + "%");
				} else {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot up progress: " + ChatColor.YELLOW + u
							+ "0 " + ChatColor.GOLD + "%");
				}
				u++;
				if (u == 10) {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot up progress: " + ChatColor.YELLOW
							+ "100 " + ChatColor.GOLD + "%");
					gen.getOwner()
							.sendMessage(ChatColor.BLUE + "Generator is now compleatly powered up, and awaiting coal!");
					gen.updateLamp(Material.REDSTONE_LAMP_ON);

					gen.setValid(true);
					gen.startWaitForCoal();
					cancel();
				}
			}

		}.runTaskTimerAsynchronously(main, 0, 30);
		u = 0;
		this.busy = false;
	}

	public void stopGenerator() {
		this.busy = true;
		this.run.cancel();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(d < 0 || d > 11) {
					//d = 10;
					cancel();
					//return;
				}
				if (d == 10) {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot down progress: " + ChatColor.YELLOW
							+ "100 " + ChatColor.GOLD + "%");
				} else {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot down progress: " + ChatColor.YELLOW + d
							+ "0 " + ChatColor.GOLD + "%");
				}
				d--;
				if (d <= 0) {
					gen.getOwner().sendMessage(ChatColor.BLUE + "Generator boot down progress: " + ChatColor.YELLOW
							+ "0 " + ChatColor.GOLD + "%");
					gen.getOwner().sendMessage(ChatColor.RED + "!!WARNING!! " + ChatColor.BLUE
							+ "Generator is now compleatly powered down!");
					gen.updateLamp(Material.REDSTONE_LAMP_OFF);

					gen.setValid(false);
					breakLamp();
					main.getRad().removeGenerator(gen);
					cancel();
					//return;
				}
			}

		}.runTaskTimerAsynchronously(main, 0, 30);
		d = 10;
		this.busy = false;
	}

	private void updateLamp(Material lamp) {
		this.busy = true;
		new BukkitRunnable() {
			@Override
			public void run() {
				gen.lamp.setType(lamp);
			}
		}.runTask(main);
		this.busy = false;
	}

	private void breakLamp() {
		this.busy = true;
		new BukkitRunnable() {

			@Override
			public void run() {
				gen.lamp.breakNaturally();
			}
		}.runTask(main);
		this.busy = false;
	}

}
