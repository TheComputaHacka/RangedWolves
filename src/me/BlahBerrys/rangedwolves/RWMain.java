package me.BlahBerrys.rangedwolves;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class RWMain extends JavaPlugin {

	public static RWMain instance;

	public static Logger log = Logger.getLogger("Minecraft");
	public String title = "[RangedWolves] ";
	public String header = ChatColor.RED + "[" + ChatColor.WHITE + "RangedWolves" + ChatColor.RED + "]" + ChatColor.RESET;

	public int pluginVersion = 1;
	public int configVersion = 1;
	public int dataVersion = 1;
	public int max_wolves = 4;
	public Double search_limit = 50.0D;

	public boolean arrow = true;
	public boolean egg = true;
	public boolean fireball = true;
	public boolean small_fireball = true;
	public boolean snowball = true;
	public boolean potions = true;

	public boolean can_attack_own = false;
	public boolean wolf_pvp = true;
	public boolean attack_creepers = false;
	public Material command_item = Material.BONE;
	
	public List<String> worlds = null;

	public static RWMain getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		try {
			RWData.createFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		    log.info(title + "Ttracking statistics...");
		} catch (IOException e) {
			log.info(title + "Failed to track statistics.");
		}

		registerEvents();
		RWData.loadData();
		getConfigSettings();

		log.info(title + "has been enabled!");
	}

	@Override
	public void onDisable() {
		RWData.saveData();

		log.info(title + "has been disabled!");
	}

	protected void registerEvents() {
		// Register commands listener
		new RWCommand();
		RWCommand cccmd = new RWCommand();
		getCommand("rangedwolves").setExecutor(cccmd);

		// Register event listeners
		Bukkit.getServer().getPluginManager().registerEvents(new RWListener(), this);
	}

	public void getConfigSettings() {
		arrow = this.getConfig().getBoolean("Projectiles.Arrow", true);
		egg = this.getConfig().getBoolean("Projectiles.Egg", true);
		fireball = this.getConfig().getBoolean("Projectiles.Fireball", true);
		small_fireball = this.getConfig().getBoolean("Projectiles.Small-Fireball", true);
		snowball = this.getConfig().getBoolean("Projectiles.Snowball", true);
		potions = this.getConfig().getBoolean("Projectiles.Potions", true);

		max_wolves = this.getConfig().getInt("Owner.max-wolves", 4);
		can_attack_own = this.getConfig().getBoolean("Owner.can-attack-own", false);
		attack_creepers = this.getConfig().getBoolean("Owner.attack-creepers", false);
		
		worlds = this.getConfig().getStringList("Server.worlds");

		wolf_pvp = this.getConfig().getBoolean("Server.wolf-pvp", true);
		search_limit = this.getConfig().getDouble("Server.search-limit", 50.0D);

		configVersion = this.getConfig().getInt("version", 0);

		int id = 0;
		id = this.getConfig().getInt("Owner.command-item", 352);

		command_item = Material.getMaterial(id);
		
		if (!(configVersion == 1)) {
			log.info(title + "Corrupt config, plugin disabled!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
	}

}
