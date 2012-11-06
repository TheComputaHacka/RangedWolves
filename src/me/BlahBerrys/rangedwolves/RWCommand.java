package me.BlahBerrys.rangedwolves;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class RWCommand implements CommandExecutor {

	public void sendHelp(Player p) {
		String title = "[" + ChatColor.WHITE + "RangedWolves Help" + ChatColor.RED + "]" + ChatColor.RED;
		p.sendMessage(ChatColor.RED + "===========" + title + "===========");
		p.sendMessage("  " + "            Author: BlahBerrys          ");
		p.sendMessage("  " + " (/rangedwolves can be accessed by /rw)");
		p.sendMessage("  " + "   For commands: /rangedwolves commands ");
		p.sendMessage("  " + "    For versions: /rangedwolves version  ");
		p.sendMessage(ChatColor.RED + "=======================================");
	}

	public void sendCommands(Player p) {
		String title = "[" + ChatColor.WHITE + "RangedWolves Commands" + ChatColor.RED + "]" + ChatColor.RED;
		p.sendMessage(ChatColor.RED + "=====" + title + "=====");
		p.sendMessage("    /rangedwolves help");
		p.sendMessage("    /rangedwolves version");
		p.sendMessage("    /rangedwolves commands");
		p.sendMessage("    /rangedwolves callpets");
		p.sendMessage("    /rangedwolves killpets");
		p.sendMessage("    /rangedwolves addwolf [player]");
		p.sendMessage("    /rangedwolves wolf [command]");
		p.sendMessage("    /rangedwolves search [radius]");
		p.sendMessage(ChatColor.RED + "================================");
	}

	public void giveWolf(Player player, Player p, UUID UUID) {
		boolean adult = true;
		int age = 0;
		int health = 0;
		int color = 0;

		for (Entity e : p.getWorld().getLivingEntities()) {
			if (e instanceof Wolf) {
				if (e.getUniqueId() == UUID) {
					adult = ((Wolf) e).isAdult();
					age = ((Wolf) e).getAge();
					health = ((Wolf) e).getHealth();
					color = WolfCollar.getColor((Wolf) e);
					((Wolf) e).setHealth(0);
					break;
				}
			}
		}

		CraftWolf wolf = (CraftWolf) player.getWorld().spawnEntity(player.getLocation().add(2, 0, 0), EntityType.WOLF);

		if (adult) {
			wolf.setAdult();
		}
		wolf.setAge(age);
		wolf.setHealth(health);
		WolfCollar.setColor(wolf, color);
		wolf.setAngry(false);
		wolf.setTarget(null);
		wolf.setOwner(player);
		wolf.setOwnerName(player.getName());
		RWListener.addWolf(player, wolf.getUniqueId());
		RWListener.WOLVES.get(p.getName()).remove(UUID);
		player.sendMessage(ChatColor.AQUA + "You were given a tamed wolf by " + p.getName() + "!");
		p.sendMessage(ChatColor.AQUA + "You successfully gave " + player.getName() + " a tamed wolf!");
		RWListener.COMMAND.remove(p.getName());
		if (RWListener.WOLVES.get(p.getName()).isEmpty()) {
			RWListener.WOLVES.remove(p.getName());
		}
		RWData.saveData();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandException {
		if (cmd.getName().equalsIgnoreCase("rangedwolves")) {

			if (!(sender instanceof Player)) {
				return true;
			}

			Player p = (Player) sender;

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("version")) {
					p.sendMessage(ChatColor.RED + " ========" + RWMain.getInstance().header + ChatColor.RED + "========");
					p.sendMessage("            Plugin Version: " + RWMain.getInstance().pluginVersion);
					p.sendMessage("            Config Version: " + RWMain.getInstance().configVersion);
					p.sendMessage("             Data Version: " + RWMain.getInstance().dataVersion);
					p.sendMessage(ChatColor.RED + " =============================");
					return true;
				}
				if (args[0].equalsIgnoreCase("help")) {
					sendHelp(p);
					return true;
				}
				if (args[0].equalsIgnoreCase("commands")) {
					sendCommands(p);
					return true;
				}

				if (args[0].equalsIgnoreCase("killpets")) {
					if (!p.hasPermission("rangedwolves.command.killpets")) {
						p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
						return true;
					}
					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;

					if (!WOLVES.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "You do have any pet wolves.");
						return true;
					} else if (WOLVES.get(p.getName()).isEmpty()) {
						WOLVES.remove(p.getName());
						p.sendMessage(ChatColor.RED + "You do have any pet wolves.");
						RWData.saveData();
						return true;
					}

					for (Entity e : Bukkit.getServer().getWorld(p.getWorld().getName()).getEntities()) {
						if (e instanceof Wolf) {
							if (WOLVES.get(p.getName()).contains(e.getUniqueId())) {
								((Wolf) e).setHealth(0);
								WOLVES.get(p.getName()).remove(e.getUniqueId());
							}
						}
					}
					p.sendMessage(ChatColor.AQUA + "All owned pets in this world killed.");
					return true;
				}

				if (args[0].equalsIgnoreCase("callpets")) {
					if (!p.hasPermission("rangedwolves.command.callpets")) {
						p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
						return true;
					}
					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;

					if (!WOLVES.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "You do have any pet wolves.");
						return true;
					} else if (WOLVES.get(p.getName()).isEmpty()) {
						WOLVES.remove(p.getName());
						p.sendMessage(ChatColor.RED + "You do have any pet wolves.");
						RWData.saveData();
						return true;
					}

					for (Entity e : Bukkit.getServer().getWorld(p.getWorld().getName()).getEntities()) {
						if (e instanceof Wolf) {
							if (WOLVES.get(p.getName()).contains(e.getUniqueId())) {
								if (((Wolf) e).isSitting()) {
									((Wolf) e).setSitting(false);
								}
								e.teleport(p.getLocation());
							}
						}
					}
					p.sendMessage(ChatColor.AQUA + "All owned pets in this world teleported to your location.");
					return true;
				}

				if (args[0].equalsIgnoreCase("addwolf")) {
					if (!p.hasPermission("rangedwolves.command.addwolf")) {
						p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
						return true;
					}
					CraftWolf wolf = (CraftWolf) p.getWorld().spawnEntity(p.getLocation().add(2, 0, 0), EntityType.WOLF);
					wolf.setAngry(false);
					wolf.setOwnerName(p.getName());
					wolf.setOwner(p);

					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;
					UUID UUID = wolf.getUniqueId();

					if (!WOLVES.containsKey(p.getName())) {
						WOLVES.put(p.getName(), new HashSet<UUID>());
						if (!WOLVES.get(p.getName()).contains(UUID)) {
							WOLVES.get(p.getName()).add(UUID);
							p.sendMessage(ChatColor.AQUA + "You successfully added a wolf to your collection!");
							RWData.saveData();
							return true;
						}
					} else if (!WOLVES.get(p.getName()).contains(UUID)) {
						WOLVES.get(p.getName()).add(UUID);
						p.sendMessage(ChatColor.AQUA + "You successfully added a wolf to your collection!");
						RWData.saveData();
						return true;
					} else {
						wolf.setHealth(0);
						p.sendMessage(ChatColor.RED + "Something went wrong, failed to add a wolf to your collection!");
						return true;
					}
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("addwolf")) {
					if (!p.hasPermission("rangedwolves.command.addwolf")) {
						p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
						return true;
					}
					Player player = Bukkit.getServer().getPlayer(args[1]);

					if (player == null || !player.isOnline()) {
						p.sendMessage(ChatColor.RED + "That player doesn't exist or is not online.");
						return true;
					}

					CraftWolf wolf = (CraftWolf) player.getWorld().spawnEntity(player.getLocation().add(2, 0, 0), EntityType.WOLF);
					wolf.setAngry(false);
					wolf.setOwnerName(player.getName());
					wolf.setOwner(player);

					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;
					UUID UUID = wolf.getUniqueId();

					if (!WOLVES.containsKey(player.getName())) {
						WOLVES.put(player.getName(), new HashSet<UUID>());
						if (!WOLVES.get(player.getName()).contains(UUID)) {
							WOLVES.get(player.getName()).add(UUID);
							player.sendMessage(ChatColor.AQUA + "You were given a tamed wolf by " + p.getName() + "!");
							p.sendMessage(ChatColor.AQUA + "You successfully gave " + player.getName() + " a tamed wolf!");
							RWData.saveData();
							return true;
						}
					} else if (!WOLVES.get(player.getName()).contains(UUID)) {
						WOLVES.get(player.getName()).add(UUID);
						player.sendMessage(ChatColor.AQUA + "You were given a tamed wolf by " + p.getName() + "!");
						p.sendMessage(ChatColor.AQUA + "You successfully gave " + player.getName() + " a tamed wolf!");
						RWData.saveData();
						return true;
					} else {
						wolf.setHealth(0);
						p.sendMessage(ChatColor.RED + "Something went wrong, failed to give " + player.getName() + " a tamed wolf.");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("search")) {
					if (!p.hasPermission("rangedwolves.command.search")) {
						p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
						return true;
					}
					int foundWolves = 0;
					Double a = RWMain.getInstance().search_limit;
					Double b = 0.0;

					if (!args[1].contains(".0D")) {
						p.sendMessage(ChatColor.RED + "Radius must be in 'double' formatt! (/rangedwolves search 15.0D)");
						return true;
					} else {
						b = Double.parseDouble(args[1]);
					}

					if (b > a) {
						p.sendMessage(ChatColor.RED + "Radius too large! Max allowed radius: " + a.toString().replaceAll(".0D", "") + "!");
						return true;
					}

					for (Entity e : p.getNearbyEntities(b, b, b)) {
						if (e instanceof Wolf) {
							if (((Wolf) e).getOwner() == null) {
								foundWolves++;
							}
						}
					}
					p.sendMessage(ChatColor.AQUA + "" + foundWolves + " untamed wolves found in a " + b.toString().replaceAll(".0D", "") + " block radius!");
					return true;
				}
				if (args[0].equalsIgnoreCase("wolf")) {
					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;
					HashMap<String, UUID> COMMAND = RWListener.COMMAND;
					Wolf wolf = null;

					if (!COMMAND.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "No pet selected, select one by sneaking + (left) clicking them.");
						return true;
					}

					if (!WOLVES.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "You have no pet wolves.");
						COMMAND.remove(p.getName());
						return true;
					}

					for (Entity e : Bukkit.getServer().getWorld(p.getWorld().getName()).getEntities()) {
						if (e instanceof Wolf) {
							if (e.getUniqueId() == COMMAND.get(p.getName())) {
								wolf = (Wolf) e;
								break;
							}
						}
					}

					if (wolf == null) {
						p.sendMessage(ChatColor.RED + "Could not find selected pet.");
						COMMAND.remove(p.getName());
						return true;
					}

					if (args[1].equalsIgnoreCase("heal")) {
						if (!p.hasPermission("rangedwolves.command.heal")) {
							p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return true;
						}
						wolf.setHealth(wolf.getMaxHealth());
						p.sendMessage(ChatColor.AQUA + "Your pet has been fully healed!");
						COMMAND.remove(p.getName());
						return true;
					}
					if (args[1].equalsIgnoreCase("kill")) {
						if (!p.hasPermission("rangedwolves.command.kill")) {
							p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return true;
						}
						wolf.setHealth(0);
						WOLVES.get(p.getName()).remove(wolf.getUniqueId());
						COMMAND.remove(p.getName());
						p.sendMessage(ChatColor.AQUA + "You've lost a wolf.");
						return true;
					}
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("wolf")) {
					HashMap<String, Set<UUID>> WOLVES = RWListener.WOLVES;
					HashMap<String, UUID> COMMAND = RWListener.COMMAND;
					Wolf wolf = null;

					if (!COMMAND.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "No pet selected, select one by sneaking + (left) clicking them.");
						return true;
					}

					if (!WOLVES.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "You have no pet wolves.");
						COMMAND.remove(p.getName());
						return true;
					}

					for (Entity e : Bukkit.getServer().getWorld(p.getWorld().getName()).getEntities()) {
						if (e instanceof Wolf) {
							if (e.getUniqueId() == COMMAND.get(p.getName())) {
								wolf = (Wolf) e;
								break;
							}
						}
					}
					if (wolf == null) {
						p.sendMessage(ChatColor.RED + "Could not find selected pet.");
						COMMAND.remove(p.getName());
						return true;
					}

					if (args[1].equalsIgnoreCase("collar")) {
						if (!p.hasPermission("rangedwolves.command.collar")) {
							p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return true;
						}
						int color = 0;
						try {
							int val = Integer.parseInt(args[2]);
							if (val < 0 || val > 15) {
								throw new NumberFormatException();
							}
							color = val;
						} catch (NumberFormatException e) {
							COMMAND.remove(p.getName());
							p.sendMessage(ChatColor.RED + "Color must be a number between 0-20! (/rangedwolves wolf collar [number])");
							return true;
						}
						p.sendMessage(ChatColor.AQUA + "Wolf's collar successfully changed!");
						WolfCollar.setColor(wolf, color);
						COMMAND.remove(p.getName());
					}

					if (args[1].equalsIgnoreCase("gift")) {
						if (!p.hasPermission("rangedwolves.command.gift")) {
							p.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return true;
						}
						Player player = Bukkit.getServer().getPlayer(args[2]);
						UUID UUID = wolf.getUniqueId();

						if (player == null || !player.isOnline()) {
							p.sendMessage(ChatColor.RED + "That player doesn't exist or is not online.");
							COMMAND.remove(p.getName());
							return true;
						}

						if (player == p) {
							p.sendMessage(ChatColor.RED + "You cannot gift a wolf to yourself.");
							COMMAND.remove(p.getName());
							return true;
						}

						giveWolf(player, p, UUID);
						return true;
					}
				}
			} else {
				sendHelp(p);
				return true;
			}
		}
		return false;
	}
}