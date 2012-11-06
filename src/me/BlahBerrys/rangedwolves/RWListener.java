package me.BlahBerrys.rangedwolves;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.EntityWolf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RWListener implements Listener {

	public static HashMap<String, Set<UUID>> WOLVES = new HashMap<String, Set<UUID>>();
	public static HashMap<String, UUID> COMMAND = new HashMap<String, UUID>();

	public void wolfMenu(Player player, Wolf wolf) {
		player.sendMessage(ChatColor.RED + " ========" + RWMain.getInstance().header + ChatColor.RED + "========");
		player.sendMessage(ChatColor.RED + "           Owner: " + ChatColor.WHITE + wolf.getOwner().getName());
		player.sendMessage(ChatColor.RED + "              Health: " + ChatColor.WHITE + wolf.getHealth() + ChatColor.RED + "/" + ChatColor.WHITE + wolf.getMaxHealth());
		player.sendMessage(ChatColor.RED + "             Breedable: " + ChatColor.WHITE + wolf.canBreed());
		player.sendMessage(ChatColor.RED + "                   Age: " + ChatColor.WHITE + wolf.getAge());
		player.sendMessage("     For a list of owner options");
		player.sendMessage("    sneak + (left) click your pet.");
		player.sendMessage(ChatColor.RED + " =============================");
	}

	public void petMenu(Player player, Wolf wolf) {
		player.sendMessage(ChatColor.RED + " ========" + RWMain.getInstance().header + ChatColor.RED + "========");
		player.sendMessage("         /rangedwolves callpets");
		player.sendMessage("        /rangedwolves wolf kill");
		player.sendMessage("        /rangedwolves wolf heal");
		player.sendMessage("   /rangedwolves wolf gift [player]");
		player.sendMessage("  /rangedwolves wolf collar [color]");
		player.sendMessage(ChatColor.RED + " =============================");
		COMMAND.put(player.getName(), wolf.getUniqueId());
	}

	public static void addWolf(Player player, UUID UUID) {
		if (!WOLVES.containsKey(player.getName())) {
			WOLVES.put(player.getName(), new HashSet<UUID>());
			if (!WOLVES.get(player.getName()).contains(UUID)) {
				WOLVES.get(player.getName()).add(UUID);
				RWData.saveData();
			}
		} else if (!WOLVES.get(player.getName()).contains(UUID)) {
			WOLVES.get(player.getName()).add(UUID);
			RWData.saveData();
		}
	}

	public static int getPetAmount(Player player) {
		if (WOLVES.containsKey(player.getName())) {
			return WOLVES.get(player.getName()).size();
		} else {
			return 0;
		}
	}

	private void setTarget(Player player, LivingEntity target) {
		for (Entity e : Bukkit.getServer().getWorld(player.getWorld().getName()).getEntities()) {
			if (e instanceof Wolf) {
				if (WOLVES.containsKey(player.getName())) {
					if (WOLVES.get(player.getName()).contains(e.getUniqueId())) {
						if (!((Wolf) e).isSitting()) {
							((EntityWolf) ((CraftWolf) ((Wolf) e)).getHandle()).b(((CraftLivingEntity) target).getHandle());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onWolfMenu(EntityDamageByEntityEvent event) {
		final Entity damager = event.getDamager();
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		final LivingEntity target = (LivingEntity) event.getEntity();

		if (damager instanceof Player) {
			final Player player = (Player) damager;
			if (target instanceof Wolf) {
				Wolf wolf = (Wolf) target;
				if (WOLVES.containsKey(player.getName())) {
					if (WOLVES.get(player.getName()).contains(wolf.getUniqueId())) {
						if (!(player.getItemInHand().getType() == RWMain.getInstance().command_item)) {
							if (!RWMain.getInstance().can_attack_own) {
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "You may not hurt your own wolves.");
								return;
							} else {
								return;
							}
						}

						if (!player.hasPermission("rangedwolves.wolf.menu")) {
							player.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return;
						}

						event.setCancelled(true);
						if (player.isSneaking()) {
							petMenu(player, wolf);
							return;
						} else {
							wolfMenu(player, wolf);
							return;
						}
					} else {
						if (!player.hasPermission("rangedwolves.attack")) {
							player.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
							return;
						}
						setTarget(player, target);
					}
				}
			}
		}

	}

	@EventHandler
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		final Entity damager = event.getDamager();

		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		final LivingEntity target = (LivingEntity) event.getEntity();

		Player player = null;

		if (damager instanceof Arrow && RWMain.getInstance().arrow) {
			if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((Arrow) event.getDamager()).getShooter();
		}
		if (damager instanceof Egg && RWMain.getInstance().egg) {
			if (!(((Egg) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((Egg) event.getDamager()).getShooter();
		}
		if (damager instanceof Fireball && RWMain.getInstance().fireball) {
			if (!(((Fireball) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((Fireball) event.getDamager()).getShooter();
		}
		if (damager instanceof SmallFireball && RWMain.getInstance().small_fireball) {
			if (!(((SmallFireball) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((SmallFireball) event.getDamager()).getShooter();
		}
		if (damager instanceof Snowball && RWMain.getInstance().snowball) {
			if (!(((Snowball) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((Snowball) event.getDamager()).getShooter();
		}
		if (damager instanceof ThrownPotion && RWMain.getInstance().potions) {
			if (!(((ThrownPotion) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			player = (Player) ((ThrownPotion) event.getDamager()).getShooter();
		}

		if (target == player || player == null) {
			return;
		}

		if (target instanceof Creeper) {
			if (!RWMain.getInstance().attack_creepers) {
				return;
			} else if (!player.hasPermission("rangedwolves.attack.creepers")) {
				return;
			}
		}

		if (!RWMain.getInstance().worlds.contains(player.getWorld().getName())) {
			return;
		}

		if (target instanceof Wolf) {
			Wolf wolf = (Wolf) target;
			if (WOLVES.containsKey(player.getName())) {
				if (!RWMain.getInstance().can_attack_own) {
					if (WOLVES.get(player.getName()).contains(wolf.getUniqueId())) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You may not hurt your own wolves.");
						return;
					}
				}
			}
		}

		if (target instanceof Player) {
			if (!player.hasPermission("rangedwolves.attack.pvp")) {
				return;
			}
			if (!RWMain.getInstance().wolf_pvp) {
				return;
			} else if (player.getWorld().getPVP()) {
				if (!RWMain.getInstance().wolf_pvp) {
					return;
				}
			}
		}

		if (!player.hasPermission("rangedwolves.attack")) {
			return;
		}

		setTarget(player, target);
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		Entity pet = event.getEntity();
		if (event.getOwner() instanceof Player) {
			Player player = (Player) event.getOwner();
			if (pet instanceof Wolf) {
				Wolf wolf = (Wolf) pet;

				if (!player.hasPermission("rangedwolves.wolf.tame")) {
					player.sendMessage(ChatColor.RED + "You do not have the permissions to do this.");
					return;
				}
				
				if (getPetAmount(player) >= RWMain.getInstance().max_wolves) {
					player.sendMessage(ChatColor.RED + "You already own " + getPetAmount(player) + " wolves. The max allowed is " + RWMain.getInstance().max_wolves + "!");
					event.setCancelled(true);
					return;
				}
				addWolf(player, wolf.getUniqueId());
				player.sendMessage(ChatColor.AQUA + "You've tamed a wolf.");
			}
		}
	}

	@EventHandler
	public void onWolfApeShit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (!(player.getLastDamageCause() instanceof Wolf)) {
			return;
		}

		Wolf wolfy = (Wolf) player.getLastDamageCause();
		CraftWolf wolf = (CraftWolf) wolfy;

		if (wolf.isSitting()) {
			wolf.setSitting(false);
		}

		if (wolf.getOwner() == null || wolf.getOwnerName() == null) {
			return;
		}
		if (wolf.getOwner() == player || wolf.getOwnerName().equalsIgnoreCase(player.getName())) {
			wolf.setTarget(null);
			wolf.setAngry(false);
			event.setCancelled(true);
		}
	}

	/*
	 * @EventHandler public void resetWolfAfterAttack(EntityDeathEvent event) {
	 * if (!(event.getEntity() instanceof LivingEntity)) { return; }
	 * 
	 * LivingEntity dead = (LivingEntity) event.getEntity();
	 * 
	 * if (!(dead.getLastDamageCause() instanceof Wolf)) { return; }
	 * 
	 * Wolf wolf = (Wolf) dead.getLastDamageCause();
	 * 
	 * if (wolf.getOwner() == null) { return; }
	 * 
	 * wolf.setSitting(true); wolf.setSitting(false);
	 * 
	 * }
	 */

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (WOLVES.containsKey(player.getName())) {
			if (WOLVES.get(player.getName()).isEmpty()) {
				WOLVES.remove(player.getName());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		LivingEntity dead = (LivingEntity) event.getEntity();
		if (!(dead instanceof Wolf)) {
			return;
		}
		Wolf wolf = (Wolf) dead;

		if (wolf.getOwner() == null) {
			return;
		}

		Player player = Bukkit.getServer().getPlayer(wolf.getOwner().getName());
		OfflinePlayer offPlayer = (OfflinePlayer) wolf.getOwner();

		if (!WOLVES.containsKey(player.getName())) {
			return;

		}

		if (player.isOnline()) {
			if (WOLVES.get(player.getName()).contains(wolf.getUniqueId())) {
				WOLVES.get(player.getName()).remove(wolf.getUniqueId());
				player.sendMessage(ChatColor.RED + "You've lost a wolf.");
				RWData.saveData();
			}
		} else if (WOLVES.get(offPlayer.getName()).contains(wolf.getUniqueId())) {
			WOLVES.get(offPlayer.getName()).remove(wolf.getUniqueId());
			RWData.saveData();
		}

		if (WOLVES.get(player.getName()).isEmpty()) {
			WOLVES.remove(player.getName());
			RWData.saveData();
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();

		loc.setX(loc.getX() + 1);

		if (!WOLVES.containsKey(player.getName())) {
			return;
		}

		for (Entity e : Bukkit.getServer().getWorld(player.getWorld().getName()).getEntities()) {
			if (e instanceof Wolf) {
				if (WOLVES.get(player.getName()).contains(e.getUniqueId())) {
					if (!((Wolf) e).isSitting()) {
						e.teleport(loc);
						((Wolf) e).setSitting(true);
						((Wolf) e).setSitting(false);
					}
				}
			}
		}
	}

}
