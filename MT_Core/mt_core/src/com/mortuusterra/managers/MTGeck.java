package com.mortuusterra.managers;

import org.bukkit.Location;
import org.bukkit.World;

public class MTGeck {
	private Boolean powered = false;
	private boolean valid = false;
	private Location geckLocation;
	private World world;

	public MTGeck(Location l) {
		this.geckLocation = l;
		this.world = l.getWorld();
	}

	public World getWorld() {
		return world;
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

	public Location getGeckLocation() {
		return geckLocation;
	}

}
