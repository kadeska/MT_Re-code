package main.java.com.mortuusterra.util;

import java.io.File;
import java.io.IOException;

import main.java.com.mortuusterra.MortuusTerraMain;

public class MTfile {
	private MortuusTerraMain core;

	public MTfile(MortuusTerraMain core) {
		this.core = core;
	}

	public void createYmlFile(String name) {
		if (!core.getDataFolder().exists()) {
			core.getDataFolder().mkdir();
		}
		File file = new File(core.getDataFolder(), name + "yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
				core.notifyConsol("The " + name + ".yml file has been created");
			} catch (IOException e) {
				core.notifyConsol("Could not create the " + name + ".yml file");
				e.printStackTrace();
			}
		}
	}

	public void createJsonFile(String name) {
		if (!core.getDataFolder().exists()) {
			core.getDataFolder().mkdir();
		}
		File jsonFile = new File(name + ".json");
		try {
			if (!jsonFile.exists()) {
				jsonFile.createNewFile();
			}
		} catch (IOException e) {
			core.notifyConsol("Could not create the " + name + ".json file");
			e.printStackTrace();
		}
	}

	public void createTextFile(String name) {
		if (!core.getDataFolder().exists()) {
			core.getDataFolder().mkdir();
		}
		File textFile = new File(name + ".txt");
		try {

			if (!textFile.exists()) {
				textFile.createNewFile();
			}

		} catch (IOException e) {
			core.notifyConsol("Could not create the " + name + ".txt file");
			e.printStackTrace();
		}
	}
	
	public void saveFiles() {
		
	}
	
	public void loadFiles() {
		
	}
}
