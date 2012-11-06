package me.BlahBerrys.rangedwolves;

import net.minecraft.server.EntityWolf;

import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Wolf;

public class WolfCollar {
	public static Wolf setColor(Wolf wolf, int color) {
		EntityWolf ent = (EntityWolf) ((CraftLivingEntity) wolf).getHandle();
		ent.setCollarColor(color);
		return wolf;
	}

	public static Wolf setColor(Wolf wolf, String colorStr) {
		int color = Integer.parseInt(colorStr.replaceAll("0x", ""), 16);
		EntityWolf ent = (EntityWolf) ((CraftLivingEntity) wolf).getHandle();
		ent.setCollarColor(color);
		return wolf;
	}

	public static Wolf setColor(Wolf wolf, int colorR, int colorG, int colorB) {
		int color = Integer.parseInt(ColorConverter.toHex(colorR, colorG, colorB), 16);
		EntityWolf ent = (EntityWolf) ((CraftLivingEntity) wolf).getHandle();
		ent.setCollarColor(color);
		return wolf;
	}

	public static int getColor(Wolf wolf) {
		EntityWolf ent = (EntityWolf) ((CraftLivingEntity) wolf).getHandle();
		return ent.getCollarColor();
	}
}